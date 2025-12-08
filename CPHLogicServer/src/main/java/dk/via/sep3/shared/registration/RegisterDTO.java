package dk.via.sep3.shared.registration;

public class RegisterDTO
{
  private boolean success;
  private String message;

  public RegisterDTO() {
  }

  public RegisterDTO(boolean success, String message) {
    this.success = success;
    this.message = message;
  }

  public boolean isSuccess() {
    return success;
  }

  public void setSuccess(boolean success) {
    this.success = success;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }
}
