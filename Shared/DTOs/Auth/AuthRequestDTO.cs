using System;
using System.Text.Json.Serialization;

namespace DTOs.Auth;

public class RegisterRequest
{
    [JsonPropertyName("fullName")]
    public required string FullName { get; set; }

    [JsonPropertyName("phoneNumber")]
    public required string PhoneNumber { get; set; }
    
    [JsonPropertyName("username")]
    public required string UserName { get; set; }
    
    [JsonPropertyName("email")]
    public required string Email { get; set; }
    
    [JsonPropertyName("password")]
    public required string Password { get; set; }
}