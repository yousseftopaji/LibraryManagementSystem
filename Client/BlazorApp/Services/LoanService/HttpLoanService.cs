using System;
using System.Collections.Generic;
using System.Net.Http;
using System.Net.Http.Json;
using System.Text.Json;
using System.Threading.Tasks;
using DTOs.Loan;

namespace BlazorApp.Services.LoanService;

public class HttpLoanService : ILoanService
{
    private readonly HttpClient _client;
    private readonly AuthProvider _authProvider;

    private static readonly JsonSerializerOptions JsonOptions = new()
    {
        PropertyNameCaseInsensitive = true
    };

    public HttpLoanService(HttpClient client, AuthProvider authProvider)
    {
        _client = client;
        _authProvider = authProvider;
    }

    public async Task<LoanDTO> CreateLoanAsync(CreateLoanDTO createLoanDto)
    {
        _authProvider.AttachToken(_client);

        var httpResponse = await _client.PostAsJsonAsync("loans", createLoanDto);

        // Let the server define the error content; keep client thin
        if (!httpResponse.IsSuccessStatusCode)
        {
            var content = await httpResponse.Content.ReadAsStringAsync();
            throw new HttpRequestException(
                $"CreateLoan failed ({(int)httpResponse.StatusCode} {httpResponse.ReasonPhrase}). {content}");
        }

        var response = await httpResponse.Content.ReadAsStringAsync();
        return JsonSerializer.Deserialize<LoanDTO>(response, JsonOptions)!;
    }

    public async Task<bool> ExtendLoanAsync(int loanId)
    {
        _authProvider.AttachToken(_client);

        var username = _authProvider.ExtractUsernameFromJwt() ?? string.Empty;
        var payload = new { loanId, username };

        var request = new HttpRequestMessage(HttpMethod.Patch, "loans/extensions")
        {
            Content = JsonContent.Create(payload)
        };

        var httpResponse = await _client.SendAsync(request);

        if (!httpResponse.IsSuccessStatusCode)
        {
            var content = await httpResponse.Content.ReadAsStringAsync();
            throw new HttpRequestException(
                $"ExtendLoan failed ({(int)httpResponse.StatusCode} {httpResponse.ReasonPhrase}). {content}");
        }

        return true;
    }

    public async Task<List<LoanDTO>> GetActiveLoansAsync(string username)
    {
        _authProvider.AttachToken(_client);

        // Prefer the "active" endpoint; fallback kept (pure routing fallback, no business logic)
        var url1 = $"loans/active?username={Uri.EscapeDataString(username)}";
        var url2 = $"loans?username={Uri.EscapeDataString(username)}";

        var httpResponse = await _client.GetAsync(url1);

        if (httpResponse.StatusCode == System.Net.HttpStatusCode.NotFound)
            httpResponse = await _client.GetAsync(url2);

        if (!httpResponse.IsSuccessStatusCode)
        {
            var content = await httpResponse.Content.ReadAsStringAsync();
            throw new HttpRequestException(
                $"GetActiveLoans failed ({(int)httpResponse.StatusCode} {httpResponse.ReasonPhrase}). {content}");
        }

        var response = await httpResponse.Content.ReadAsStringAsync();
        return JsonSerializer.Deserialize<List<LoanDTO>>(response, JsonOptions) ?? new List<LoanDTO>();
    }
}
