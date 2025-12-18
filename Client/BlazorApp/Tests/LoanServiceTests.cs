using System.Net;
using System.Net.Http.Json;
using System.Text.Json;
using BlazorApp.Services.LoanService;
using DTOs.Loan;
using Xunit;

public class LoanServiceTests
{
    [Fact]
    public async Task GetActiveLoansAsync_Returns_List()
    {
        var loans = new List<LoanDTO>
        {
            new LoanDTO { LoanId = 1, BookId = 10, Username = "user1", BorrowDate = DateTime.UtcNow.AddDays(-5), DueDate = DateTime.UtcNow.AddDays(10), NumberOfExtensions = 0, IsReturned = false }
        };

        var handler = new FakeHttpMessageHandler(req =>
        {
            if (req.RequestUri!.AbsolutePath.Contains("/loans/active") || req.RequestUri!.AbsolutePath.Contains("loans/active"))
            {
                return new HttpResponseMessage(HttpStatusCode.OK)
                {
                    Content = JsonContent.Create(loans, options: new JsonSerializerOptions { PropertyNameCaseInsensitive = true })
                };
            }

            if (req.RequestUri!.AbsolutePath.Contains("/loans") || req.RequestUri!.AbsolutePath.Contains("loans"))
            {
                return new HttpResponseMessage(HttpStatusCode.OK)
                {
                    Content = JsonContent.Create(loans, options: new JsonSerializerOptions { PropertyNameCaseInsensitive = true })
                };
            }

            return new HttpResponseMessage(HttpStatusCode.NotFound);
        });

        var client = new HttpClient(handler) { BaseAddress = new Uri("http://localhost/") };
        var auth = TestAuthHelpers.CreateAuthProvider();
        var service = new HttpLoanService(client, auth);

        var result = await service.GetActiveLoansAsync("user1");

        Assert.NotNull(result);
        Assert.Single(result);
        Assert.Equal("user1", result[0].Username);
    }

    [Fact]
    public async Task ExtendLoanAsync_Returns_True_On_Success()
    {
        var handler = new FakeHttpMessageHandler(req =>
        {
            if (req.Method == HttpMethod.Patch && req.RequestUri!.AbsoluteUri.Contains("/loans/extensions"))
            {
                return new HttpResponseMessage(HttpStatusCode.OK);
            }
            return new HttpResponseMessage(HttpStatusCode.NotFound);
        });

        var client = new HttpClient(handler) { BaseAddress = new Uri("http://localhost/") };
        var auth = TestAuthHelpers.CreateAuthProvider();
        var service = new HttpLoanService(client, auth);

        var result = await service.ExtendLoanAsync(5);

        Assert.True(result);
    }
}

