using System;

namespace DTOs.Error_Handler;

public class ErrorResponseDTO
{
    public String? Message {get; set;}
    public String? ErrorCode {get; set;}
    public String? Timestamp {get; set;}
    public String? Details {get; set;}
}
