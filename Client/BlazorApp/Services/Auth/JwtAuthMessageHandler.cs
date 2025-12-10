using System;
using System.Net.Http.Headers;
using Microsoft.JSInterop;


namespace BlazorApp.Services.Auth;

public class JwtAuthMessageHandler : DelegatingHandler
{
    private readonly IJSRuntime js;

    public JwtAuthMessageHandler(IJSRuntime js)
    {
        this.js = js;
    }

    protected override async Task<HttpResponseMessage> SendAsync(
        HttpRequestMessage request,
        CancellationToken cancellationToken)
    {
        // Read token from local storage
        string? token = await js.InvokeAsync<string>("localStorage.getItem", "authToken");

        if (!string.IsNullOrEmpty(token))
        {
            request.Headers.Authorization =
                new AuthenticationHeaderValue("Bearer", token);
        }

        return await base.SendAsync(request, cancellationToken);
    }
}