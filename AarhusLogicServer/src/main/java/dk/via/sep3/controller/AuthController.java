package dk.via.sep3.controller;

import dk.via.sep3.model.domain.User;
import dk.via.sep3.security.JwtUtil;
import dk.via.sep3.shared.mapper.userMapper.UserMapper;
import dk.via.sep3.shared.registration.RegistrationDTO;
import dk.via.sep3.model.register.RegisterService;
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
    public ResponseEntity<User> register(@RequestBody RegistrationDTO req) {
      User user = userMapper.mapRegistrationDTOToDomain(req);
      User registeredUser = registerService.register(user);

      String token = jwtUtil.generateToken(user.getUsername());

      return new ResponseEntity<>(registeredUser, HttpStatus.OK);
    }
}

