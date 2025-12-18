package dk.via.sep3.application.domain;

public class User
{
  private String name;
  private String username;
  private String password;
  private String role;
  private String phoneNumber;
  private String email;

  public User()
  {
  }

  public User(String name, String username, String password, String role, String phoneNumber, String email)
  {
    this.name = name;
    this.username = username;
    this.password = password;
    this.role = role;
    this.phoneNumber = phoneNumber;
    this.email = email;
  }

  public String getName()
  {
    return name;
  }

  public void setName(String name)
  {
    this.name = name;
  }

  public String getUsername()
  {
    return username;
  }

  public void setUsername(String username)
  {
    this.username = username;
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

  public String getPhoneNumber()
  {
    return phoneNumber;
  }

  public void setPhoneNumber(String phoneNumber)
  {
    this.phoneNumber = phoneNumber;
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
