package dk.via.sep3.exceptionHandler;

public class GrpcCommunicationException extends RuntimeException
{
  public GrpcCommunicationException(String message)
  {
    super(message);
  }

  public GrpcCommunicationException(String message, Throwable cause)
  {
    super(message, cause);
  }
}
