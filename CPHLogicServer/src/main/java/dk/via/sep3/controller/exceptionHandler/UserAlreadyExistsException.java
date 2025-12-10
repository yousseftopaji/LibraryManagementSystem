package dk.via.sep3.controller.exceptionHandler;

public class UserAlreadyExistsException extends RuntimeException
{
  public UserAlreadyExistsException(String message)
  {
    super(message);
  }

  public UserAlreadyExistsException(String message, Throwable cause)
  {
    super(message, cause);
  }
}
