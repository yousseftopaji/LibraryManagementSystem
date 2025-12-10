package dk.via.sep3.controller;

import dk.via.sep3.model.registration.RegistrationService;
import dk.via.sep3.shared.registration.CreateRegisterDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class RegistrationController {
  private final RegistrationService registrationService;

  public RegistrationController(RegistrationService registrationService) {
    this.registrationService = registrationService;
  }

  @PostMapping("/register")
  public ResponseEntity<Void> register(@RequestBody CreateRegisterDTO createRegisterDTO) {
    registrationService.register(createRegisterDTO);
    return new ResponseEntity<>(HttpStatus.CREATED);
  }
}
