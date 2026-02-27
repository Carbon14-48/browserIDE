package com.Glitch.browserIDE.security;

import com.Glitch.browserIDE.model.AuthProvider;
import com.Glitch.browserIDE.model.User;
import com.Glitch.browserIDE.model.UserOAuthAuth;
import com.Glitch.browserIDE.repository.UserOAuthAuthRepository;
import com.Glitch.browserIDE.repository.UserRepository;
import com.Glitch.browserIDE.service.RefreshTokenService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2RefreshToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Map;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final UserRepository userRepository;
    private final UserOAuthAuthRepository oauthRepository;
    private final RefreshTokenService refreshTokenService;
    private final OAuth2AuthorizedClientService authorizedClientService;

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {

        OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
        OAuth2User oauthUser = oauthToken.getPrincipal();
        String providerName = oauthToken.getAuthorizedClientRegistrationId();

        OAuth2AuthorizedClient authorizedClient = authorizedClientService.loadAuthorizedClient(
                providerName,
                oauthToken.getName());

        String providerAccessToken = null;
        String providerRefreshToken = null;
        LocalDateTime tokenExpiresAt = null;

        if (authorizedClient != null) {
            OAuth2AccessToken accessToken = authorizedClient.getAccessToken();
            OAuth2RefreshToken refreshToken = authorizedClient.getRefreshToken();

            providerAccessToken = accessToken.getTokenValue();
            providerRefreshToken = refreshToken != null ? refreshToken.getTokenValue() : null;

            Instant expiresAt = accessToken.getExpiresAt();
            tokenExpiresAt = expiresAt != null
                    ? LocalDateTime.ofInstant(expiresAt, ZoneId.systemDefault())
                    : null;
        }

        Map<String, Object> attributes = oauthUser.getAttributes();

        String providerUserId;
        String email;
        String username;
        String avatarUrl;
        String fullName;

        if ("google".equals(providerName)) {
            providerUserId = (String) attributes.get("sub");
            email = (String) attributes.get("email");
            fullName = (String) attributes.get("name");
            avatarUrl = (String) attributes.get("picture");
            username = email != null ? email.split("@")[0] : "user_" + providerUserId;
        } else {
            // github
            providerUserId = String.valueOf(attributes.get("id"));
            email = (String) attributes.get("email");
            username = (String) attributes.get("login");
            avatarUrl = (String) attributes.get("avatar_url");
            fullName = (String) attributes.get("name");
        }

        User user = findOrCreateUser(
                providerName,
                providerUserId,
                email,
                username,
                avatarUrl,
                fullName,
                providerAccessToken,
                providerRefreshToken,
                tokenExpiresAt);

        String refreshToken = refreshTokenService.createRefreshToken(user).getToken();

        Cookie refreshCookie = new Cookie("refreshToken", refreshToken);
        refreshCookie.setHttpOnly(true);
        refreshCookie.setSecure(false);
        refreshCookie.setPath("/");
        refreshCookie.setMaxAge(7 * 24 * 60 * 60);
        response.addCookie(refreshCookie);

        getRedirectStrategy().sendRedirect(request, response, "http://localhost:5173/auth/callback");
    }

    private User findOrCreateUser(
            String providerName,
            String providerUserId,
            String email,
            String username,
            String avatarUrl,
            String fullName,
            String providerAccessToken,
            String providerRefreshToken,
            LocalDateTime tokenExpiresAt) {

        AuthProvider provider = AuthProvider.valueOf(providerName.toUpperCase());

        Optional<UserOAuthAuth> existingOAuth = oauthRepository
                .findByProviderAndProviderUserId(provider, providerUserId);

        User user;

        if (existingOAuth.isPresent()) {
            UserOAuthAuth oauthAuth = existingOAuth.get();
            oauthAuth.setAccessToken(providerAccessToken);
            oauthAuth.setRefreshToken(providerRefreshToken);
            oauthAuth.setTokenExpiresAt(tokenExpiresAt);
            oauthRepository.save(oauthAuth);

            user = oauthAuth.getUser();
        } else {
            Optional<User> existingUser = userRepository.findByEmail(email);

            if (existingUser.isPresent()) {
                user = existingUser.get();
            } else {
                user = User.builder()
                        .username(username != null ? username : "user_" + providerUserId)
                        .email(email)
                        .fullName(fullName)
                        .avatarUrl(avatarUrl)
                        .emailVerified(true)
                        .enabled(true)
                        .build();
                user = userRepository.save(user);
            }

            UserOAuthAuth oauthAuth = UserOAuthAuth.builder()
                    .user(user)
                    .provider(provider)
                    .providerUserId(providerUserId)
                    .accessToken(providerAccessToken)
                    .refreshToken(providerRefreshToken)
                    .tokenExpiresAt(tokenExpiresAt)
                    .build();
            oauthRepository.save(oauthAuth);
        }

        return user;
    }
}