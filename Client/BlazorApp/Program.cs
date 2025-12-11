using BlazorApp.Components;
using BlazorApp.Services;
using BlazorApp.Services.LoanService;
using Microsoft.AspNetCore.Components.Authorization;

var builder = WebApplication.CreateBuilder(args);

// Add services to the container.
builder.Services.AddRazorComponents()
    .AddInteractiveServerComponents();

// Register AuthProvider and AuthenticationStateProvider
builder.Services.AddScoped<AuthProvider>();
builder.Services.AddScoped<AuthenticationStateProvider>(sp => sp.GetRequiredService<AuthProvider>());

// JWT message handler
builder.Services.AddScoped<JwtAuthMessageHandler>();

// Named HttpClient with JWT handler for authorized requests
builder.Services.AddHttpClient("AuthorizedClient", client =>
{
    client.BaseAddress = new Uri("http://localhost:8081/");
})
.AddHttpMessageHandler<JwtAuthMessageHandler>();

// Services using the authorized HttpClient
builder.Services.AddScoped<IBookService, HttpBookService>(sp =>
{
    var factory = sp.GetRequiredService<IHttpClientFactory>();
    return new HttpBookService(factory);
});

builder.Services.AddScoped<ILoanService, HttpLoanService>(sp =>
{
    var factory = sp.GetRequiredService<IHttpClientFactory>();
    return new HttpLoanService(factory);
});


// Optional: a plain HttpClient if you need unauthenticated calls
builder.Services.AddScoped(sp =>
{
    var client = new HttpClient
    {
        BaseAddress = new Uri("http://localhost:8081/")
    };
    return client;
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
