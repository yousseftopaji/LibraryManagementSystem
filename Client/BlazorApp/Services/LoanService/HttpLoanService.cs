using System;
using System.Text.Json;
using System.Text.RegularExpressions;
using System.Collections.Generic;
using System.Threading.Tasks;
using DTOs.Loan;
using DTOs.Error_Handler;

namespace BlazorApp.Services.LoanService;

public class HttpLoanService : ILoanService
{
    private readonly HttpClient _client;
    private readonly AuthProvider _authProvider;

    public HttpLoanService(HttpClient client, AuthProvider authProvider)
    {
        _client = client;
        _authProvider = authProvider;
    }

    private string StripHtml(string html)
    {
        if (string.IsNullOrEmpty(html)) return html;
        // Remove script/style blocks
        var withoutScripts = Regex.Replace(html, @"<(script|style)[^>]*>[\s\S]*?<\/(script|style)>", string.Empty, RegexOptions.IgnoreCase);
        // Remove tags
        var text = Regex.Replace(withoutScripts, @"<[^>]+>", string.Empty);
        // Collapse whitespace
        text = Regex.Replace(text, @"\s+", " ").Trim();
        return text;
    }

    private async Task<string> ExtractFriendlyErrorAsync(HttpResponseMessage response)
    {
        string content = await response.Content.ReadAsStringAsync();
        if (string.IsNullOrEmpty(content))
            return response.ReasonPhrase ?? "An unknown error occurred.";

        // Try to parse known JSON ErrorResponseDTO
        try
        {
            var parsed = JsonSerializer.Deserialize<ErrorResponseDTO>(content, new JsonSerializerOptions { PropertyNameCaseInsensitive = true });
            if (parsed != null)
                return parsed.Details ?? parsed.Message ?? parsed.ErrorCode ?? content;
        }
        catch
        {
            // Not the expected JSON format - continue
        }

        // If looks like HTML, strip tags and return the first meaningful snippet
        var lower = content.Length > 1000 ? content.Substring(0, 1000).ToLowerInvariant() : content.ToLowerInvariant();
        if (lower.Contains("<html") || lower.Contains("<!doctype html") || lower.Contains("<head") || lower.Contains("<body"))
        {
            var text = StripHtml(content);
            if (!string.IsNullOrEmpty(text))
            {
                // Return first 200 characters of the textual content
                return text.Length > 200 ? text.Substring(0, 200) + "..." : text;
            }
        }

        // Fallback to trimmed content (plain text or other formats)
        var trimmed = content.Length > 500 ? content.Substring(0, 500) + "..." : content;
        return trimmed;
    }

    public async Task<LoanDTO> CreateLoanAsync(CreateLoanDTO createLoanDto)
    {
        _authProvider.AttachToken(_client);
        HttpResponseMessage httpResponse = await _client.PostAsJsonAsync("loans", createLoanDto);
        string response = await httpResponse.Content.ReadAsStringAsync();
        if (!httpResponse.IsSuccessStatusCode)
        {
            var friendly = await ExtractFriendlyErrorAsync(httpResponse);
            throw new Exception(friendly);
        }
        return JsonSerializer.Deserialize<LoanDTO>(response, new JsonSerializerOptions { PropertyNameCaseInsensitive = true })!;
    }

    public async Task<bool> ExtendLoanAsync(int loanId)
    {
        _authProvider.AttachToken(_client);

        // Build the extension DTO expected by the server: { loanId, username }
        var username = _authProvider.ExtractUsernameFromJwt() ?? string.Empty;
        var extensionPayload = new { loanId, username };

        // Build absolute Uri from client's BaseAddress to avoid hitting the Blazor host static assets pipeline
        var baseAddress = _client.BaseAddress?.ToString()?.TrimEnd('/');
        if (string.IsNullOrEmpty(baseAddress))
            throw new InvalidOperationException("HttpClient.BaseAddress is not configured for loan requests.");

        var requestUri = new Uri($"{baseAddress}/loans/extensions", UriKind.Absolute);

        HttpRequestMessage request = new HttpRequestMessage(HttpMethod.Patch, requestUri)
        {
            Content = JsonContent.Create(extensionPayload)
        };

        HttpResponseMessage httpResponse = await _client.SendAsync(request);

        if (httpResponse.IsSuccessStatusCode)
            return true;

        // Log and extract detailed error information for debugging
        var fullResponse = await httpResponse.Content.ReadAsStringAsync();
        var friendly = await ExtractFriendlyErrorAsync(httpResponse);
        Console.WriteLine($"Error extending loan. Request URL: {requestUri}");
        Console.WriteLine($"Status: {(int)httpResponse.StatusCode} {httpResponse.ReasonPhrase}");
        Console.WriteLine($"Raw response body: {fullResponse}");
        Console.WriteLine($"Friendly message: {friendly}");

        return false;
    }

    public async Task<List<LoanDTO>> GetActiveLoansAsync(string username)
    {
        _authProvider.AttachToken(_client);
        var url1 = $"loans/active?username={Uri.EscapeDataString(username)}";
        var url2 = $"loans?username={Uri.EscapeDataString(username)}";

        HttpResponseMessage httpResponse = await _client.GetAsync(url1);

        if (httpResponse.StatusCode == System.Net.HttpStatusCode.NotFound)
        {
            httpResponse = await _client.GetAsync(url2);
        }

        if (!httpResponse.IsSuccessStatusCode)
        {
            var friendly = await ExtractFriendlyErrorAsync(httpResponse);
            throw new Exception(friendly);
        }

        string response = await httpResponse.Content.ReadAsStringAsync();

        return JsonSerializer.Deserialize<List<LoanDTO>>(response, new JsonSerializerOptions { PropertyNameCaseInsensitive = true }) ?? new List<LoanDTO>();
    }
}