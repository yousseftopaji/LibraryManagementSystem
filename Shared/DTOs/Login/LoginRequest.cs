using System;
using System.Text.Json.Serialization;
namespace DTOs;

public class LoginRequest
{
    [JsonPropertyName("username")]
    public string UserName { get; set; } = string.Empty;
    
    [JsonPropertyName("password")]
    public string Password { get; set; } = string.Empty;

    public LoginRequest()
    {

    }
    public LoginRequest(string userName, string password)
    {
        UserName = userName;
        Password = password;
    }
}
