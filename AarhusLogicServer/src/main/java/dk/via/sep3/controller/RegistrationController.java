package dk.via.sep3.controller;

import dk.via.sep3.shared.registration.RegistrationDTO;
import dk.via.sep3.model.users.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class RegistrationController {
    private final UserService userService;

    public RegistrationController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegistrationDTO req) {
        userService.register(req);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}

