package dk.via.sep3.controller;

import dk.via.sep3.model.domain.User;
import dk.via.sep3.security.JwtUtil;
import dk.via.sep3.shared.auth.AuthResponseDTO;
import dk.via.sep3.shared.mapper.userMapper.UserMapper;
import dk.via.sep3.shared.registration.RegistrationDTO;
import dk.via.sep3.model.register.RegisterService;
import dk.via.sep3.shared.user.UserDTO;
import dk.via.sep3.model.utils.validation.Validator;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final UserMapper userMapper;
    private final RegisterService registerService;
    private final JwtUtil jwtUtil;
    private final Validator<RegistrationDTO> passwordsMatchValidator;

    public AuthController(UserMapper userMapper,
                          RegisterService registerService,
                          JwtUtil jwtUtil,
                          @Qualifier("passwordsMatchValidator") Validator<RegistrationDTO> passwordsMatchValidator) {
        this.userMapper = userMapper;
        this.registerService = registerService;
        this.jwtUtil = jwtUtil;
        this.passwordsMatchValidator = passwordsMatchValidator;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponseDTO> register(@RequestBody RegistrationDTO req) {
        // 0. Validate passwords match + password policy
        passwordsMatchValidator.validate(req);

        // 1. Map incoming DTO to domain
        User user = userMapper.mapRegistrationDTOToDomain(req);

        // 2. Delegate to model layer: validation, hashing, persistence
        User registeredUser = registerService.register(user);

        // 3. Generate JWT for the registered user
        String token = jwtUtil.generateToken(registeredUser.getUsername());
        // You might also include role, etc. inside the token if you want.

        // 4. Map domain user → DTO to avoid password and other internals
        UserDTO userDTO = userMapper.mapDomainToUserDTO(registeredUser);

        // 5. Wrap in AuthResponseDTO using explicit fields to avoid constructor resolution issues
        AuthResponseDTO response = new AuthResponseDTO(
                token,
                userDTO != null ? userDTO.getUsername() : null,
                userDTO != null ? userDTO.getName() : null,
                userDTO != null ? userDTO.getEmail() : null,
                userDTO != null ? userDTO.getPhoneNumber() : null,
                userDTO != null ? userDTO.getRole() : null
        );

        // 6. Return
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}
