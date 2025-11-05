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
// Avoid printing the full connection string (don't expose passwords in logs).
try
{
    var csb = new NpgsqlConnectionStringBuilder(connectionString);
    var maskedPwd = string.IsNullOrEmpty(csb.Password) ? "(none)" : "*****";
    Console.WriteLine($"Connected to PostgreSQL: Host={csb.Host};Port={csb.Port};Database={csb.Database};Username={csb.Username};Password={maskedPwd}");
}
catch
{
    // Fallback if parsing fails
    Console.WriteLine("Connected to PostgreSQL: (connection string present, password masked)");
}

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
