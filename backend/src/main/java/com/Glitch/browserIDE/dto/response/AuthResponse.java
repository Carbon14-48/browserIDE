package com.Glitch.browserIDE.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class AuthResponse {

    private String accessToken;

    private String token;

    @Builder.Default
    private String tokenType = "Bearer";

    private UserDTO user;

    private Long expiresIn;

    public AuthResponse(String token, UserDTO user) {
        this.token = token;
        this.user = user;
        this.tokenType = "Bearer";
    }
}
