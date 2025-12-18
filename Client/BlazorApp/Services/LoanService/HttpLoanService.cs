using System;
using System.Collections.Generic;
using System.Net.Http;
using System.Net.Http.Json;
using System.Text.Json;
using System.Threading.Tasks;
using DTOs.Extension;
using DTOs.Loan;

namespace BlazorApp.Services.LoanService;

public class HttpLoanService(HttpClient client, AuthProvider authProvider) : ILoanService
{
    private static readonly JsonSerializerOptions JsonOptions = new()
    {
        PropertyNameCaseInsensitive = true
    };

    public async Task<LoanDTO> CreateLoanAsync(CreateLoanDTO createLoanDto)
    {
        authProvider.AttachToken(client);

        var httpResponse = await client.PostAsJsonAsync("loans", createLoanDto);
        
        if (!httpResponse.IsSuccessStatusCode)
        {
            var content = await httpResponse.Content.ReadAsStringAsync();
            throw new HttpRequestException(
                $"CreateLoan failed ({(int)httpResponse.StatusCode} {httpResponse.ReasonPhrase}). {content}");
        }

        var response = await httpResponse.Content.ReadAsStringAsync();
        return JsonSerializer.Deserialize<LoanDTO>(response, JsonOptions)!;
    }

    public async Task ExtendLoanAsync(int loanId)
    {
        authProvider.AttachToken(client);

        var username = authProvider.ExtractUsernameFromJwt() ?? string.Empty;
        var payload = new CreateExtensionDTO(loanId, username);

        var request = new HttpRequestMessage(HttpMethod.Patch, "loans/extensions")
        {
            Content = JsonContent.Create(payload)
        };

        var httpResponse = await client.SendAsync(request);

        if (!httpResponse.IsSuccessStatusCode)
        {
            var content = await httpResponse.Content.ReadAsStringAsync();
            throw new HttpRequestException(
                $"ExtendLoan failed ({(int)httpResponse.StatusCode} {httpResponse.ReasonPhrase}). {content}");
        }
    }

    public async Task<List<LoanDTO>> GetActiveLoansAsync()
    {
        authProvider.AttachToken(client);
        string username = authProvider.ExtractUsernameFromJwt() ?? string.Empty;
        
        var url = $"loans/active?username={Uri.EscapeDataString(username)}";

        var httpResponse = await client.GetAsync(url);

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
