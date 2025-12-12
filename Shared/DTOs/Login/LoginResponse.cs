using System;
using DTOs.User;
namespace DTOs.Auth;
using System.Text.Json.Serialization;


public class LoginResponse
{
    [JsonPropertyName("token")]
    public string? Token { get; set; }
      
    [JsonPropertyName("username")]
    public string? Username { get; set; }

}
