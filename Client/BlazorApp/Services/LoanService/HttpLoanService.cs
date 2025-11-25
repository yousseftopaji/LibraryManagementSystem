using System;
using System.Text.Json;
using DTOs.Loan;
using Microsoft.AspNetCore.Http.Json;

namespace BlazorApp.Services.LoanService;

public class HttpLoanService : ILoanService
{
    private readonly HttpClient client;
    private ILoanService _loanServiceImplementation;

    public HttpLoanService(HttpClient client)
    {
        this.client = client;
    }

    public async Task<LoanDTO> CreateLoanAsync(CreateLoanDTO createLoanDto)
    {
        HttpResponseMessage httpResponse = await client.PostAsJsonAsync("loans", createLoanDto);
        string response = await httpResponse.Content.ReadAsStringAsync();
        if (!httpResponse.IsSuccessStatusCode)
        {
            throw new Exception(response);
        }
        return JsonSerializer.Deserialize<LoanDTO>(response, new JsonSerializerOptions { PropertyNameCaseInsensitive = true })!;
    }

    public  async Task<LoanDTO> ExtendLoanAsync(Guid loanId)
    {HttpResponseMessage httpResponse = await client.PostAsync($"loans/{loanId}/extend", null);
        string response = await httpResponse.Content.ReadAsStringAsync();

        if (!httpResponse.IsSuccessStatusCode)
        {
            throw new Exception(response);
        }

        return JsonSerializer.Deserialize<LoanDTO>(response, JsonOptions())
               ?? throw new Exception("Failed to parse extend response.");
    }

    private JsonSerializerOptions JsonOptions()
    {
        return new JsonSerializerOptions
        {
            PropertyNameCaseInsensitive = true
        };
    }
    }

