package dk.via.sep3.DTOs.auth;

import dk.via.sep3.DTOs.user.UserDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for RegisterResponseDTO
 * Tests DTO construction and field access
 */
class RegisterResponseDTOTest {

    private RegisterResponseDTO registerResponseDTO;

    @BeforeEach
    void setUp() {
        registerResponseDTO = new RegisterResponseDTO();
    }

    @Test
    @DisplayName("Should create RegisterResponseDTO with no-arg constructor")
    void testNoArgConstructor() {
        // Act
        RegisterResponseDTO dto = new RegisterResponseDTO();

        // Assert
        assertNotNull(dto);
        assertNull(dto.getUsername());
        assertNull(dto.getName());
        assertNull(dto.getEmail());
        assertNull(dto.getPhoneNumber());
        assertNull(dto.getRole());
    }

    @Test
    @DisplayName("Should create RegisterResponseDTO with all-args constructor")
    void testAllArgsConstructor() {
        // Act
        RegisterResponseDTO dto = new RegisterResponseDTO(
                "johndoe",
                "John Doe",
                "john@example.com",
                "12345678",
                "Reader"
        );

        // Assert
        assertNotNull(dto);
        assertEquals("johndoe", dto.getUsername());
        assertEquals("John Doe", dto.getName());
        assertEquals("john@example.com", dto.getEmail());
        assertEquals("12345678", dto.getPhoneNumber());
        assertEquals("Reader", dto.getRole());
    }

    @Test
    @DisplayName("Should create RegisterResponseDTO from UserDTO")
    void testUserDTOConstructor() {
        // Arrange
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername("janedoe");
        userDTO.setFullName("Jane Doe");
        userDTO.setEmail("jane@example.com");
        userDTO.setPhoneNumber("87654321");
        userDTO.setRole("Librarian");

        // Act
        RegisterResponseDTO dto = new RegisterResponseDTO(userDTO);

        // Assert
        assertNotNull(dto);
        assertEquals("janedoe", dto.getUsername());
        assertEquals("Jane Doe", dto.getName());
        assertEquals("jane@example.com", dto.getEmail());
        assertEquals("87654321", dto.getPhoneNumber());
        assertEquals("Librarian", dto.getRole());
    }

    @Test
    @DisplayName("Should handle null UserDTO in constructor")
    void testNullUserDTOConstructor() {
        // Act
        RegisterResponseDTO dto = new RegisterResponseDTO(null);

        // Assert
        assertNotNull(dto);
        assertNull(dto.getUsername());
        assertNull(dto.getName());
        assertNull(dto.getEmail());
        assertNull(dto.getPhoneNumber());
        assertNull(dto.getRole());
    }

    @Test
    @DisplayName("Should set and get username")
    void testSetAndGetUsername() {
        // Act
        registerResponseDTO.setUsername("testuser");

        // Assert
        assertEquals("testuser", registerResponseDTO.getUsername());
    }

    @Test
    @DisplayName("Should set and get name")
    void testSetAndGetName() {
        // Act
        registerResponseDTO.setName("Test User");

        // Assert
        assertEquals("Test User", registerResponseDTO.getName());
    }

    @Test
    @DisplayName("Should set and get email")
    void testSetAndGetEmail() {
        // Act
        registerResponseDTO.setEmail("test@example.com");

        // Assert
        assertEquals("test@example.com", registerResponseDTO.getEmail());
    }

    @Test
    @DisplayName("Should set and get phoneNumber")
    void testSetAndGetPhoneNumber() {
        // Act
        registerResponseDTO.setPhoneNumber("99887766");

        // Assert
        assertEquals("99887766", registerResponseDTO.getPhoneNumber());
    }

    @Test
    @DisplayName("Should set and get role")
    void testSetAndGetRole() {
        // Act
        registerResponseDTO.setRole("Admin");

        // Assert
        assertEquals("Admin", registerResponseDTO.getRole());
    }

    @Test
    @DisplayName("Should handle null values")
    void testNullValues() {
        // Act
        registerResponseDTO.setUsername(null);
        registerResponseDTO.setName(null);
        registerResponseDTO.setEmail(null);
        registerResponseDTO.setPhoneNumber(null);
        registerResponseDTO.setRole(null);

        // Assert
        assertNull(registerResponseDTO.getUsername());
        assertNull(registerResponseDTO.getName());
        assertNull(registerResponseDTO.getEmail());
        assertNull(registerResponseDTO.getPhoneNumber());
        assertNull(registerResponseDTO.getRole());
    }

    @Test
    @DisplayName("Should handle empty strings")
    void testEmptyStrings() {
        // Act
        registerResponseDTO.setUsername("");
        registerResponseDTO.setName("");
        registerResponseDTO.setEmail("");
        registerResponseDTO.setPhoneNumber("");
        registerResponseDTO.setRole("");

        // Assert
        assertEquals("", registerResponseDTO.getUsername());
        assertEquals("", registerResponseDTO.getName());
        assertEquals("", registerResponseDTO.getEmail());
        assertEquals("", registerResponseDTO.getPhoneNumber());
        assertEquals("", registerResponseDTO.getRole());
    }

    @Test
    @DisplayName("Should handle different role values")
    void testDifferentRoles() {
        // Test Reader role
        RegisterResponseDTO reader = new RegisterResponseDTO("user1", "User One",
                "user1@example.com", "11111111", "Reader");
        assertEquals("Reader", reader.getRole());

        // Test Librarian role
        RegisterResponseDTO librarian = new RegisterResponseDTO("user2", "User Two",
                "user2@example.com", "22222222", "Librarian");
        assertEquals("Librarian", librarian.getRole());

        // Test Admin role
        RegisterResponseDTO admin = new RegisterResponseDTO("user3", "User Three",
                "user3@example.com", "33333333", "Admin");
        assertEquals("Admin", admin.getRole());
    }

    @Test
    @DisplayName("Should correctly map all fields from UserDTO")
    void testCompleteUserDTOMapping() {
        // Arrange
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername("completeuser");
        userDTO.setFullName("Complete User");
        userDTO.setEmail("complete@example.com");
        userDTO.setPhoneNumber("12348765");
        userDTO.setRole("Reader");

        // Act
        RegisterResponseDTO dto = new RegisterResponseDTO(userDTO);

        // Assert
        assertAll("User DTO mapping",
                () -> assertEquals(userDTO.getUsername(), dto.getUsername()),
                () -> assertEquals(userDTO.getName(), dto.getName()),
                () -> assertEquals(userDTO.getEmail(), dto.getEmail()),
                () -> assertEquals(userDTO.getPhoneNumber(), dto.getPhoneNumber()),
                () -> assertEquals(userDTO.getRole(), dto.getRole())
        );
    }

    @Test
    @DisplayName("Should handle UserDTO with null fields")
    void testUserDTOWithNullFields() {
        // Arrange
        UserDTO userDTO = new UserDTO();
        // Leave all fields as null

        // Act
        RegisterResponseDTO dto = new RegisterResponseDTO(userDTO);

        // Assert
        assertNotNull(dto);
        assertNull(dto.getUsername());
        assertNull(dto.getName());
        assertNull(dto.getEmail());
        assertNull(dto.getPhoneNumber());
        assertNull(dto.getRole());
    }

    @Test
    @DisplayName("Should handle special characters in fields")
    void testSpecialCharacters() {
        // Act
        RegisterResponseDTO dto = new RegisterResponseDTO(
                "user@name",
                "Nåme Wïth Spëciål",
                "special+email@example.com",
                "+45-1234-5678",
                "Rôle"
        );

        // Assert
        assertEquals("user@name", dto.getUsername());
        assertEquals("Nåme Wïth Spëciål", dto.getName());
        assertEquals("special+email@example.com", dto.getEmail());
        assertEquals("+45-1234-5678", dto.getPhoneNumber());
        assertEquals("Rôle", dto.getRole());
    }

    @Test
    @DisplayName("Should handle very long strings")
    void testLongStrings() {
        // Arrange
        String longString = "a".repeat(1000);

        // Act
        registerResponseDTO.setUsername(longString);
        registerResponseDTO.setName(longString);
        registerResponseDTO.setEmail(longString);
        registerResponseDTO.setPhoneNumber(longString);
        registerResponseDTO.setRole(longString);

        // Assert
        assertEquals(longString, registerResponseDTO.getUsername());
        assertEquals(longString, registerResponseDTO.getName());
        assertEquals(longString, registerResponseDTO.getEmail());
        assertEquals(longString, registerResponseDTO.getPhoneNumber());
        assertEquals(longString, registerResponseDTO.getRole());
    }
}

