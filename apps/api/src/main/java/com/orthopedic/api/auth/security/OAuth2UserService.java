package com.orthopedic.api.auth.security;

import com.orthopedic.api.auth.entity.OAuth2Account;
import com.orthopedic.api.auth.entity.Role;
import com.orthopedic.api.auth.entity.User;
import com.orthopedic.api.auth.exception.AuthException;
import com.orthopedic.api.auth.repository.OAuth2AccountRepository;
import com.orthopedic.api.auth.repository.RoleRepository;
import com.orthopedic.api.auth.repository.UserRepository;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
@Transactional
public class OAuth2UserService extends DefaultOAuth2UserService {
    private static final Logger log = LoggerFactory.getLogger(OAuth2UserService.class);

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final OAuth2AccountRepository oauth2AccountRepository;

    public OAuth2UserService(UserRepository userRepository,
            RoleRepository roleRepository,
            OAuth2AccountRepository oauth2AccountRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.oauth2AccountRepository = oauth2AccountRepository;
    }

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        try {
            return processOAuth2User(userRequest, oAuth2User);
        } catch (Exception ex) {
            log.error("Error processing OAuth2 user", ex);
            throw new OAuth2AuthenticationException(ex.getMessage());
        }
    }

    private OAuth2User processOAuth2User(OAuth2UserRequest userRequest, OAuth2User oAuth2User) {
        String provider = userRequest.getClientRegistration().getRegistrationId();
        Map<String, Object> attributes = oAuth2User.getAttributes();

        String providerId = attributes.get("sub").toString(); // For Google
        String email = (String) attributes.get("email");
        String name = (String) attributes.get("name");

        // 🔒 SECURITY: Verify email is confirmed by provider
        boolean emailVerified = Boolean.TRUE.equals(attributes.get("email_verified"));
        if (!emailVerified) {
            throw new AuthException("Email not verified by " + provider);
        }

        Optional<OAuth2Account> accountOptional = oauth2AccountRepository.findByProviderAndProviderId(provider,
                providerId);
        User user;

        if (accountOptional.isPresent()) {
            user = accountOptional.get().getUser();
        } else {
            // Check if user exists with this email
            user = userRepository.findByEmail(email).orElseGet(() -> registerNewOAuth2User(email, name));

            OAuth2Account newAccount = new OAuth2Account();
            newAccount.setUser(user);
            newAccount.setProvider(provider);
            newAccount.setProviderId(providerId);
            newAccount.setEmail(email);
            oauth2AccountRepository.save(newAccount);
        }

        return new CustomUserDetails(user);
    }

    private User registerNewOAuth2User(String email, String name) {
        User user = new User();
        user.setEmail(email);
        user.setPassword(""); // No password for OAuth users

        String[] names = name.split(" ", 2);
        user.setFirstName(names[0]);
        user.setLastName(names.length > 1 ? names[1] : "");
        user.setEnabled(true);

        // 🔒 SECURITY: Assign ADMIN for internal domains
        String roleName = "PATIENT";
        if (email.endsWith("@orthosync.com") || email.endsWith("@orthopedic.com")) {
            roleName = "ADMIN";
        }

        String finalRoleName = roleName;
        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new AuthException("Default role not found: " + finalRoleName));
        user.setRoles(Set.of(role));

        log.info("Registering new OAuth2 user: {} with role: {}", email, finalRoleName);
        return userRepository.save(user);
    }
}
