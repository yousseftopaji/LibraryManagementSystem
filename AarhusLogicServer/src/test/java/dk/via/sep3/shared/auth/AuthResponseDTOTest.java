package dk.via.sep3.shared.auth;

import dk.via.sep3.shared.user.UserDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for AuthResponseDTO
 * Tests DTO construction and field access
 */
class AuthResponseDTOTest {

    private AuthResponseDTO authResponseDTO;

    @BeforeEach
    void setUp() {
        authResponseDTO = new AuthResponseDTO();
    }

    @Test
    @DisplayName("Should create AuthResponseDTO with no-arg constructor")
    void testNoArgConstructor() {
        // Act
        AuthResponseDTO dto = new AuthResponseDTO();

        // Assert
        assertNotNull(dto);
        assertNull(dto.getUsername());
        assertNull(dto.getName());
        assertNull(dto.getEmail());
        assertNull(dto.getPhoneNumber());
        assertNull(dto.getRole());
    }

    @Test
    @DisplayName("Should create AuthResponseDTO with all fields")
    void testAllArgsConstructor() {
        // Act
        AuthResponseDTO dto = new AuthResponseDTO(
            "johndoe",
            "John Doe",
            "john@example.com",
            "12345678",
            "Reader"
        );

        // Assert
        assertEquals("johndoe", dto.getUsername());
        assertEquals("John Doe", dto.getName());
        assertEquals("john@example.com", dto.getEmail());
        assertEquals("12345678", dto.getPhoneNumber());
        assertEquals("Reader", dto.getRole());
    }

    @Test
    @DisplayName("Should create AuthResponseDTO from UserDTO")
    void testUserDTOConstructor() {
        // Arrange
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername("janedoe");
        userDTO.setFullName("Jane Doe");
        userDTO.setEmail("jane@example.com");
        userDTO.setPhoneNumber("87654321");
        userDTO.setRole("Librarian");

        // Act
        AuthResponseDTO dto = new AuthResponseDTO(userDTO);

        // Assert
        assertEquals("janedoe", dto.getUsername());
        assertEquals("Jane Doe", dto.getName());
        assertEquals("jane@example.com", dto.getEmail());
        assertEquals("87654321", dto.getPhoneNumber());
        assertEquals("Librarian", dto.getRole());
    }

    @Test
    @DisplayName("Should handle null UserDTO in constructor")
    void testUserDTOConstructor_NullUser() {
        // Act
        AuthResponseDTO dto = new AuthResponseDTO(null);

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
    void testSetGetUsername() {
        // Act
        authResponseDTO.setUsername("testuser");

        // Assert
        assertEquals("testuser", authResponseDTO.getUsername());
    }

    @Test
    @DisplayName("Should set and get name")
    void testSetGetName() {
        // Act
        authResponseDTO.setName("Test User");

        // Assert
        assertEquals("Test User", authResponseDTO.getName());
    }

    @Test
    @DisplayName("Should set and get email")
    void testSetGetEmail() {
        // Act
        authResponseDTO.setEmail("test@example.com");

        // Assert
        assertEquals("test@example.com", authResponseDTO.getEmail());
    }

    @Test
    @DisplayName("Should set and get phone number")
    void testSetGetPhoneNumber() {
        // Act
        authResponseDTO.setPhoneNumber("99887766");

        // Assert
        assertEquals("99887766", authResponseDTO.getPhoneNumber());
    }

    @Test
    @DisplayName("Should set and get role")
    void testSetGetRole() {
        // Act
        authResponseDTO.setRole("Admin");

        // Assert
        assertEquals("Admin", authResponseDTO.getRole());
    }

    @Test
    @DisplayName("Should handle null values in setters")
    void testSetters_NullValues() {
        // Act
        authResponseDTO.setUsername(null);
        authResponseDTO.setName(null);
        authResponseDTO.setEmail(null);
        authResponseDTO.setPhoneNumber(null);
        authResponseDTO.setRole(null);

        // Assert
        assertNull(authResponseDTO.getUsername());
        assertNull(authResponseDTO.getName());
        assertNull(authResponseDTO.getEmail());
        assertNull(authResponseDTO.getPhoneNumber());
        assertNull(authResponseDTO.getRole());
    }

    @Test
    @DisplayName("Should handle empty strings in setters")
    void testSetters_EmptyStrings() {
        // Act
        authResponseDTO.setUsername("");
        authResponseDTO.setName("");
        authResponseDTO.setEmail("");
        authResponseDTO.setPhoneNumber("");
        authResponseDTO.setRole("");

        // Assert
        assertEquals("", authResponseDTO.getUsername());
        assertEquals("", authResponseDTO.getName());
        assertEquals("", authResponseDTO.getEmail());
        assertEquals("", authResponseDTO.getPhoneNumber());
        assertEquals("", authResponseDTO.getRole());
    }

    @Test
    @DisplayName("Should create DTO with all different roles")
    void testAllArgsConstructor_DifferentRoles() {
        // Reader
        AuthResponseDTO reader = new AuthResponseDTO("user1", "User One",
            "user1@example.com", "11111111", "Reader");
        assertEquals("Reader", reader.getRole());

        // Librarian
        AuthResponseDTO librarian = new AuthResponseDTO("user2", "User Two",
            "user2@example.com", "22222222", "Librarian");
        assertEquals("Librarian", librarian.getRole());

        // Admin
        AuthResponseDTO admin = new AuthResponseDTO("user3", "User Three",
            "user3@example.com", "33333333", "Admin");
        assertEquals("Admin", admin.getRole());
    }

    @Test
    @DisplayName("Should preserve all fields when created from UserDTO")
    void testUserDTOConstructor_PreservesAllFields() {
        // Arrange
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername("completeuser");
        userDTO.setFullName("Complete User");
        userDTO.setEmail("complete@example.com");
        userDTO.setPhoneNumber("12348765");
        userDTO.setRole("Reader");

        // Act
        AuthResponseDTO dto = new AuthResponseDTO(userDTO);

        // Assert - All fields preserved
        assertNotNull(dto);
        assertEquals(userDTO.getUsername(), dto.getUsername());
        assertEquals(userDTO.getName(), dto.getName());
        assertEquals(userDTO.getEmail(), dto.getEmail());
        assertEquals(userDTO.getPhoneNumber(), dto.getPhoneNumber());
        assertEquals(userDTO.getRole(), dto.getRole());
    }

    @Test
    @DisplayName("Should handle UserDTO with null fields")
    void testUserDTOConstructor_UserWithNullFields() {
        // Arrange
        UserDTO userDTO = new UserDTO();
        // All fields are null by default

        // Act
        AuthResponseDTO dto = new AuthResponseDTO(userDTO);

        // Assert
        assertNotNull(dto);
        assertNull(dto.getUsername());
        assertNull(dto.getName());
        assertNull(dto.getEmail());
        assertNull(dto.getPhoneNumber());
        assertNull(dto.getRole());
    }

    @Test
    @DisplayName("Should handle special characters in all fields")
    void testSpecialCharacters() {
        // Act
        AuthResponseDTO dto = new AuthResponseDTO(
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
    void testVeryLongStrings() {
        // Arrange
        String longString = "a".repeat(1000);

        // Act
        authResponseDTO.setUsername(longString);
        authResponseDTO.setName(longString);
        authResponseDTO.setEmail(longString);
        authResponseDTO.setPhoneNumber(longString);
        authResponseDTO.setRole(longString);

        // Assert
        assertEquals(longString, authResponseDTO.getUsername());
        assertEquals(longString, authResponseDTO.getName());
        assertEquals(longString, authResponseDTO.getEmail());
        assertEquals(longString, authResponseDTO.getPhoneNumber());
        assertEquals(longString, authResponseDTO.getRole());
    }
}

