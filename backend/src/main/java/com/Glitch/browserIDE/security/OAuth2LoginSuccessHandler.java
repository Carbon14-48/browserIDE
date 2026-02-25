package com.Glitch.browserIDE.security;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.Glitch.browserIDE.model.User;
import com.Glitch.browserIDE.model.UserOAuthAuth;
import com.Glitch.browserIDE.repository.UserOAuthAuthRepository;
import com.Glitch.browserIDE.repository.UserRepository;
import com.Glitch.browserIDE.service.JwtService;
import com.Glitch.browserIDE.service.RefreshTokenService;
import com.Glitch.browserIDE.model.AuthProvider;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final UserRepository userRepository;
    private final UserOAuthAuthRepository oauthRepository;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {
        OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
        OAuth2User oauthUser = oauthToken.getPrincipal();
        String providerName = oauthToken.getAuthorizedClientRegistrationId();

        Map<String, Object> attributes = oauthUser.getAttributes();
        String providerUserId = String.valueOf(attributes.get("id"));
        String email = (String) attributes.get("email");
        String username = (String) attributes.get("login");// github use login
        String avatarUrl = (String) attributes.get("avatar_url");
        String fullName = (String) attributes.get("name");

        User user = findOrCreateUser(providerName, providerUserId, email, username, avatarUrl, fullName);

        String accessToken = jwtService.generateAccessToken(user.getId(), user.getEmail());
        String refreshToken = refreshTokenService.createRefreshToken(user).getToken();

        Cookie refreshCookie = new Cookie("refreshToken", refreshToken);
        refreshCookie.setHttpOnly(true);
        refreshCookie.setSecure(false);/// should be true in prod (https)
        refreshCookie.setPath("/");
        refreshCookie.setMaxAge(7 * 24 * 60 * 60);
        response.addCookie(refreshCookie);

        String redirectUrl = String.format("http://localhost:5173/oauth2/callback?token=%s", accessToken);

        getRedirectStrategy().sendRedirect(request, response, redirectUrl);

    }

    private User findOrCreateUser(String providerName, String providerUserId, String email, String username,
            String avatarUrl, String fullName) {
        AuthProvider provider = AuthProvider.valueOf(providerName.toUpperCase(null));

        Optional<UserOAuthAuth> existingOAuth = oauthRepository.findByProviderAndProviderUserId(provider,
                providerUserId);
        if (existingOAuth.isPresent()) {
            return existingOAuth.get().getUser();
        }

        Optional<User> existingUser = userRepository.findByEmail(email);

        User user;
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
                .build();
        oauthRepository.save(oauthAuth);
        return user;
    }

}
