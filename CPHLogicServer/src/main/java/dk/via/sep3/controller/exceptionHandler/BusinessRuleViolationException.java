package dk.via.sep3.controller.exceptionHandler;

public class BusinessRuleViolationException extends RuntimeException
{
  public BusinessRuleViolationException(String message)
  {
    super(message);
  }

  public BusinessRuleViolationException(String message, Throwable cause)
  {
    super(message, cause);
  }
}
