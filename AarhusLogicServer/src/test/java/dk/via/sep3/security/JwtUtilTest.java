package dk.via.sep3.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for JwtUtil
 * Tests JWT token generation, validation, and claim extraction
 */
class JwtUtilTest {

    private JwtUtil jwtUtil;
    private final String testSecret = "test-secret-key-for-testing-purposes";
    private final long testExpirationMs = 3600000; // 1 hour

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();

        // Set private fields using reflection
        ReflectionTestUtils.setField(jwtUtil, "secret", testSecret);
        ReflectionTestUtils.setField(jwtUtil, "expirationMs", testExpirationMs);

        // Initialize algorithm and verifier
        jwtUtil.init();
    }

    @Test
    @DisplayName("Should generate valid JWT token with username and role")
    void testGenerateToken_Success() {
        // Arrange
        String username = "johndoe";
        String role = "Reader";

        // Act
        String token = jwtUtil.generateToken(username, role);

        // Assert
        assertNotNull(token);
        assertFalse(token.isEmpty());
        assertTrue(token.split("\\.").length == 3); // JWT has 3 parts: header.payload.signature
    }

    @Test
    @DisplayName("Should extract correct username from token")
    void testExtractUsername_Success() {
        // Arrange
        String username = "johndoe";
        String role = "Reader";
        String token = jwtUtil.generateToken(username, role);

        // Act
        String extractedUsername = jwtUtil.extractUsername(token);

        // Assert
        assertEquals(username, extractedUsername);
    }

    @Test
    @DisplayName("Should extract correct role from token")
    void testExtractRole_Success() {
        // Arrange
        String username = "johndoe";
        String role = "Reader";
        String token = jwtUtil.generateToken(username, role);

        // Act
        String extractedRole = jwtUtil.extractRole(token);

        // Assert
        assertEquals(role, extractedRole);
    }

    @Test
    @DisplayName("Should get username from token using interface method")
    void testGetUsernameFromToken_Success() {
        // Arrange
        String username = "johndoe";
        String role = "Reader";
        String token = jwtUtil.generateToken(username, role);

        // Act
        String extractedUsername = jwtUtil.getUsernameFromToken(token);

        // Assert
        assertEquals(username, extractedUsername);
    }

    @Test
    @DisplayName("Should get role from token using interface method")
    void testGetRoleFromToken_Success() {
        // Arrange
        String username = "johndoe";
        String role = "Librarian";
        String token = jwtUtil.generateToken(username, role);

        // Act
        String extractedRole = jwtUtil.getRoleFromToken(token);

        // Assert
        assertEquals(role, extractedRole);
    }

    @Test
    @DisplayName("Should validate correct token")
    void testValidateToken_ValidToken() {
        // Arrange
        String username = "johndoe";
        String role = "Reader";
        String token = jwtUtil.generateToken(username, role);

        // Act
        boolean isValid = jwtUtil.validateToken(token);

        // Assert
        assertTrue(isValid);
    }

    @Test
    @DisplayName("Should reject invalid token")
    void testValidateToken_InvalidToken() {
        // Arrange
        String invalidToken = "invalid.token.here";

        // Act
        boolean isValid = jwtUtil.validateToken(invalidToken);

        // Assert
        assertFalse(isValid);
    }

    @Test
    @DisplayName("Should reject null token")
    void testValidateToken_NullToken() {
        // Act
        boolean isValid = jwtUtil.validateToken(null);

        // Assert
        assertFalse(isValid);
    }

    @Test
    @DisplayName("Should reject empty token")
    void testValidateToken_EmptyToken() {
        // Act
        boolean isValid = jwtUtil.validateToken("");

        // Assert
        assertFalse(isValid);
    }

    @Test
    @DisplayName("Should reject token signed with different secret")
    void testValidateToken_DifferentSecret() {
        // Arrange - Create token with different secret
        Algorithm differentAlgorithm = Algorithm.HMAC256("different-secret".getBytes());
        String token = JWT.create()
                .withSubject("johndoe")
                .withClaim("role", "Reader")
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + 3600000))
                .sign(differentAlgorithm);

        // Act
        boolean isValid = jwtUtil.validateToken(token);

        // Assert
        assertFalse(isValid);
    }

    @Test
    @DisplayName("Should reject expired token")
    void testValidateToken_ExpiredToken() {
        // Arrange - Create token that's already expired
        Algorithm algorithm = Algorithm.HMAC256(testSecret.getBytes());
        Date past = new Date(System.currentTimeMillis() - 10000); // 10 seconds ago
        Date morePast = new Date(System.currentTimeMillis() - 20000); // 20 seconds ago

        String expiredToken = JWT.create()
                .withSubject("johndoe")
                .withClaim("role", "Reader")
                .withIssuedAt(morePast)
                .withExpiresAt(past)
                .sign(algorithm);

        // Act
        boolean isValid = jwtUtil.validateToken(expiredToken);

        // Assert
        assertFalse(isValid);
    }

    @Test
    @DisplayName("Should throw exception when extracting username from invalid token")
    void testExtractUsername_InvalidToken() {
        // Arrange
        String invalidToken = "invalid.token.here";

        // Act & Assert
        assertThrows(JWTVerificationException.class, () -> {
            jwtUtil.extractUsername(invalidToken);
        });
    }

    @Test
    @DisplayName("Should throw exception when extracting role from invalid token")
    void testExtractRole_InvalidToken() {
        // Arrange
        String invalidToken = "invalid.token.here";

        // Act & Assert
        assertThrows(JWTVerificationException.class, () -> {
            jwtUtil.extractRole(invalidToken);
        });
    }

    @Test
    @DisplayName("Should generate different tokens for same user at different times")
    void testGenerateToken_DifferentTokensOverTime() throws InterruptedException {
        // Arrange
        String username = "johndoe";
        String role = "Reader";

        // Act
        String token1 = jwtUtil.generateToken(username, role);
        Thread.sleep(1100); // Wait 1.1 seconds to ensure different timestamp (JWT uses seconds)
        String token2 = jwtUtil.generateToken(username, role);

        // Assert
        assertNotEquals(token1, token2);

        // But both should contain same username and role
        assertEquals(jwtUtil.extractUsername(token1), jwtUtil.extractUsername(token2));
        assertEquals(jwtUtil.extractRole(token1), jwtUtil.extractRole(token2));
    }

    @Test
    @DisplayName("Should handle special characters in username")
    void testGenerateToken_SpecialCharactersInUsername() {
        // Arrange
        String username = "user@example.com";
        String role = "Reader";

        // Act
        String token = jwtUtil.generateToken(username, role);
        String extractedUsername = jwtUtil.extractUsername(token);

        // Assert
        assertEquals(username, extractedUsername);
    }

    @Test
    @DisplayName("Should handle different role types")
    void testGenerateToken_DifferentRoles() {
        // Arrange
        String username = "johndoe";
        String[] roles = {"Reader", "Librarian", "Admin", "READER", "reader"};

        // Act & Assert
        for (String role : roles) {
            String token = jwtUtil.generateToken(username, role);
            String extractedRole = jwtUtil.extractRole(token);
            assertEquals(role, extractedRole);
        }
    }

    @Test
    @DisplayName("Should include issued at and expiration dates in token")
    void testGenerateToken_ContainsTimestamps() {
        // Arrange
        String username = "johndoe";
        String role = "Reader";

        // Act
        String token = jwtUtil.generateToken(username, role);

        // Decode token to check timestamps
        DecodedJWT decoded = JWT.decode(token);
        Date issuedAt = decoded.getIssuedAt();
        Date expiresAt = decoded.getExpiresAt();
        long afterGeneration = System.currentTimeMillis();

        // Assert
        assertNotNull(issuedAt);
        assertNotNull(expiresAt);
        // IssuedAt should be close to current time (within 2 seconds)
        assertTrue(Math.abs(issuedAt.getTime() - afterGeneration) <= 2000,
                   "IssuedAt should be within 2 seconds of current time");
        assertTrue(expiresAt.getTime() > issuedAt.getTime());

        // Check that the expiration time difference is approximately equal to testExpirationMs
        long actualDifference = expiresAt.getTime() - issuedAt.getTime();
        long tolerance = 2000; // Allow 2 seconds tolerance for execution time
        assertTrue(Math.abs(actualDifference - testExpirationMs) <= tolerance,
                   "Expected expiration difference around " + testExpirationMs + "ms, but was " + actualDifference + "ms");
    }

    @Test
    @DisplayName("Should validate token is still valid before expiration")
    void testValidateToken_BeforeExpiration() {
        // Arrange
        String username = "johndoe";
        String role = "Reader";
        String token = jwtUtil.generateToken(username, role);

        // Act - Check immediately (should be valid)
        boolean isValid = jwtUtil.validateToken(token);

        // Assert
        assertTrue(isValid);
    }

    @Test
    @DisplayName("Should handle malformed token gracefully")
    void testValidateToken_MalformedToken() {
        // Arrange
        String[] malformedTokens = {
            "not.a.token",
            "only.two.parts",
            ".",
            "...",
            "header.payload", // Missing signature
            "a.b.c.d.e" // Too many parts
        };

        // Act & Assert
        for (String malformedToken : malformedTokens) {
            boolean isValid = jwtUtil.validateToken(malformedToken);
            assertFalse(isValid, "Should reject malformed token: " + malformedToken);
        }
    }

    @Test
    @DisplayName("Should extract username and role consistently")
    void testExtractClaims_Consistency() {
        // Arrange
        String username = "johndoe";
        String role = "Reader";
        String token = jwtUtil.generateToken(username, role);

        // Act - Extract multiple times
        String username1 = jwtUtil.extractUsername(token);
        String username2 = jwtUtil.getUsernameFromToken(token);
        String role1 = jwtUtil.extractRole(token);
        String role2 = jwtUtil.getRoleFromToken(token);

        // Assert - Should be consistent
        assertEquals(username1, username2);
        assertEquals(role1, role2);
        assertEquals(username, username1);
        assertEquals(role, role1);
    }

    @Test
    @DisplayName("Should handle very long username")
    void testGenerateToken_LongUsername() {
        // Arrange
        String longUsername = "a".repeat(500);
        String role = "Reader";

        // Act
        String token = jwtUtil.generateToken(longUsername, role);
        String extractedUsername = jwtUtil.extractUsername(token);

        // Assert
        assertEquals(longUsername, extractedUsername);
        assertTrue(jwtUtil.validateToken(token));
    }

    @Test
    @DisplayName("Should handle empty string username")
    void testGenerateToken_EmptyUsername() {
        // Arrange
        String emptyUsername = "";
        String role = "Reader";

        // Act
        String token = jwtUtil.generateToken(emptyUsername, role);
        String extractedUsername = jwtUtil.extractUsername(token);

        // Assert
        assertEquals(emptyUsername, extractedUsername);
        assertTrue(jwtUtil.validateToken(token));
    }

    @Test
    @DisplayName("Should handle null role gracefully")
    void testGenerateToken_NullRole() {
        // Arrange
        String username = "johndoe";
        String role = null;

        // Act
        String token = jwtUtil.generateToken(username, role);
        String extractedRole = jwtUtil.extractRole(token);

        // Assert
        assertNull(extractedRole);
    }

    @Test
    @DisplayName("Should initialize algorithm and verifier on init")
    void testInit_InitializesComponents() {
        // Arrange
        JwtUtil newJwtUtil = new JwtUtil();
        ReflectionTestUtils.setField(newJwtUtil, "secret", testSecret);
        ReflectionTestUtils.setField(newJwtUtil, "expirationMs", testExpirationMs);

        // Act
        newJwtUtil.init();

        // Assert - Should be able to generate and validate token
        String token = newJwtUtil.generateToken("testuser", "Reader");
        assertNotNull(token);
        assertTrue(newJwtUtil.validateToken(token));
    }
}

