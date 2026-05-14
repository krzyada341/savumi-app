package com.savumi.auth;

import com.savumi.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import static org.assertj.core.api.Assertions.assertThat;

public class JwtServiceTest {

    private JwtService jwtService;

    private User testUser;

    private User testUser2;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "secret", "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970");
        ReflectionTestUtils.setField(jwtService, "expiration", 86400000L);
        testUser = User.builder().email("testuser@user.com").password("test123").build();
        testUser2 = User.builder().email("testuser2@user.com").password("test123").build();
    }

    @Test
    void shouldGenerateNonNullToken() {
        var token = jwtService.generateToken(testUser);
        assertThat(token).isNotBlank();
    }

    @Test
    void shouldReturnTrueWhenTokenIsValid() {
        var token = jwtService.generateToken(testUser);
        var result = jwtService.isTokenValid(token, testUser);
        assertThat(result).isTrue();
    }

    @Test
    void shouldExtractCorrectUsername() {
        var token = jwtService.generateToken(testUser);
        var result = jwtService.extractUsername(token);
        assertThat(result).isEqualTo(testUser.getUsername());
    }

    @Test
    void shouldReturnFalseForDifferentUser() {
        var token = jwtService.generateToken(testUser);
        var result = jwtService.isTokenValid(token, testUser2);
        assertThat(result).isFalse();
    }

    @Test
    void shouldReturnExpirationTimeInSeconds() {
        assertThat(jwtService.getExpirationTime()).isEqualTo(86400L);
    }

    @Nested
    class WithExpiredToken {
        @BeforeEach
        void setUp() {
            ReflectionTestUtils.setField(jwtService, "expiration", -1000L);
        }

        @Test
        void shouldReturnFalseWhenTokenIsExpired() {
            var token = jwtService.generateToken(testUser);
            var result = jwtService.isTokenValid(token, testUser);
            assertThat(result).isFalse();
        }
    }

}
