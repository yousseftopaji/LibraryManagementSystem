using System.Security.Claims;
using System.Text.Json;
using BlazorApp.Components.Auth;
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

    public async Task Register(string fullName, string phone, string userName, string email, string password, string confirmPassword)
    {
        var request = new RegisterRequest()
        {
            FullName = fullName,
            Phone = phone,
            UserName = userName,
            Email = email,
            Password = password,
            ConfirmPassword = confirmPassword
        };
    Console.WriteLine(JsonSerializer.Serialize(request));
        var response = await client.PostAsJsonAsync("auth/register", request);

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

        var loginResponse = JsonSerializer.Deserialize<LoginResponse>(content, new JsonSerializerOptions
        {
            PropertyNameCaseInsensitive = true
        })!;


    // Save JWT locally (in memory for now)
        var token = loginResponse.Token;

        List<Claim> claims = new List<Claim>()
        {
            new Claim(ClaimTypes.Name, loginResponse.Username),
            // new Claim("Password", loginResponse.Password),
            new Claim("Role", loginResponse.Role)
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