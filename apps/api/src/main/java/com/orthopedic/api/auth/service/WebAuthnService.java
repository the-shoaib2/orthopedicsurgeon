package com.orthopedic.api.auth.service;

import com.orthopedic.api.auth.entity.User;
import com.yubico.webauthn.data.PublicKeyCredentialCreationOptions;
import com.yubico.webauthn.data.PublicKeyCredentialRequestOptions;

public interface WebAuthnService {
    PublicKeyCredentialCreationOptions startRegistration(User user);

    void finishRegistration(User user, String requestId, String responseJson);

    PublicKeyCredentialRequestOptions startAuthentication(String email);

    void finishAuthentication(String requestId, String responseJson);

    boolean hasPasskeys(User user);
}
