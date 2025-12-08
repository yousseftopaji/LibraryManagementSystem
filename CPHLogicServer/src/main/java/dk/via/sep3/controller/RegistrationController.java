package dk.via.sep3.controller;

import dk.via.sep3.model.registration.RegistrationService;
import dk.via.sep3.shared.registration.CreateRegisterDTO;
import dk.via.sep3.shared.registration.RegisterDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/registration")
public class RegistrationController {
  private final RegistrationService registrationService;

  public RegistrationController(RegistrationService registrationService) {
    this.registrationService = registrationService;
  }

  @PostMapping
  public ResponseEntity<RegisterDTO> register(@RequestBody CreateRegisterDTO createRegisterDTO) {
    try {
      RegisterDTO registerDTO = registrationService.register(createRegisterDTO);
      return new ResponseEntity<>(registerDTO, HttpStatus.CREATED);
    } catch (IllegalArgumentException e) {
      // Validation failed
      return new ResponseEntity<>(new RegisterDTO(false, e.getMessage()), HttpStatus.BAD_REQUEST);
    } catch (Exception e) {
      // Unexpected error
      return new ResponseEntity<>(new RegisterDTO(false, "Registration failed. Please try again later."), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }
}
