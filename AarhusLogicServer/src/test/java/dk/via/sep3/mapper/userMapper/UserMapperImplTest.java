package dk.via.sep3.mapper.userMapper;

import dk.via.sep3.DTOUser;
import dk.via.sep3.model.domain.User;
import dk.via.sep3.shared.auth.AuthResponseDTO;
import dk.via.sep3.shared.login.LoginRequestDTO;
import dk.via.sep3.shared.registration.RegistrationDTO;
import dk.via.sep3.shared.user.UserDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for UserMapperImpl
 * Tests mapping between DTOs and domain objects
 */
class UserMapperImplTest {

    private UserMapperImpl userMapper;

    @BeforeEach
    void setUp() {
        userMapper = new UserMapperImpl();
    }

    // ========== mapRegistrationDTOToDomain Tests ==========

    @Test
    @DisplayName("Should map RegistrationDTO to User domain")
    void testMapRegistrationDTOToDomain_Success() {
        // Arrange
        RegistrationDTO dto = new RegistrationDTO();
        dto.setFullName("John Doe");
        dto.setUsername("johndoe");
        dto.setPassword("SecurePass123");
        dto.setEmail("john@example.com");
        dto.setPhoneNumber("12345678");

        // Act
        User user = userMapper.mapRegistrationDTOToDomain(dto);

        // Assert
        assertNotNull(user);
        assertEquals("John Doe", user.getName());
        assertEquals("johndoe", user.getUsername());
        assertEquals("SecurePass123", user.getPassword());
        assertEquals("john@example.com", user.getEmail());
        assertEquals("12345678", user.getPhoneNumber());
    }

    @Test
    @DisplayName("Should return null when RegistrationDTO is null")
    void testMapRegistrationDTOToDomain_NullDTO() {
        // Act
        User user = userMapper.mapRegistrationDTOToDomain(null);

        // Assert
        assertNull(user);
    }

    // ========== mapDomainToUserDTO Tests ==========

    @Test
    @DisplayName("Should map User domain to UserDTO")
    void testMapDomainToUserDTO_Success() {
        // Arrange
        User user = new User();
        user.setName("John Doe");
        user.setUsername("johndoe");
        user.setPassword("$2a$10$hashedPassword");
        user.setEmail("john@example.com");
        user.setPhoneNumber("12345678");
        user.setRole("Reader");

        // Act
        UserDTO dto = userMapper.mapDomainToUserDTO(user);

        // Assert
        assertNotNull(dto);
        assertEquals("John Doe", dto.getName());
        assertEquals("johndoe", dto.getUsername());
        assertEquals("$2a$10$hashedPassword", dto.getPassword());
        assertEquals("john@example.com", dto.getEmail());
        assertEquals("12345678", dto.getPhoneNumber());
        assertEquals("Reader", dto.getRole());
    }

    @Test
    @DisplayName("Should return null when User domain is null")
    void testMapDomainToUserDTO_NullUser() {
        // Act
        UserDTO dto = userMapper.mapDomainToUserDTO(null);

        // Assert
        assertNull(dto);
    }

    // ========== mapDTOUserToDomain (UserDTO) Tests ==========

    @Test
    @DisplayName("Should map UserDTO to User domain")
    void testMapDTOUserToDomain_FromUserDTO_Success() {
        // Arrange
        UserDTO dto = new UserDTO();
        dto.setFullName("Jane Smith");
        dto.setUsername("janesmith");
        dto.setPassword("password123");
        dto.setEmail("jane@example.com");
        dto.setPhoneNumber("87654321");
        dto.setRole("Librarian");

        // Act
        User user = userMapper.mapDTOUserToDomain(dto);

        // Assert
        assertNotNull(user);
        assertEquals("Jane Smith", user.getName());
        assertEquals("janesmith", user.getUsername());
        assertEquals("password123", user.getPassword());
        assertEquals("jane@example.com", user.getEmail());
        assertEquals("87654321", user.getPhoneNumber());
        assertEquals("Librarian", user.getRole());
    }

    @Test
    @DisplayName("Should return null when UserDTO is null")
    void testMapDTOUserToDomain_FromUserDTO_NullDTO() {
        // Act
        User user = userMapper.mapDTOUserToDomain((UserDTO) null);

        // Assert
        assertNull(user);
    }

    // ========== mapDTOUserToDomain (DTOUser - Proto) Tests ==========

    @Test
    @DisplayName("Should map proto DTOUser to User domain")
    void testMapDTOUserToDomain_FromProtoDTOUser_Success() {
        // Arrange
        DTOUser dtoUser = DTOUser.newBuilder()
                .setName("Proto User")
                .setUsername("protouser")
                .setPassword("$2a$10$protoPassword")
                .setEmail("proto@example.com")
                .setPhoneNumber("11223344")
                .setRole("Admin")
                .build();

        // Act
        User user = userMapper.mapDTOUserToDomain(dtoUser);

        // Assert
        assertNotNull(user);
        assertEquals("Proto User", user.getName());
        assertEquals("protouser", user.getUsername());
        assertEquals("$2a$10$protoPassword", user.getPassword());
        assertEquals("proto@example.com", user.getEmail());
        assertEquals("11223344", user.getPhoneNumber());
        assertEquals("Admin", user.getRole());
    }

    @Test
    @DisplayName("Should return null when proto DTOUser is null")
    void testMapDTOUserToDomain_FromProtoDTOUser_NullDTO() {
        // Act
        User user = userMapper.mapDTOUserToDomain((DTOUser) null);

        // Assert
        assertNull(user);
    }

    // ========== mapDomainToAuthResponse Tests ==========

    @Test
    @DisplayName("Should map User domain to AuthResponseDTO")
    void testMapDomainToAuthResponse_Success() {
        // Arrange
        User user = new User();
        user.setUsername("johndoe");
        user.setName("John Doe");
        user.setEmail("john@example.com");
        user.setPhoneNumber("12345678");
        user.setRole("Reader");

        // Act
        AuthResponseDTO dto = userMapper.mapDomainToAuthResponse(user);

        // Assert
        assertNotNull(dto);
        assertEquals("johndoe", dto.getUsername());
        assertEquals("John Doe", dto.getName());
        assertEquals("john@example.com", dto.getEmail());
        assertEquals("12345678", dto.getPhoneNumber());
        assertEquals("Reader", dto.getRole());
    }

    @Test
    @DisplayName("Should return null when User domain is null for AuthResponse")
    void testMapDomainToAuthResponse_NullUser() {
        // Act
        AuthResponseDTO dto = userMapper.mapDomainToAuthResponse(null);

        // Assert
        assertNull(dto);
    }

    // ========== mapLoginRequestToDomain Tests ==========

    @Test
    @DisplayName("Should map LoginRequestDTO to User domain")
    void testMapLoginRequestToDomain_Success() {
        // Arrange
        LoginRequestDTO loginRequest = new LoginRequestDTO();
        loginRequest.setUsername("testuser");
        loginRequest.setPassword("testpass");

        // Act
        User user = userMapper.mapLoginRequestToDomain(loginRequest);

        // Assert
        assertNotNull(user);
        assertEquals("testuser", user.getUsername());
        assertEquals("testpass", user.getPassword());
    }

    @Test
    @DisplayName("Should return null when LoginRequestDTO is null")
    void testMapLoginRequestToDomain_NullDTO() {
        // Act
        User user = userMapper.mapLoginRequestToDomain(null);

        // Assert
        assertNull(user);
    }

    // ========== Edge Cases and Round-Trip Mapping ==========

    @Test
    @DisplayName("Should handle empty string values in RegistrationDTO")
    void testMapRegistrationDTOToDomain_EmptyStrings() {
        // Arrange
        RegistrationDTO dto = new RegistrationDTO();
        dto.setFullName("");
        dto.setUsername("");
        dto.setPassword("");
        dto.setEmail("");
        dto.setPhoneNumber("");

        // Act
        User user = userMapper.mapRegistrationDTOToDomain(dto);

        // Assert
        assertNotNull(user);
        assertEquals("", user.getName());
        assertEquals("", user.getUsername());
        assertEquals("", user.getPassword());
        assertEquals("", user.getEmail());
        assertEquals("", user.getPhoneNumber());
    }

    @Test
    @DisplayName("Should preserve all data in round-trip mapping")
    void testRoundTripMapping_UserToUserDTOToUser() {
        // Arrange
        User originalUser = new User();
        originalUser.setName("Original Name");
        originalUser.setUsername("originaluser");
        originalUser.setPassword("originalpass");
        originalUser.setEmail("original@example.com");
        originalUser.setPhoneNumber("99887766");
        originalUser.setRole("Reader");

        // Act - Round trip: User -> UserDTO -> User
        UserDTO dto = userMapper.mapDomainToUserDTO(originalUser);
        User mappedBackUser = userMapper.mapDTOUserToDomain(dto);

        // Assert
        assertNotNull(mappedBackUser);
        assertEquals(originalUser.getName(), mappedBackUser.getName());
        assertEquals(originalUser.getUsername(), mappedBackUser.getUsername());
        assertEquals(originalUser.getPassword(), mappedBackUser.getPassword());
        assertEquals(originalUser.getEmail(), mappedBackUser.getEmail());
        assertEquals(originalUser.getPhoneNumber(), mappedBackUser.getPhoneNumber());
        assertEquals(originalUser.getRole(), mappedBackUser.getRole());
    }

    @Test
    @DisplayName("Should handle special characters in all fields")
    void testMapRegistrationDTOToDomain_SpecialCharacters() {
        // Arrange
        RegistrationDTO dto = new RegistrationDTO();
        dto.setFullName("Jöhn Döe");
        dto.setUsername("jöhn@döe");
        dto.setPassword("P@ssw0rd!#$%");
        dto.setEmail("jöhn@exämple.com");
        dto.setPhoneNumber("+45-1234-5678");

        // Act
        User user = userMapper.mapRegistrationDTOToDomain(dto);

        // Assert
        assertNotNull(user);
        assertEquals("Jöhn Döe", user.getName());
        assertEquals("jöhn@döe", user.getUsername());
        assertEquals("P@ssw0rd!#$%", user.getPassword());
        assertEquals("jöhn@exämple.com", user.getEmail());
        assertEquals("+45-1234-5678", user.getPhoneNumber());
    }

    @Test
    @DisplayName("Should map all user roles correctly")
    void testMapDomainToUserDTO_DifferentRoles() {
        // Arrange & Act & Assert for Reader
        User reader = new User();
        reader.setRole("Reader");
        UserDTO readerDTO = userMapper.mapDomainToUserDTO(reader);
        assertEquals("Reader", readerDTO.getRole());

        // Librarian
        User librarian = new User();
        librarian.setRole("Librarian");
        UserDTO librarianDTO = userMapper.mapDomainToUserDTO(librarian);
        assertEquals("Librarian", librarianDTO.getRole());

        // Admin
        User admin = new User();
        admin.setRole("Admin");
        UserDTO adminDTO = userMapper.mapDomainToUserDTO(admin);
        assertEquals("Admin", adminDTO.getRole());
    }
}

