package dk.via.sep3.controller;

import dk.via.sep3.model.domain.User;
import dk.via.sep3.security.JwtUtil;
import dk.via.sep3.shared.auth.AuthResponseDTO;
import dk.via.sep3.shared.mapper.userMapper.UserMapper;
import dk.via.sep3.shared.registration.RegistrationDTO;
import dk.via.sep3.model.register.RegisterService;
import dk.via.sep3.shared.user.UserDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final UserMapper userMapper;
    private final RegisterService registerService;
    private final JwtUtil jwtUtil;

    public AuthController(UserMapper userMapper, RegisterService registerService, JwtUtil jwtUtil) {
        this.userMapper = userMapper;
        this.registerService = registerService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponseDTO> register(@RequestBody RegistrationDTO req) {
        // 1. Map incoming DTO to domain
        User user = userMapper.mapRegistrationDTOToDomain(req);

        // 2. Delegate to model layer: validation, hashing, persistence
        User registeredUser = registerService.register(user);

        // 3. Generate JWT for the registered user
        String token = jwtUtil.generateToken(registeredUser.getUsername());
        // You might also include role, etc. inside the token if you want.

        // 4. Map domain user → DTO to avoid password and other internals
        UserDTO userDTO = userMapper.mapDomainToUserDTO(registeredUser);

        // 5. Wrap in AuthResponseDTO
        AuthResponseDTO response = new AuthResponseDTO(token, userDTO);

        // 6. Return
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}

