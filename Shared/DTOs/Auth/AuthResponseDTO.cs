using System;
using System.Text.Json.Serialization;
using DTOs.User;

namespace DTOs.Auth;

public class AuthResponseDTO
{
    [JsonPropertyName("username")]
    public string? Username { get; set; }

    [JsonPropertyName("name")]
    public string? Name { get; set; }

    [JsonPropertyName("email")]
    public string? Email { get; set; }

    [JsonPropertyName("phoneNumber")]
    public string? PhoneNumber { get; set; }

    [JsonPropertyName("role")]
    public string? Role { get; set; }
    
}
