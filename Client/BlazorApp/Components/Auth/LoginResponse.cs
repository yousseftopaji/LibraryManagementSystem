using System;

namespace BlazorApp.Components.Auth;

public class LoginResponse
{
    public string Username { get; set; } = null!;
    public string Role { get; set; } = null!;
    public string Token { get; set; } = null!;
}