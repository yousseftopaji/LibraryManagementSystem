package dk.via.sep3.controller;

import dk.via.sep3.application.domain.User;
import dk.via.sep3.application.services.registration.RegistrationService;
import dk.via.sep3.mapper.registrationMapper.RegistrationMapper;
import dk.via.sep3.DTOs.registration.CreateRegisterDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RegistrationControllerTest {

  private RegistrationService registrationService;
  private RegistrationMapper registrationMapper;
  private RegistrationController controller;

  @BeforeEach
  void setUp() {
    registrationService = mock(RegistrationService.class);
    registrationMapper = mock(RegistrationMapper.class);
    controller = new RegistrationController(registrationService, registrationMapper);
  }

  @Test
  void register_returnsCreatedUser() {
    // Arrange
    CreateRegisterDTO dto = new CreateRegisterDTO();
    dto.setUsername("john");

    User mappedUser = new User();
    mappedUser.setUsername("john");

    User registeredUser = new User();
    registeredUser.setUsername("john");

    when(registrationMapper.mapCreateRegisterDTOToDomain(dto))
        .thenReturn(mappedUser);

    when(registrationService.register(mappedUser))
        .thenReturn(registeredUser);

    // Act
    ResponseEntity<User> response = controller.register(dto);

    // Assert
    assertEquals(201, response.getStatusCode().value());
    assertNotNull(response.getBody());
    assertEquals("john", response.getBody().getUsername());

    verify(registrationMapper).mapCreateRegisterDTOToDomain(dto);
    verify(registrationService).register(mappedUser);
  }
}
