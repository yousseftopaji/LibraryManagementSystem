package dk.via.sep3.shared.login;

import com.fasterxml.jackson.annotation.JsonProperty;

public class LoginDTO
{
  @JsonProperty(value = "username")
  private String username;

  @JsonProperty(value = "password")
  private String password;

  public LoginDTO() {
  }

  public LoginDTO(String username, String password) {
    this.username = username;
    this.password = password;
  }

  public String getUsername() {
    return username;
  }
  public void setUsername(String username) {
    this.username = username;
  }
  public String getPassword() {
    return password;
  }
  public void setPassword(String password) {
    this.password = password;
  }

  @Override
  public String toString() {
    return "LoginDTO{" +
        "username='" + username + '\'' +
        ", password='" + (password != null ? "***" : "null") + '\'' +
        '}';
  }
}
