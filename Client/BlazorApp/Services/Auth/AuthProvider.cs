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
    public async Task Register(string fullName, string phoneNumber, string userName, string email, string password)
    {
        var request = new RegisterRequest()
        {
            FullName = fullName,
            PhoneNumber = phoneNumber,
            UserName = userName,
            Email = email,
            Password = password
        };

        var response = await client.PostAsJsonAsync("auth/register", request);
        string content = await response.Content.ReadAsStringAsync();

        if (!response.IsSuccessStatusCode)
            throw new Exception(content);
    }

    // LOGIN
    public async Task Login(string username, string password)
    {
        var response = await client.PostAsJsonAsync("auth/login", new LoginRequest(username, password));
        string content = await response.Content.ReadAsStringAsync();

        if (!response.IsSuccessStatusCode)
            throw new Exception(content);

        var loginResponse = JsonSerializer.Deserialize<LoginResponse>(content, new JsonSerializerOptions
        {
            PropertyNameCaseInsensitive = true
        });

    
        if (loginResponse == null || string.IsNullOrEmpty(loginResponse.Token))
            throw new Exception("Invalid login response");

        if (loginResponse.Username == null)
            throw new Exception("Invalid username.");

        await SaveTokenAsync(loginResponse.Token);
        AttachToken(client);

        // Build claims
        var claims = new List<Claim>()
        {
            new Claim(ClaimTypes.Name, loginResponse.Username ?? "User")
            // new Claim(ClaimTypes.Role, loginResponse.User.Role ?? "User"),
            // new Claim("FullName", loginResponse.User.Name ?? ""),
            // new Claim("Email", loginResponse.User.Email ?? ""),
            // new Claim("PhoneNumber", loginResponse.User.PhoneNumber ?? "")
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


// method to restore authentication from stored JWT
    public async Task RestoreFromTokenAsync()
    {
        var storedToken = await js.InvokeAsync<string>("localStorage.getItem", "jwtToken");

        if (!string.IsNullOrEmpty(storedToken))
        {
            try
            {
                var handler = new JwtSecurityTokenHandler();
                var jwt = handler.ReadJwtToken(storedToken);

                if (jwt.ValidTo > DateTime.UtcNow)
                {
                    jwtToken = storedToken;

                    // Map server-sent username to ClaimTypes.Name
                    var claims = new List<Claim>();
                    var username = jwt.Claims.FirstOrDefault(c => c.Type == "sub")?.Value;
                    if (!string.IsNullOrEmpty(username))
                        claims.Add(new Claim(ClaimTypes.Name, username));

                    // Add all other claims (except "username" to avoid duplicates)
                    claims.AddRange(jwt.Claims.Where(c => c.Type != "sub"));

                    currentClaimsPrincipal = new ClaimsPrincipal(new ClaimsIdentity(claims, "jwt"));
                }
                else
                {
                    // Token expired, remove it
                    await js.InvokeVoidAsync("localStorage.removeItem", "jwtToken");
                    currentClaimsPrincipal = new ClaimsPrincipal(new ClaimsIdentity());
                }
            }
            catch
            {
                // Invalid token
                await js.InvokeVoidAsync("localStorage.removeItem", "jwtToken");
                currentClaimsPrincipal = new ClaimsPrincipal(new ClaimsIdentity());
            }
        }
        else
        {
            // No token found
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
