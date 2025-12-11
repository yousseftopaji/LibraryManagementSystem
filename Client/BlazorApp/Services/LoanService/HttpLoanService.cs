using System;
using System.Text.Json;
using DTOs.Loan;
using Microsoft.AspNetCore.Http.Json;

namespace BlazorApp.Services.LoanService;

public class HttpLoanService : ILoanService
{
    private readonly HttpClient client;
    private readonly AuthProvider authProvider;

     public HttpLoanService(HttpClient  client, AuthProvider authProvider)
    {
        this.client = client;
        this.authProvider = authProvider;
    }
    public async Task<LoanDTO> CreateLoanAsync(CreateLoanDTO createLoanDto)
    {
        authProvider.AttachToken(client);
        HttpResponseMessage httpResponse = await client.PostAsJsonAsync("loans", createLoanDto);
        string response = await httpResponse.Content.ReadAsStringAsync();
        if (!httpResponse.IsSuccessStatusCode)
        {
            throw new Exception($"Error creating loan: {response}");
        }
        return JsonSerializer.Deserialize<LoanDTO>(response, new JsonSerializerOptions { PropertyNameCaseInsensitive = true })!;
    }

 public async Task<bool> ExtendLoanAsync(int loanId)
    {authProvider.AttachToken(client);
        HttpResponseMessage httpResponse = await client.PatchAsync($"loans/{loanId}", null);

        if (httpResponse.IsSuccessStatusCode)
            return true;

        // Optional error message for UI:
        string error = await httpResponse.Content.ReadAsStringAsync();
        Console.WriteLine($"Error extending loan: {error}");

        return false;
    }
}