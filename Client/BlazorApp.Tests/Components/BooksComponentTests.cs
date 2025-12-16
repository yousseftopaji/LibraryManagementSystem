using System.Collections.Generic;
using System.Security.Claims;
using System.Threading.Tasks;
using Bunit;
using DTOs.Book;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Components.Authorization;
using Microsoft.Extensions.DependencyInjection;
using Moq;
using BlazorApp.Services;
using BlazorApp.Components.Pages;
using Xunit;

namespace BlazorApp.Tests.Components;

// Test helpers to satisfy AuthorizeView dependencies
public class AllowAllAuthorizationPolicyProvider : IAuthorizationPolicyProvider
{
    public Task<AuthorizationPolicy?> GetPolicyAsync(string policyName)
        => Task.FromResult<AuthorizationPolicy?>(new AuthorizationPolicyBuilder().RequireAssertion(_ => true).Build());

    public Task<AuthorizationPolicy> GetDefaultPolicyAsync()
        => Task.FromResult(new AuthorizationPolicyBuilder().RequireAssertion(_ => true).Build());

    public Task<AuthorizationPolicy?> GetFallbackPolicyAsync()
        => Task.FromResult<AuthorizationPolicy?>(null);
}

public class AllowAllAuthorizationService : IAuthorizationService
{
    public Task<AuthorizationResult> AuthorizeAsync(ClaimsPrincipal user, object? resource, IEnumerable<IAuthorizationRequirement> requirements)
        => Task.FromResult(AuthorizationResult.Success());

    public Task<AuthorizationResult> AuthorizeAsync(ClaimsPrincipal user, object? resource, string policyName)
        => Task.FromResult(AuthorizationResult.Success());
}

public class TestAuthenticationStateProvider : AuthenticationStateProvider
{
    public override Task<AuthenticationState> GetAuthenticationStateAsync()
    {
        var identity = new ClaimsIdentity(new[] { new Claim(ClaimTypes.Name, "testuser") }, "test");
        var user = new ClaimsPrincipal(identity);
        return Task.FromResult(new AuthenticationState(user));
    }
}

public class BooksComponentTests
{
    [Fact]
    public void Books_RendersList_WhenServiceReturnsBooks()
    {
        using var ctx = new Bunit.BunitContext();

        // Register minimal auth services so AuthorizeView can instantiate
        ctx.Services.AddSingleton<IAuthorizationPolicyProvider>(new AllowAllAuthorizationPolicyProvider());
        ctx.Services.AddSingleton<IAuthorizationService>(new AllowAllAuthorizationService());
        ctx.Services.AddSingleton<AuthenticationStateProvider>(new TestAuthenticationStateProvider());

        // Arrange
        var mockBookService = new Mock<IBookService>();
        mockBookService.Setup(s => s.GetBooksAsync()).ReturnsAsync(new List<BookDTO>
        {
            new BookDTO { ISBN = "111", Title = "Book One", Author = "Author A", State = "Available", Genre = new List<GenreDTO>() },
            new BookDTO { ISBN = "222", Title = "Book Two", Author = "Author B", State = "Available", Genre = new List<GenreDTO>() }
        });

        ctx.Services.AddSingleton(mockBookService.Object);

        // Act
        var cascade = ctx.Render<Microsoft.AspNetCore.Components.Authorization.CascadingAuthenticationState>(parameters =>
            parameters.AddChildContent<Books>());

        var booksComponent = cascade.FindComponent<Books>();

        // Assert - check that the titles appear
        Assert.Contains("Book One", booksComponent.Markup);
        Assert.Contains("Book Two", booksComponent.Markup);
    }
}
