using System;
using System.Text.Json;
using DTOs.Loan;
using Microsoft.AspNetCore.Http.Json;

namespace BlazorApp.Services.LoanService;

public class HttpLoanService : ILoanService
{
    private readonly HttpClient client;

    public HttpLoanService(HttpClient client)
    {
        this.client = client;
    }

    public async Task<LoanResponseDTO> CreateLoanAsync(CreateLoanDTO createLoanDTO)
    {
        StringContent content = new StringContent(JsonSerializer.Serialize(createLoanDTO), System.Text.Encoding.UTF8, "application/json");
        HttpResponseMessage httpResponse = await client.PostAsync("loans", content);
        string response = await httpResponse.Content.ReadAsStringAsync();
        if (!httpResponse.IsSuccessStatusCode)
        {
            throw new Exception(response);
        }
        return JsonSerializer.Deserialize<LoanResponseDTO>(response, new JsonSerializerOptions { PropertyNameCaseInsensitive = true })!;
    }
}
