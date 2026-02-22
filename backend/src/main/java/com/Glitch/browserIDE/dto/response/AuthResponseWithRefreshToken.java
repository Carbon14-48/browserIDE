package com.Glitch.browserIDE.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponseWithRefreshToken {
    public AuthResponse authResponse;
    public String refreshToken;
}