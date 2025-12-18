using System.Net.Http.Headers;
using BlazorApp.Services;
using Microsoft.JSInterop;

public static class TestAuthHelpers
{
    public static AuthProvider CreateAuthProvider(string? token = "test-jwt")
    {
        var client = new HttpClient();
        var js = new TestJsRuntime();
        var provider = new AuthProvider(client, js);

        // Use reflection to set private jwtToken for tests or call SaveTokenAsync via reflection
        var field = typeof(AuthProvider).GetField("jwtToken", System.Reflection.BindingFlags.NonPublic | System.Reflection.BindingFlags.Instance);
        field!.SetValue(provider, token);

        return provider;
    }
}

internal class TestJsRuntime : IJSRuntime
{
    public ValueTask<TValue> InvokeAsync<TValue>(string identifier, object?[]? args)
    {
        // localStorage.getItem / setItem are only used during RestoreFromTokenAsync and SaveTokenAsync in tests we won't invoke them.
        return new ValueTask<TValue>(default(TValue)!);
    }

    public ValueTask<TValue> InvokeAsync<TValue>(string identifier, CancellationToken cancellationToken, object?[]? args)
    {
        return new ValueTask<TValue>(default(TValue)!);
    }
}
