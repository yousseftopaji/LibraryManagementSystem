package dk.via.sep3.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class JwtAuthenticationFilterTest {

  private IJwtTokenProvider jwtTokenProvider;
  private JwtAuthenticationFilter filter;

  private HttpServletRequest request;
  private HttpServletResponse response;
  private FilterChain filterChain;

  @BeforeEach
  void setUp() {
    jwtTokenProvider = mock(IJwtTokenProvider.class);
    filter = new JwtAuthenticationFilter(jwtTokenProvider);

    request = mock(HttpServletRequest.class);
    response = mock(HttpServletResponse.class);
    filterChain = mock(FilterChain.class);

    SecurityContextHolder.clearContext();
  }

  @AfterEach
  void tearDown() {
    SecurityContextHolder.clearContext();
  }

  // --------------------------------------------------
  // VALID TOKEN
  // --------------------------------------------------

  @Test
  void doFilterInternal_validToken_setsAuthentication() throws Exception {
    String token = "valid.jwt.token";

    when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
    when(jwtTokenProvider.validateToken(token)).thenReturn(true);
    when(jwtTokenProvider.getUsernameFromToken(token)).thenReturn("john");
    when(jwtTokenProvider.getRoleFromToken(token)).thenReturn("READER");

    filter.doFilterInternal(request, response, filterChain);

    Authentication auth = SecurityContextHolder.getContext().getAuthentication();

    assertNotNull(auth);
    assertEquals("john", auth.getPrincipal());
    assertTrue(
        auth.getAuthorities().stream()
            .anyMatch(a -> a.getAuthority().equals("ROLE_READER"))
    );

    verify(filterChain).doFilter(request, response);
  }

  // --------------------------------------------------
  // NO TOKEN
  // --------------------------------------------------

  @Test
  void doFilterInternal_noToken_doesNotAuthenticate() throws Exception {
    when(request.getHeader("Authorization")).thenReturn(null);

    filter.doFilterInternal(request, response, filterChain);

    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    assertNull(auth);

    verify(filterChain).doFilter(request, response);
    verifyNoInteractions(jwtTokenProvider);
  }

  // --------------------------------------------------
  // INVALID TOKEN
  // --------------------------------------------------

  @Test
  void doFilterInternal_invalidToken_doesNotAuthenticate() throws Exception {
    String token = "invalid.token";

    when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
    when(jwtTokenProvider.validateToken(token)).thenReturn(false);

    filter.doFilterInternal(request, response, filterChain);

    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    assertNull(auth);

    verify(filterChain).doFilter(request, response);
  }

  // --------------------------------------------------
  // MALFORMED HEADER
  // --------------------------------------------------

  @Test
  void doFilterInternal_headerWithoutBearer_doesNotAuthenticate() throws Exception {
    when(request.getHeader("Authorization")).thenReturn("Token abc123");

    filter.doFilterInternal(request, response, filterChain);

    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    assertNull(auth);

    verify(filterChain).doFilter(request, response);
  }
}
