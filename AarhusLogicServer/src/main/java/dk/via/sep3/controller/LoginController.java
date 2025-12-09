package dk.via.sep3.controller;

import dk.via.sep3.model.auth.AuthService;
import dk.via.sep3.shared.login.LoginRequest;
import dk.via.sep3.shared.login.LoginResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class LoginController {
    private final AuthService authService;

    public LoginController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping(value = "/login", consumes = "application/json", produces = "application/json")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        try {
            if (request == null || request.getUsername() == null || request.getPassword() == null) {
                return ResponseEntity.badRequest().body(new LoginResponse(null, false, "Invalid request", null));
            }

            LoginResponse response = authService.login(request);

            if (response == null) {
                // Auth service had an unexpected error
                return ResponseEntity.status(500).body(new LoginResponse(null, false, "Authentication service error", null));
            }

            if (!response.isSuccess()) {
                return ResponseEntity.status(401).body(response);
            }

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            // Log the exception in real code (omitted here to keep example concise)
            return ResponseEntity.status(500).body(new LoginResponse(null, false, "Internal server error", null));
        }
    }
}
