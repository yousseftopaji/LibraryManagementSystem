using System;
using System.Text.Json;
using System.Threading.Tasks;
using Microsoft.JSInterop;
using Moq;
using RichardSzalay.MockHttp;
using Xunit;

namespace BlazorApp.Tests.Services.Auth;

public class AuthProviderTests
{
    [Fact]
    public async Task Login_SavesJwtAndSetsAuthState_WhenServerReturnsValidResponse()
    {
        // Arrange
        var mockHttp = new MockHttpMessageHandler();
        var token = "ey.test.jwt";
        var username = "testuser";

        var responseObj = new { token = token, username = username };
        var json = JsonSerializer.Serialize(responseObj);

        mockHttp.When("*/auth/login").Respond("application/json", json);

        var client = mockHttp.ToHttpClient();
        client.BaseAddress = new Uri("http://localhost/");

        var mockJs = new Mock<IJSRuntime>();
        // Return a ValueTask<IJSVoidResult> compatible with InvokeVoidAsync's expectation
        var jsVoidResult = Mock.Of<Microsoft.JSInterop.Infrastructure.IJSVoidResult>();
        mockJs.Setup(js => js.InvokeAsync<Microsoft.JSInterop.Infrastructure.IJSVoidResult>(It.IsAny<string>(), It.IsAny<object[]>() ) )
              .Returns(new ValueTask<Microsoft.JSInterop.Infrastructure.IJSVoidResult>(Task.FromResult(jsVoidResult)));

        var provider = new AuthProvider(client, mockJs.Object);

        // Act
        await provider.Login(username, "password");

        // Assert: token saved in provider
        Assert.Equal(token, provider.GetToken());

        // Assert: HttpClient default request header has been updated
        var authHeader = client.DefaultRequestHeaders.Authorization;
        Assert.NotNull(authHeader);
        Assert.Equal("Bearer", authHeader!.Scheme);
        Assert.Equal(token, authHeader.Parameter);

        // Assert: authentication state contains the username
        var authState = await provider.GetAuthenticationStateAsync();
        Assert.Equal(username, authState.User.Identity?.Name);

        // Verify localStorage was called with key and token via InvokeAsync<IJSVoidResult>
        mockJs.Verify(js => js.InvokeAsync<Microsoft.JSInterop.Infrastructure.IJSVoidResult>("localStorage.setItem", It.Is<object[]>(a => a.Length == 2 && (string)a[0] == "jwtToken" && (string)a[1] == token)), Times.Once);
    }
}
