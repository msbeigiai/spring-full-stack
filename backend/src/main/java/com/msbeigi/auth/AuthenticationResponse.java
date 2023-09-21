package com.msbeigi.auth;

import com.msbeigi.customer.CustomerDTO;

public record AuthenticationResponse(
        String token,
        CustomerDTO customerDTO) {
}
