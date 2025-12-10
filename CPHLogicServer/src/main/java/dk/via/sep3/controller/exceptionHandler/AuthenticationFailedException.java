package dk.via.sep3.controller.exceptionHandler;

public class AuthenticationFailedException extends RuntimeException
{
  public AuthenticationFailedException(String message)
  {
    super(message);
  }

  public AuthenticationFailedException(String message, Throwable cause)
  {
    super(message, cause);
  }
}
