package dk.via.sep3.controller;

import dk.via.sep3.application.domain.User;
import dk.via.sep3.application.services.registration.RegistrationService;
import dk.via.sep3.mapper.registrationMapper.RegistrationMapper;
import dk.via.sep3.DTOs.registration.CreateRegisterDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class RegistrationController {
  private final RegistrationService registrationService;
  private final RegistrationMapper registrationMapper;
  private static final Logger logger = LoggerFactory.getLogger(RegistrationController.class);

  public RegistrationController(RegistrationService registrationService,
      RegistrationMapper registrationMapper) {
    this.registrationService = registrationService;
    this.registrationMapper = registrationMapper;
  }

  @PostMapping("/register")
  public ResponseEntity<User> register(@RequestBody CreateRegisterDTO createRegisterDTO)
  {
    logger.info("Registering user: {}", createRegisterDTO.getUsername());
    User user = registrationMapper.mapCreateRegisterDTOToDomain(createRegisterDTO);
    User registeredUser = registrationService.register(user);
    logger.info("User registered successfully: {}", createRegisterDTO.getUsername());
    //dont send registerDTO
    return ResponseEntity.status(HttpStatus.CREATED).body(registeredUser);
  }
}
