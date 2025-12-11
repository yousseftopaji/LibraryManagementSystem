using BlazorApp.Components;
using BlazorApp.Services;
using BlazorApp.Services.LoanService;
using BlazorApp.Services.ReservationService;
using Microsoft.AspNetCore.Components.Authorization;

var builder = WebApplication.CreateBuilder(args);


// Add services to the container.
builder.Services.AddRazorComponents()
    .AddInteractiveServerComponents();

// Unauthenticated client
builder.Services.AddScoped(sp => new HttpClient
{
    BaseAddress = new Uri("http://localhost:8081/")
});

builder.Services.AddScoped<IBookService, HttpBookService>();
builder.Services.AddScoped<ILoanService, HttpLoanService>();
builder.Services.AddScoped<IReservationService,HttpReservationService>();
builder.Services.AddScoped<AuthProvider>();
builder.Services.AddScoped<AuthenticationStateProvider>(sp => sp.GetRequiredService<AuthProvider>());
builder.Services.AddScoped<JwtAuthMessageHandler>();

// Authenticated HTTP client
builder.Services.AddHttpClient("AuthorizedClient", client =>
{
    client.BaseAddress = new Uri("http://localhost:8081/");
})
.AddHttpMessageHandler<JwtAuthMessageHandler>();

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
