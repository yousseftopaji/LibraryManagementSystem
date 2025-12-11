package dk.via.sep3.shared.auth;

import dk.via.sep3.shared.user.UserDTO;

public class LoginResponse {
    private String token;
    private boolean success;
    private String message;
    private UserDTO user;

    public LoginResponse() {}

    public LoginResponse(String token, boolean success, String message, UserDTO user) {
        this.token = token;
        this.success = success;
        this.message = message;
        this.user = user;
    }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public UserDTO getUser() { return user; }
    public void setUser(UserDTO user) { this.user = user; }
}

