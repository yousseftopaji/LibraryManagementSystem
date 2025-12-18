package dk.via.sep3.DTOs.error;

public class ErrorResponseDTO
{
  private String message;
  private String errorCode;
  private String timestamp;
  private String details;

  public ErrorResponseDTO(String message, String errorCode, String timestamp, String details)
  {
    this.message = message;
    this.errorCode = errorCode;
    this.timestamp = timestamp;
    this.details = details;
  }

  public String getMessage()
  {
    return message;
  }

  public String getErrorCode()
  {
    return errorCode;
  }

  public String getTimestamp()
  {
    return timestamp;
  }

  public String getDetails()
  {
    return details;
  }

  public void setDetails(String details)
  {
    this.details = details;
  }

  public void setErrorCode(String errorCode)
  {
    this.errorCode = errorCode;
  }

  public void setMessage(String message)
  {
    this.message = message;
  }

  public void setTimestamp(String timestamp)
  {
    this.timestamp = timestamp;
  }
}
