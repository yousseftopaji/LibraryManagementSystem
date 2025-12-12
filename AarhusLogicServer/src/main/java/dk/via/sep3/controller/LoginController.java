package dk.via.sep3.controller;

import dk.via.sep3.mapper.userMapper.UserMapper;
import dk.via.sep3.model.auth.AuthService;
import dk.via.sep3.model.domain.User;
import dk.via.sep3.security.JwtUtil;
import dk.via.sep3.shared.login.LoginRequestDTO;
import dk.via.sep3.shared.login.LoginResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class LoginController {
    private final AuthService authService;
    private final UserMapper userMapper;
    private final JwtUtil jwtUtil;
    public LoginController(AuthService authService, UserMapper userMapper, JwtUtil jwtUtil) {
        this.authService = authService;
        this.userMapper = userMapper;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping(value = "/login", consumes = "application/json", produces = "application/json")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginRequestDTO request) {
       User user = userMapper.mapLoginRequestToDomain(request);

       User authenticatedUser = authService.login(user);
       String token = jwtUtil.generateToken(authenticatedUser.getUsername(),  authenticatedUser.getRole());
        LoginResponseDTO loginResponseDTO = new LoginResponseDTO();
        loginResponseDTO.setToken(token);
        loginResponseDTO.setUsername(authenticatedUser.getUsername());
        return  new ResponseEntity<>(loginResponseDTO, HttpStatus.OK);
    }
}
