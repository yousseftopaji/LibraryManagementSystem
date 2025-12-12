package dk.via.sep3.controller;

import dk.via.sep3.security.IJwtTokenProvider;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class UserController {
    private final IJwtTokenProvider jwtProvider;

    public UserController(IJwtTokenProvider jwtProvider) {
        this.jwtProvider = jwtProvider;
    }

    @GetMapping("/me")
    public ResponseEntity<?> me(@RequestHeader(value = "Authorization", required = false) String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return new ResponseEntity<>("Missing or invalid Authorization header", HttpStatus.UNAUTHORIZED);
        }
        String token = authorizationHeader.substring("Bearer ".length());
        if (!jwtProvider.validateToken(token)) {
            return new ResponseEntity<>("Invalid token", HttpStatus.UNAUTHORIZED);
        }
        String username = jwtProvider.getUsernameFromToken(token);
        return new ResponseEntity<>(username, HttpStatus.OK);
    }
}

