using BlazorApp.Components;
using BlazorApp.Services;
using BlazorApp.Services.LoanService;
using Microsoft.AspNetCore.Components.Authorization;

var builder = WebApplication.CreateBuilder(args);


// Add services to the container.
builder.Services.AddRazorComponents()
    .AddInteractiveServerComponents();

// Unauthenticated client
builder.Services.AddScoped(sp => new HttpClient
{
    BaseAddress = new Uri("http://localhost:8080/")
});

builder.Services.AddScoped<IBookService, HttpBookService>();
builder.Services.AddScoped<ILoanService, HttpLoanService>();
builder.Services.AddScoped<AuthProvider>();
builder.Services.AddScoped<AuthenticationStateProvider>(sp => sp.GetRequiredService<AuthProvider>());
builder.Services.AddScoped<JwtAuthMessageHandler>();

// Authenticated HTTP client
builder.Services.AddHttpClient("AuthorizedClient", client =>
{
    client.BaseAddress = new Uri("http://localhost:8080/");
})
.AddHttpMessageHandler<JwtAuthMessageHandler>();


// Services that require JWT
builder.Services.AddScoped<IBookService, HttpBookService>(sp =>
{
    var factory = sp.GetRequiredService<IHttpClientFactory>();
    return new HttpBookService(factory.CreateClient("AuthorizedClient"));
});

builder.Services.AddScoped<ILoanService, HttpLoanService>(sp =>
{
    var factory = sp.GetRequiredService<IHttpClientFactory>();
    return new HttpLoanService(factory.CreateClient("AuthorizedClient"));
});

var app = builder.Build();

// Configure the HTTP request pipeline.
if (!app.Environment.IsDevelopment())
{
    app.UseExceptionHandler("/Error", createScopeForErrors: true);
    app.UseHsts();
}

app.UseStatusCodePagesWithReExecute("/not-found");
app.UseHttpsRedirection();
app.UseAntiforgery();

app.MapStaticAssets();
app.MapRazorComponents<App>()
    .AddInteractiveServerRenderMode();

app.Run();
