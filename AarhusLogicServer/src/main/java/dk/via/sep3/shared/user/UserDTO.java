package dk.via.sep3.shared.user;

public class UserDTO
{
  private String username;
  private String password;
  private String role;
  private String name;
  private String phoneNumber;
  private String email;

  public UserDTO()
  {
  }

  public String getUsername()
  {
    return username;
  }

  public void setUsername(String username)
  {
    this.username = username;
  }

  public String getName()
  {
    return name;
  }

  public void setFullName(String name)
  {
    this.name = name;
  }

  public String getPhoneNumber()
  {
    return phoneNumber;
  }

  public void setPhoneNumber(String phoneNumber)
  {
    this.phoneNumber = phoneNumber;
  }

  public String getPassword()
  {
    return password;
  }

  public void setPassword(String password)
  {
    this.password = password;
  }

  public String getRole()
  {
    return role;
  }

  public void setRole(String role)
  {
    this.role = role;
  }

  public String getEmail()
  {
    return email;
  }

  public void setEmail(String email)
  {
    this.email = email;
  }
}
