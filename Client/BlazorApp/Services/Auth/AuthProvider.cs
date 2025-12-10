using System.IdentityModel.Tokens.Jwt;
using System.Security.Claims;
using System.Text.Json;
using DTOs;
using DTOs.Auth;
using Microsoft.AspNetCore.Components.Authorization;
using Microsoft.JSInterop;

public class AuthProvider : AuthenticationStateProvider
{
    private readonly HttpClient client;
    private readonly IJSRuntime js;
    private string? jwtToken;
    private ClaimsPrincipal currentClaimsPrincipal = new(new ClaimsIdentity());

    public AuthProvider(HttpClient client, IJSRuntime js)
    {
        this.client = client;
        this.js = js;
    }

    // REGISTER
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
        string content = await response.Content.ReadAsStringAsync();

        if (!response.IsSuccessStatusCode)
            throw new Exception(content);

        var registerResponse = JsonSerializer.Deserialize<AuthResponseDTO>(content,
            new JsonSerializerOptions { PropertyNameCaseInsensitive = true })!;

        // Save token and update authentication state
        if (!string.IsNullOrEmpty(registerResponse.Token))
        {
            await SaveTokenAsync(registerResponse.Token);
        }
    }

    // LOGIN
    public async Task Login(string username, string password)
    {
        var response = await client.PostAsJsonAsync("auth/login", new LoginRequest(username, password));
        string content = await response.Content.ReadAsStringAsync();

        if (!response.IsSuccessStatusCode)
            throw new Exception(content);

        var loginResponse = JsonSerializer.Deserialize<AuthResponseDTO>(content, new JsonSerializerOptions
        {
            PropertyNameCaseInsensitive = true
        });

        Console.WriteLine("Received token: " + loginResponse?.Token);

        if (loginResponse == null || string.IsNullOrEmpty(loginResponse.Token))
            throw new Exception("Invalid login response");

        if (loginResponse.User == null)
            throw new Exception("Invalid user data in login response");

        await SaveTokenAsync(loginResponse.Token);

        // Build claims
        var claims = new List<Claim>()
        {
            new Claim(ClaimTypes.Name, loginResponse.User.Username ?? "User"),
            new Claim(ClaimTypes.Role, loginResponse.User.Role ?? "User"),
            new Claim("FullName", loginResponse.User.Name ?? ""),
            new Claim("Email", loginResponse.User.Email ?? ""),
            new Claim("PhoneNumber", loginResponse.User.PhoneNumber ?? "")
        };

        currentClaimsPrincipal = new ClaimsPrincipal(new ClaimsIdentity(claims, "jwt"));
        NotifyAuthenticationStateChanged(Task.FromResult(new AuthenticationState(currentClaimsPrincipal)));
    }

    // LOGOUT
    public async Task Logout()
    {
        currentClaimsPrincipal = new ClaimsPrincipal(new ClaimsIdentity());
        jwtToken = null;
        await js.InvokeVoidAsync("localStorage.removeItem", "jwtToken");
        NotifyAuthenticationStateChanged(Task.FromResult(new AuthenticationState(currentClaimsPrincipal)));
    }

    // Initialize authentication state from localStorage
    public async Task InitializeAsync()
    {
        currentClaimsPrincipal = new ClaimsPrincipal(new ClaimsIdentity());
        NotifyAuthenticationStateChanged(Task.FromResult(new AuthenticationState(currentClaimsPrincipal)));

        jwtToken = await js.InvokeAsync<string>("localStorage.getItem", "jwtToken");

        if (!string.IsNullOrEmpty(jwtToken))
        {
            // Validate token
            var handler = new JwtSecurityTokenHandler();
            JwtSecurityToken? jwt = null;

            try
            {
                jwt = handler.ReadJwtToken(jwtToken);
            }
            catch
            {
                // Invalid token
                await Logout();
                return;
            }

            // Check expiration
            if (jwt.ValidTo < DateTime.UtcNow)
            {
                await Logout();
                return;
            }

            // Set claims
            var claims = jwt.Claims.ToList();
            currentClaimsPrincipal = new ClaimsPrincipal(new ClaimsIdentity(claims, "jwt"));
        }
        else
        {
            currentClaimsPrincipal = new ClaimsPrincipal(new ClaimsIdentity());
        }

        NotifyAuthenticationStateChanged(Task.FromResult(new AuthenticationState(currentClaimsPrincipal)));
    }

    public override Task<AuthenticationState> GetAuthenticationStateAsync()
    {
        return Task.FromResult(new AuthenticationState(currentClaimsPrincipal));
    }

    public string? GetToken() => jwtToken;

    // Helper: save token to localStorage
    private async Task SaveTokenAsync(string token)
    {
        jwtToken = token;
        await js.InvokeVoidAsync("localStorage.setItem", "jwtToken", token);
    }
    public void AttachToken(HttpClient client)
{
    if (!string.IsNullOrEmpty(jwtToken))
        client.DefaultRequestHeaders.Authorization =
            new System.Net.Http.Headers.AuthenticationHeaderValue("Bearer", jwtToken);
}
}
