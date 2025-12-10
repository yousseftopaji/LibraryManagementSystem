using System;
using DTOs.User;
namespace DTOs.Auth;

public class LoginResponse
{
    public required string Token { get; set; }
    public bool Success { get; set; }
    public required string Message { get; set; }
    public required UserDTO User { get; set; }
}
