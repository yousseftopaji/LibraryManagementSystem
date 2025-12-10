using System;
using DTOs.User;
namespace DTOs.Auth;
using System.Text.Json.Serialization;


public class LoginResponse
{
    [JsonPropertyName("token")]
    public string? Token { get; set; }
    
    [JsonPropertyName("success")]
    public bool Success { get; set; }
   
    [JsonPropertyName("message")]
    public string? Message { get; set; }
   
    [JsonPropertyName("user")]
    public UserDTO? User { get; set; }
}
