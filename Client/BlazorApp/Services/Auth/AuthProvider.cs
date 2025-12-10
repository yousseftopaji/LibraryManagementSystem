using System.Security.Claims;
using System.Text.Json;
using DTOs;
using DTOs.Auth;
using Microsoft.AspNetCore.Components.Authorization;


public class AuthProvider : AuthenticationStateProvider
{
    private readonly HttpClient client;
    private string? jwtToken;
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


        jwtToken = loginResponse.Token;

        List<Claim> claims = new List<Claim>()
        {
        new Claim(ClaimTypes.Name, loginResponse.User.Username),
        new Claim(ClaimTypes.Role, loginResponse.User.Role),
        new Claim("FullName", loginResponse.User.Name),
        new Claim("Email", loginResponse.User.Email),
        new Claim("PhoneNumber", loginResponse.User.PhoneNumber)
        };

        ClaimsIdentity identity = new ClaimsIdentity(claims, "auth");
        currentClaimsPrincipal = new ClaimsPrincipal(identity);

        NotifyAuthenticationStateChanged(
            Task.FromResult(new AuthenticationState(currentClaimsPrincipal))
        );
    }
   
    public string? GetToken() => jwtToken;

    public override Task<AuthenticationState> GetAuthenticationStateAsync()
    {
        return Task.FromResult(new AuthenticationState(currentClaimsPrincipal ?? new()));
    }

    public void Logout()
    {
        currentClaimsPrincipal = new();
        NotifyAuthenticationStateChanged(Task.FromResult(new AuthenticationState(currentClaimsPrincipal)));
    }

    public void AttachToken(HttpClient client)
{
    if (!string.IsNullOrEmpty(jwtToken))
    {
        client.DefaultRequestHeaders.Authorization =
            new System.Net.Http.Headers.AuthenticationHeaderValue("Bearer", jwtToken);
    }
}
}