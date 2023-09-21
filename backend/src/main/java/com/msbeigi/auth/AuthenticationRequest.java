package com.msbeigi.auth;

public record AuthenticationRequest(
        String username,
        String password
) {
}
