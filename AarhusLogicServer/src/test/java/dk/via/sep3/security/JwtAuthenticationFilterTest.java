package dk.via.sep3.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for JwtAuthenticationFilter
 * Tests JWT-based authentication filtering
 */
@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    @Mock
    private IJwtTokenProvider jwtTokenProvider;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    private JwtAuthenticationFilter jwtAuthenticationFilter;

    private static final String VALID_TOKEN = "valid.jwt.token";
    private static final String INVALID_TOKEN = "invalid.jwt.token";

    @BeforeEach
    void setUp() {
        jwtAuthenticationFilter = new JwtAuthenticationFilter(jwtTokenProvider);
        // Clear security context before each test
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("Should authenticate user with valid JWT token")
    void testDoFilterInternal_ValidToken() throws ServletException, IOException {
        // Arrange
        when(request.getHeader("Authorization")).thenReturn("Bearer " + VALID_TOKEN);
        when(jwtTokenProvider.validateToken(VALID_TOKEN)).thenReturn(true);
        when(jwtTokenProvider.getUsernameFromToken(VALID_TOKEN)).thenReturn("johndoe");
        when(jwtTokenProvider.getRoleFromToken(VALID_TOKEN)).thenReturn("Reader");

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(authentication);
        assertEquals("johndoe", authentication.getPrincipal());
        assertTrue(authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_Reader")));

        verify(jwtTokenProvider, times(1)).validateToken(VALID_TOKEN);
        verify(jwtTokenProvider, times(1)).getUsernameFromToken(VALID_TOKEN);
        verify(jwtTokenProvider, times(1)).getRoleFromToken(VALID_TOKEN);
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    @DisplayName("Should not authenticate with invalid JWT token")
    void testDoFilterInternal_InvalidToken() throws ServletException, IOException {
        // Arrange
        when(request.getHeader("Authorization")).thenReturn("Bearer " + INVALID_TOKEN);
        when(jwtTokenProvider.validateToken(INVALID_TOKEN)).thenReturn(false);

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertNull(authentication);

        verify(jwtTokenProvider, times(1)).validateToken(INVALID_TOKEN);
        verify(jwtTokenProvider, never()).getUsernameFromToken(anyString());
        verify(jwtTokenProvider, never()).getRoleFromToken(anyString());
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    @DisplayName("Should not authenticate when no Authorization header present")
    void testDoFilterInternal_NoAuthorizationHeader() throws ServletException, IOException {
        // Arrange
        when(request.getHeader("Authorization")).thenReturn(null);

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertNull(authentication);

        verify(jwtTokenProvider, never()).validateToken(anyString());
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    @DisplayName("Should not authenticate when Authorization header doesn't start with Bearer")
    void testDoFilterInternal_NoBearerPrefix() throws ServletException, IOException {
        // Arrange
        when(request.getHeader("Authorization")).thenReturn("Basic sometoken");

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertNull(authentication);

        verify(jwtTokenProvider, never()).validateToken(anyString());
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    @DisplayName("Should handle exception during token validation gracefully")
    void testDoFilterInternal_ValidationException() throws ServletException, IOException {
        // Arrange
        when(request.getHeader("Authorization")).thenReturn("Bearer " + VALID_TOKEN);
        when(jwtTokenProvider.validateToken(VALID_TOKEN))
                .thenThrow(new RuntimeException("Token validation error"));

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertNull(authentication);

        // Filter chain should still continue
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    @DisplayName("Should extract token correctly from Bearer header")
    void testExtractToken_ValidBearerHeader() throws ServletException, IOException {
        // Arrange
        String token = "my.jwt.token.here";
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtTokenProvider.validateToken(token)).thenReturn(true);
        when(jwtTokenProvider.getUsernameFromToken(token)).thenReturn("user");
        when(jwtTokenProvider.getRoleFromToken(token)).thenReturn("Admin");

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(jwtTokenProvider, times(1)).validateToken(token);
    }

    @Test
    @DisplayName("Should set ROLE_ prefix for authority")
    void testDoFilterInternal_RolePrefixAdded() throws ServletException, IOException {
        // Arrange
        when(request.getHeader("Authorization")).thenReturn("Bearer " + VALID_TOKEN);
        when(jwtTokenProvider.validateToken(VALID_TOKEN)).thenReturn(true);
        when(jwtTokenProvider.getUsernameFromToken(VALID_TOKEN)).thenReturn("librarian");
        when(jwtTokenProvider.getRoleFromToken(VALID_TOKEN)).thenReturn("Librarian");

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(authentication);
        assertTrue(authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_Librarian")));
    }

    @Test
    @DisplayName("Should always call filter chain")
    void testDoFilterInternal_AlwaysCallsFilterChain() throws ServletException, IOException {
        // Test with valid token
        when(request.getHeader("Authorization")).thenReturn("Bearer " + VALID_TOKEN);
        when(jwtTokenProvider.validateToken(VALID_TOKEN)).thenReturn(true);
        when(jwtTokenProvider.getUsernameFromToken(VALID_TOKEN)).thenReturn("user");
        when(jwtTokenProvider.getRoleFromToken(VALID_TOKEN)).thenReturn("Reader");

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);
        verify(filterChain, times(1)).doFilter(request, response);

        // Reset and test with no token
        reset(filterChain);
        when(request.getHeader("Authorization")).thenReturn(null);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    @DisplayName("Should handle empty Bearer token")
    void testDoFilterInternal_EmptyBearerToken() throws ServletException, IOException {
        // Arrange
        when(request.getHeader("Authorization")).thenReturn("Bearer ");

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertNull(authentication);

        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    @DisplayName("Should handle different role types")
    void testDoFilterInternal_DifferentRoles() throws ServletException, IOException {
        // Test Admin role
        when(request.getHeader("Authorization")).thenReturn("Bearer admintoken");
        when(jwtTokenProvider.validateToken("admintoken")).thenReturn(true);
        when(jwtTokenProvider.getUsernameFromToken("admintoken")).thenReturn("admin");
        when(jwtTokenProvider.getRoleFromToken("admintoken")).thenReturn("Admin");

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(auth);
        assertTrue(auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_Admin")));

        // Clear context for next test
        SecurityContextHolder.clearContext();

        // Test Reader role
        when(request.getHeader("Authorization")).thenReturn("Bearer readertoken");
        when(jwtTokenProvider.validateToken("readertoken")).thenReturn(true);
        when(jwtTokenProvider.getUsernameFromToken("readertoken")).thenReturn("reader");
        when(jwtTokenProvider.getRoleFromToken("readertoken")).thenReturn("Reader");

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        auth = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(auth);
        assertTrue(auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_Reader")));
    }

    @Test
    @DisplayName("Should set username as principal")
    void testDoFilterInternal_UsernamePrincipal() throws ServletException, IOException {
        // Arrange
        when(request.getHeader("Authorization")).thenReturn("Bearer " + VALID_TOKEN);
        when(jwtTokenProvider.validateToken(VALID_TOKEN)).thenReturn(true);
        when(jwtTokenProvider.getUsernameFromToken(VALID_TOKEN)).thenReturn("testuser");
        when(jwtTokenProvider.getRoleFromToken(VALID_TOKEN)).thenReturn("Reader");

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(authentication);
        assertEquals("testuser", authentication.getPrincipal());
    }

    @Test
    @DisplayName("Should handle exception when extracting username")
    void testDoFilterInternal_ExceptionDuringUsernameExtraction() throws ServletException, IOException {
        // Arrange
        when(request.getHeader("Authorization")).thenReturn("Bearer " + VALID_TOKEN);
        when(jwtTokenProvider.validateToken(VALID_TOKEN)).thenReturn(true);
        when(jwtTokenProvider.getUsernameFromToken(VALID_TOKEN))
                .thenThrow(new RuntimeException("Token parsing error"));

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertNull(authentication);

        verify(filterChain, times(1)).doFilter(request, response);
    }
}

