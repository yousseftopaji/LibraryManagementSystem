package dk.via.sep3.shared.auth;

import dk.via.sep3.shared.user.UserDTO;

@SuppressWarnings("unused")
public class AuthResponseDTO {

    private String username;
    private String name;
    private String email;
    private String phoneNumber;
    private String role;

    public AuthResponseDTO() {
    }

    public AuthResponseDTO(
                           String username,
                           String name,
                           String email,
                           String phoneNumber,
                           String role) {

        this.username = username;
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.role = role;
    }

    // Convenience constructor used by AuthController
    public AuthResponseDTO( UserDTO user) {

        if (user != null) {
            this.username = user.getUsername();
            this.name = user.getName();
            this.email = user.getEmail();
            this.phoneNumber = user.getPhoneNumber();
            this.role = user.getRole();
        }
    }


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
