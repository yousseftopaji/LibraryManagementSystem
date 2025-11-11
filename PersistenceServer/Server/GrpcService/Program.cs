using GrpcService.DatabaseService;
using GrpcService.Services;
using Npgsql;

var builder = WebApplication.CreateBuilder(args);

// Add gRPC support
builder.Services.AddGrpc();

// Register DatabaseService so gRPC services can use it
builder.Services.AddSingleton<DBService>();

// Read appsettings.json connection string for debugging
var connectionString = builder.Configuration.GetConnectionString("LibraryDb");
Console.WriteLine($"Connected to PostgreSQL: {connectionString}");

// Configure Kestrel to listen on port 9090 (HTTP/2)
builder.WebHost.ConfigureKestrel(options =>
{
    // options.ListenLocalhost(9090, o => o.Protocols = Microsoft.AspNetCore.Server.Kestrel.Core.HttpProtocols.Http2);
    options.ListenAnyIP(9090);
});

var app = builder.Build();

// Map gRPC services
// Configure the HTTP request pipeline.
app.MapGrpcService<BookServiceImpl>();
app.MapGet("/", () => "Communication with gRPC endpoints must be made through a gRPC client. To learn how to create a client, visit: https://go.microsoft.com/fwlink/?linkid=2086909");

app.Run();
