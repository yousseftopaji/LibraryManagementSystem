package dk.via.sep3.controller;

import dk.via.sep3.shared.login.LoginDTO;
import dk.via.sep3.model.users.UserService;
import dk.via.sep3.shared.user.UserDTO;
import dk.via.sep3.security.JwtUtil;
import dk.via.sep3.security.PasswordService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final UserService userService;
    private final PasswordService passwordService;
    private final JwtUtil jwtUtil;

    public AuthController(UserService userService, PasswordService passwordService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.passwordService = passwordService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDTO req) {
        if (req.getUsername() == null || req.getPassword() == null) {
            return ResponseEntity.badRequest().body("username and password required");
        }

        UserDTO user = userService.getUserByUsername(req.getUsername());
        if (user == null || user.getPasswordHash() == null) {
            return ResponseEntity.status(401).body("invalid credentials");
        }

        if (!passwordService.matches(req.getPassword(), user.getPasswordHash())) {
            return ResponseEntity.status(401).body("invalid credentials");
        }

        String token = jwtUtil.generateToken(user.getUsername());
        return ResponseEntity.ok(token);
    }
}
