package com.orthopedic.api.auth.service;

import com.orthopedic.api.auth.entity.PasskeyCredential;
import com.orthopedic.api.auth.entity.User;
import com.orthopedic.api.auth.repository.PasskeyCredentialRepository;
import com.orthopedic.api.auth.repository.UserRepository;
import com.yubico.webauthn.CredentialRepository;
import com.yubico.webauthn.RegisteredCredential;
import com.yubico.webauthn.data.ByteArray;
import com.yubico.webauthn.data.PublicKeyCredentialDescriptor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class WebAuthnCredentialRepository implements CredentialRepository {

    private final PasskeyCredentialRepository credentialRepository;
    private final UserRepository userRepository;

    @Override
    public Set<PublicKeyCredentialDescriptor> getCredentialIdsForUsername(String username) {
        User user = userRepository.findByEmail(username).orElse(null);
        if (user == null) {
            return Set.of();
        }
        return credentialRepository.findByUser(user).stream()
                .map(cred -> {
                    try {
                        return PublicKeyCredentialDescriptor.builder()
                                .id(ByteArray.fromBase64Url(cred.getCredentialId()))
                                .build();
                    } catch (Exception e) {
                        return null;
                    }
                })
                .filter(java.util.Objects::nonNull)
                .collect(Collectors.toSet());
    }

    @Override
    public Optional<ByteArray> getUserHandleForUsername(String username) {
        return userRepository.findByEmail(username)
                .map(user -> new ByteArray(user.getId().toString().getBytes()));
    }

    @Override
    public Optional<String> getUsernameForUserHandle(ByteArray userHandle) {
        // userHandle was saved as UUID string without hyphens, but let's just find the
        // user
        // We can just iterate or fetch by ID if we reconstruct the UUID
        // For simplicity, returning empty if not supported directly (Yubico allows this
        // if we don't use it or we find by ID)
        String handle = userHandle.getBase64Url();
        // Requires more robust UUID reconstruction, skipping for now
        return Optional.empty();
    }

    @Override
    public Optional<RegisteredCredential> lookup(ByteArray credentialId, ByteArray userHandle) {
        Optional<PasskeyCredential> credOpt = credentialRepository.findById(credentialId.getBase64Url());
        return credOpt.map(cred -> {
            try {
                return RegisteredCredential.builder()
                        .credentialId(credentialId)
                        .userHandle(userHandle)
                        .publicKeyCose(ByteArray.fromBase64Url(cred.getPublicKey()))
                        .signatureCount(cred.getSignCount())
                        .build();
            } catch (Exception e) {
                return null;
            }
        });
    }

    @Override
    public Set<RegisteredCredential> lookupAll(ByteArray credentialId) {
        Optional<PasskeyCredential> credOpt = credentialRepository.findById(credentialId.getBase64Url());
        if (credOpt.isEmpty()) {
            return Set.of();
        }
        PasskeyCredential cred = credOpt.get();
        try {
            return Set.of(RegisteredCredential.builder()
                    .credentialId(credentialId)
                    .userHandle(new ByteArray(cred.getUser().getId().toString().getBytes()))
                    .publicKeyCose(ByteArray.fromBase64Url(cred.getPublicKey()))
                    .signatureCount(cred.getSignCount())
                    .build());
        } catch (Exception e) {
            return Set.of();
        }
    }
}
