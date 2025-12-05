package dk.via.sep3.controller.exceptionHandler;

public class ResourceNotFoundException extends RuntimeException
{
  public ResourceNotFoundException(String message)
  {
    super(message);
  }
}
