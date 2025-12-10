package dk.via.sep3.shared.login;

import dk.via.sep3.shared.user.UserDTO;

public class LoginResponseDTO {
    private String token;
    private String  username;

    public LoginResponseDTO() {}

    public LoginResponseDTO(String token, String username) {
        this.token = token;
        this.username = username;
    }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
}

