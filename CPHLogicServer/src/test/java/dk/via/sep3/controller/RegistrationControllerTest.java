package dk.via.sep3.controller;

import dk.via.sep3.model.domain.User;
import dk.via.sep3.model.registration.RegistrationService;
import dk.via.sep3.shared.mapper.registrationMapper.RegistrationMapper;
import dk.via.sep3.shared.registration.CreateRegisterDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class RegistrationControllerTest {

  private RegistrationService registrationService;
  private RegistrationMapper registrationMapper;
  private RegistrationController controller;

  @BeforeEach
  void setup() {
    registrationService = mock(RegistrationService.class);
    registrationMapper = mock(RegistrationMapper.class);

    controller = new RegistrationController(registrationService, registrationMapper);
  }

  @Test
  void register_validInput_returnsCreatedUser() {
    // ----- Input DTO -----
    CreateRegisterDTO dto = new CreateRegisterDTO(
        "John Doe",
        "john",
        "password123",
        "12345678",
        "john@example.com"
    );

    // ----- Domain User (mapped from DTO) -----
    User user = new User();
    user.setUsername("john");

    // ----- Returned registered user -----
    User registered = new User();
    registered.setUsername("john");
    registered.setRole("READER");

    // ----- Define mock behavior -----
    when(registrationMapper.mapCreateRegisterDTOToDomain(dto)).thenReturn(user);
    when(registrationService.register(user)).thenReturn(registered);

    // ----- Act -----
    ResponseEntity<User> response = controller.register(dto);

    // ----- Assert -----
    assertEquals(201, response.getStatusCode().value());
    assertEquals("john", response.getBody().getUsername());
    assertEquals("READER", response.getBody().getRole());

    // ----- Verify method calls -----
    verify(registrationMapper).mapCreateRegisterDTOToDomain(dto);
    verify(registrationService).register(user);
  }
}
