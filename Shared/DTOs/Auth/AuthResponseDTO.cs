using System;
using System.Text.Json.Serialization;
using DTOs.User;

namespace DTOs.Auth;

public class AuthResponseDTO
{
    [JsonPropertyName("token")]
    public string? Token { get; set; }

    [JsonPropertyName("success")]
    public bool Success { get; set; }

    [JsonPropertyName("message")]
    public string? Message { get; set; }

    [JsonPropertyName("user")]
    public UserInfoDTO? User { get; set; }
}
