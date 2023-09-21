package com.msbeigi.journey;

import com.github.javafaker.Faker;
import com.msbeigi.auth.AuthenticationRequest;
import com.msbeigi.auth.AuthenticationResponse;
import com.msbeigi.customer.CustomerDTO;
import com.msbeigi.customer.CustomerRegistrationRequest;
import com.msbeigi.customer.Gender;
import com.msbeigi.jwt.JWTUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.EntityExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Random;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
public class AuthenticationIntegrationTest {

    private static final String AUTHENTICATION_PATH = "/api/v1/auth";
    private static final String CUSTOMER_PATH = "/api/v1/customers";
    private static final Random RANDOM = new Random();

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private JWTUtil jwtUtil;

    @Test
    void canLogin() {
        // Given
        Faker faker = new Faker();
        var name = faker.name().fullName();
        var email = faker.name().lastName().toLowerCase() + "-" + UUID.randomUUID() + "@gmail.com";
        int age = new Random().nextInt(20, 90);

        Gender gender = age % 2 == 0 ? Gender.MALE : Gender.FEMALE;

        // create registration request
        String password = "new-password";

        var customerRegistrationRequest =
                new CustomerRegistrationRequest(name, email, password, age, gender);

        AuthenticationRequest authenticationRequest = new AuthenticationRequest(
                email,
                password
        );

        webTestClient
                .post()
                .uri(AUTHENTICATION_PATH + "/login")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(authenticationRequest), AuthenticationRequest.class)
                .exchange()
                .expectStatus()
                .isUnauthorized();

        // send a post customerRegistrationRequest
        webTestClient.post()
                .uri(CUSTOMER_PATH)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(
                        Mono.just(customerRegistrationRequest),
                        CustomerRegistrationRequest.class
                )
                .exchange()
                .expectStatus()
                .isOk();

        EntityExchangeResult<AuthenticationResponse> result = webTestClient
                .post()
                .uri(AUTHENTICATION_PATH + "/login")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(authenticationRequest), AuthenticationRequest.class)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(new ParameterizedTypeReference<AuthenticationResponse>() {
                })
                .returnResult();

        String jwtToken = result.getResponseHeaders().get(HttpHeaders.AUTHORIZATION).get(0);

        AuthenticationResponse authenticationResponse = result.getResponseBody();

        CustomerDTO customerDTO = authenticationResponse.customerDTO();

        assertThat(jwtUtil.isTokenValid(
                jwtToken,
                customerDTO.username())).isTrue();

        assertThat(customerDTO.email()).isEqualTo(email);
        assertThat(customerDTO.age()).isEqualTo(age);
        assertThat(customerDTO.name()).isEqualTo(name);
        assertThat(customerDTO.username()).isEqualTo(email);
        assertThat(customerDTO.gender()).isEqualTo(gender);
        assertThat(customerDTO.roles()).isEqualTo(List.of("ROLE_USER"));
    }
}
