package com.savumi;

import com.savumi.auth.AuthResponse;
import com.savumi.auth.LoginRequest;
import com.savumi.auth.RegisterRequest;
import com.savumi.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.HttpStatus;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;


@SpringBootTest(
		webEnvironment = RANDOM_PORT,
		properties = {
		"security.jwt.secret=404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970",
		"security.jwt.expiration=86400000"}
)
@Testcontainers
class SavumiApplicationTests {

	@Container
	@ServiceConnection
	static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:16-alpine");

	@Autowired
	TestRestTemplate restTemplate;

	@Autowired
	UserRepository userRepository;

	@Test
	void contextLoads() {
	}

	@Test
	void shouldRegisterUserSuccessfully() {
		RegisterRequest registerRequest = new RegisterRequest("Jan", "jankowalski123@email.com", "test12345");
		var response = restTemplate.postForEntity("/api/auth/register", registerRequest, AuthResponse.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
		assertThat(response.getBody()).isNotNull();
		assertThat(response.getBody().getToken()).isNotBlank();
	}

	@Test
	void shouldReturnConflictForDuplicateEmail() {
		RegisterRequest registerRequest = new RegisterRequest("Duplicate", "duplicate123@email.com", "duplicate12345");
		var registerResponse = restTemplate.postForEntity("/api/auth/register", registerRequest, AuthResponse.class);
		assertThat(registerResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
		var duplicateResponse = restTemplate.postForEntity("/api/auth/register", registerRequest, AuthResponse.class);
		assertThat(duplicateResponse.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
	}
	@Test
	void shouldLoginSuccessfully() {
		RegisterRequest registerRequest = new RegisterRequest("Login", "login@email.com", "login123");
		var registerResponse = restTemplate.postForEntity("/api/auth/register", registerRequest, AuthResponse.class);
		assertThat(registerResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
		LoginRequest loginRequest = new LoginRequest("login@email.com", "login123");
		var loginResponse = restTemplate.postForEntity("/api/auth/login", loginRequest, AuthResponse.class);
		assertThat(loginResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(loginResponse.getBody()).isNotNull();
		assertThat(loginResponse.getBody().getToken()).isNotBlank();
	}
	@Test
	void shouldReturnUnauthorizedForWrongPassword() {
		RegisterRequest registerRequest = new RegisterRequest("WrongPassword", "wrongpass@email.com", "wrongpass123");
		var registerResponse = restTemplate.postForEntity("/api/auth/register", registerRequest, AuthResponse.class);
		assertThat(registerResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
		LoginRequest loginRequest = new LoginRequest("wrongpass@email.com", "badpassword");
		var loginResponse = restTemplate.postForEntity("/api/auth/login", loginRequest, AuthResponse.class);
		assertThat(loginResponse.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);

	}

	@Test
	void shouldVerifyPasswordIsHashed() {
		RegisterRequest registerRequest = new RegisterRequest("PasswordHashed", "passhashed@email.com", "passhashed123");
		var registerResponse = restTemplate.postForEntity("/api/auth/register", registerRequest, AuthResponse.class);
		assertThat(registerResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
		var user = userRepository.findByEmail(registerRequest.getEmail()).orElseThrow();
		assertThat(user.getPassword()).startsWith("$2a$");
		assertThat(user.getPassword()).isNotEqualTo(registerRequest.getPassword());
	}

}
