using System.Text.Json.Serialization;

namespace DTOs.User;

public class LoginDTO
{
    [JsonPropertyName("username")]
    public string Username { get; set; } = string.Empty;

    [JsonPropertyName("password")]
    public string Password { get; set; } = string.Empty;
}

