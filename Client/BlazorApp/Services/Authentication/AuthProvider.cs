using System.Security.Claims;
using System.Text.Json;
using DTOs;
using DTOs.Auth;
using DTOs.User;
using Microsoft.AspNetCore.Components.Authorization;


public class AuthProvider : AuthenticationStateProvider
{
    private readonly HttpClient client;
    private ClaimsPrincipal? currentClaimsPrincipal;

    public AuthProvider(HttpClient client)
    {
        this.client = client;
    }

    public async Task Register(string fullName, string phone, string userName, string email, string password)
    {
        var request = new RegisterRequest()
        {
            FullName = fullName,
            Phone = phone,
            UserName = userName,
            Email = email,
            Password = password
        };

        var response = await client.PostAsJsonAsync("api/auth/register", request);

        if (!response.IsSuccessStatusCode)
            throw new Exception(await response.Content.ReadAsStringAsync());
    }
  
    public async Task Login(string username, string password)
    {
        HttpResponseMessage response = await client.PostAsJsonAsync(
            "auth/login",
            new LoginRequest(username, password)
        );

        string content = await response.Content.ReadAsStringAsync();
        if (!response.IsSuccessStatusCode)
        {
            throw new Exception(content);
        }

        UserDTO userDTO = JsonSerializer.Deserialize<UserDTO>(content, new JsonSerializerOptions
        {
            PropertyNameCaseInsensitive = true
        })!;

        List<Claim> claims = new List<Claim>()
        {
            new Claim(ClaimTypes.Name, userDTO.Username),
            new Claim("Password", userDTO.PasswordHash),
            new Claim("Role", userDTO.Role)
        };

        ClaimsIdentity identity = new ClaimsIdentity(claims, "apiauth");
        currentClaimsPrincipal = new ClaimsPrincipal(identity);

        NotifyAuthenticationStateChanged(
            Task.FromResult(new AuthenticationState(currentClaimsPrincipal))
        );
    }
   
    public override Task<AuthenticationState> GetAuthenticationStateAsync()
    {
        return Task.FromResult(new AuthenticationState(currentClaimsPrincipal ?? new()));
    }

    public void Logout()
    {
        currentClaimsPrincipal = new();
        NotifyAuthenticationStateChanged(Task.FromResult(new AuthenticationState(currentClaimsPrincipal)));
    }
}