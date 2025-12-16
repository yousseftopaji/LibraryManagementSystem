package dk.via.sep3.exceptionHandler;

public class ResourceNotFoundException extends RuntimeException
{
  public ResourceNotFoundException(String message)
  {
    super(message);
  }

  public ResourceNotFoundException(String message, Throwable cause)
  {
    super(message, cause);
  }
}
