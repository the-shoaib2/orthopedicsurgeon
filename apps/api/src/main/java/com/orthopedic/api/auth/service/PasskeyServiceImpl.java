package com.orthopedic.api.auth.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.orthopedic.api.auth.entity.PasskeyCredential;
import com.orthopedic.api.auth.entity.User;
import com.orthopedic.api.auth.exception.AuthException;
import com.orthopedic.api.auth.repository.PasskeyCredentialRepository;
import com.orthopedic.api.auth.repository.UserRepository;
import com.yubico.webauthn.*;
import com.yubico.webauthn.data.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class PasskeyServiceImpl implements PasskeyService {

    private final RelyingParty relyingParty;
    private final UserRepository userRepository;
    private final PasskeyCredentialRepository credentialRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public PublicKeyCredentialCreationOptions startRegistration(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AuthException("User not found"));

        StartRegistrationOptions request = StartRegistrationOptions.builder()
                .user(UserIdentity.builder()
                        .name(user.getEmail())
                        .displayName(user.getFirstName() + " " + user.getLastName())
                        .id(new ByteArray(user.getId().toString().getBytes()))
                        .build())
                .build();

        PublicKeyCredentialCreationOptions options = relyingParty.startRegistration(request);

        // Store options in redis to verify later
        try {
            redisTemplate.opsForValue().set(
                    "webauthn_reg:" + email,
                    objectMapper.writeValueAsString(options),
                    Duration.ofMinutes(5));
        } catch (Exception e) {
            throw new AuthException("Failed to serialize registration options", e);
        }

        return options;
    }

    @Override
    @Transactional
    public RegistrationResult finishRegistration(String email, String credentialResponseJson) {
        String optionsJson = (String) redisTemplate.opsForValue().get("webauthn_reg:" + email);
        if (optionsJson == null) {
            throw new AuthException("Registration session expired or not found");
        }

        try {
            PublicKeyCredentialCreationOptions options = objectMapper.readValue(optionsJson,
                    PublicKeyCredentialCreationOptions.class);
            PublicKeyCredential<AuthenticatorAttestationResponse, ClientRegistrationExtensionOutputs> response = PublicKeyCredential
                    .parseRegistrationResponseJson(credentialResponseJson);

            FinishRegistrationOptions finishOptions = FinishRegistrationOptions.builder()
                    .request(options)
                    .response(response)
                    .build();

            RegistrationResult result = relyingParty.finishRegistration(finishOptions);

            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new AuthException("User not found"));

            PasskeyCredential cred = PasskeyCredential.builder()
                    .credentialId(result.getKeyId().getId().getBase64Url())
                    .user(user)
                    .publicKey(result.getPublicKeyCose().getBase64Url())
                    .signCount(response.getResponse().getParsedAuthenticatorData().getSignatureCounter())
                    .name("Passkey")
                    .build();

            credentialRepository.save(cred);
            redisTemplate.delete("webauthn_reg:" + email);

            return result;
        } catch (Exception e) {
            log.error("Failed to finish registration", e);
            throw new AuthException("Failed to finish webauthn registration", e);
        }
    }

    @Override
    public AssertionRequest startAuthentication(String email) {
        StartAssertionOptions request = StartAssertionOptions.builder()
                .username(email)
                .build();

        AssertionRequest options = relyingParty.startAssertion(request);

        try {
            redisTemplate.opsForValue().set(
                    "webauthn_auth:" + email,
                    objectMapper.writeValueAsString(options),
                    Duration.ofMinutes(5));
        } catch (Exception e) {
            throw new AuthException("Failed to serialize assertion options", e);
        }

        return options;
    }

    @Override
    @Transactional
    public AssertionResult finishAuthentication(String email, String credentialResponseJson) {
        String optionsJson = (String) redisTemplate.opsForValue().get("webauthn_auth:" + email);
        if (optionsJson == null) {
            throw new AuthException("Authentication session expired or not found");
        }

        try {
            AssertionRequest options = objectMapper.readValue(optionsJson, AssertionRequest.class);
            PublicKeyCredential<AuthenticatorAssertionResponse, ClientAssertionExtensionOutputs> response = PublicKeyCredential
                    .parseAssertionResponseJson(credentialResponseJson);

            FinishAssertionOptions finishOptions = FinishAssertionOptions.builder()
                    .request(options)
                    .response(response)
                    .build();

            AssertionResult result = relyingParty.finishAssertion(finishOptions);

            if (result.isSuccess()) {
                Optional<PasskeyCredential> credOpt = credentialRepository
                        .findById(result.getCredential().getCredentialId().getBase64Url());
                credOpt.ifPresent(cred -> {
                    cred.setSignCount(result.getSignatureCount());
                    cred.setLastUsed(java.time.LocalDateTime.now());
                    credentialRepository.save(cred);
                });
                redisTemplate.delete("webauthn_auth:" + email);
            }

            return result;
        } catch (Exception e) {
            log.error("Failed to finish authentication", e);
            throw new AuthException("Failed to finish webauthn authentication", e);
        }
    }
}
