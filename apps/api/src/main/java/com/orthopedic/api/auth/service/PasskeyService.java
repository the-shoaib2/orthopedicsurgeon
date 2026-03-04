package com.orthopedic.api.auth.service;

import com.yubico.webauthn.AssertionRequest;
import com.yubico.webauthn.AssertionResult;
import com.yubico.webauthn.RegistrationResult;
import com.yubico.webauthn.data.PublicKeyCredentialCreationOptions;

public interface PasskeyService {

    /**
     * Start WebAuthn registration, returning options to the client.
     */
    PublicKeyCredentialCreationOptions startRegistration(String email);

    /**
     * Finish WebAuthn registration.
     */
    RegistrationResult finishRegistration(String email, String credentialResponseJson);

    /**
     * Start WebAuthn authentication.
     */
    AssertionRequest startAuthentication(String email);

    /**
     * Finish WebAuthn authentication.
     */
    AssertionResult finishAuthentication(String email, String credentialResponseJson);
}
