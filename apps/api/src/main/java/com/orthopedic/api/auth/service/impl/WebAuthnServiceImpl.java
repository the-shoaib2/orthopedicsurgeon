package com.orthopedic.api.auth.service.impl;

import com.orthopedic.api.auth.entity.User;
import com.orthopedic.api.auth.service.WebAuthnService;
import com.yubico.webauthn.data.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class WebAuthnServiceImpl implements WebAuthnService {

    private final com.orthopedic.api.auth.repository.PasskeyCredentialRepository credentialRepository;
    private static final java.security.SecureRandom random = new java.security.SecureRandom();

    @Override
    public PublicKeyCredentialCreationOptions startRegistration(User user) {
        byte[] challenge = new byte[32];
        random.nextBytes(challenge);

        return PublicKeyCredentialCreationOptions.builder()
                .rp(RelyingPartyIdentity.builder().id("orthopedic.api").name("Orthopedic Platform").build())
                .user(UserIdentity.builder()
                        .name(user.getEmail())
                        .displayName(user.getFirstName() + " " + user.getLastName())
                        .id(new ByteArray(user.getId().toString().getBytes()))
                        .build())
                .challenge(new ByteArray(challenge))
                .pubKeyCredParams(Collections.singletonList(PublicKeyCredentialParameters.builder()
                        .alg(COSEAlgorithmIdentifier.ES256)
                        .type(PublicKeyCredentialType.PUBLIC_KEY)
                        .build()))
                .build();
    }

    @Override
    public void finishRegistration(User user, String requestId, String responseJson) {
        // In a real implementation, we'd use RelyingParty.finishRegistration()
        // and save to credentialRepository
    }

    @Override
    public PublicKeyCredentialRequestOptions startAuthentication(String email) {
        byte[] challenge = new byte[32];
        random.nextBytes(challenge);

        return PublicKeyCredentialRequestOptions.builder()
                .challenge(new ByteArray(challenge))
                .rpId("orthopedic.api")
                .build();
    }

    @Override
    public void finishAuthentication(String requestId, String responseJson) {
        // Verification logic here
    }

    @Override
    public boolean hasPasskeys(User user) {
        return !credentialRepository.findByUser(user).isEmpty();
    }
}
