using EFCDatabaseRepositories;
using GrpcService.Services;
using Microsoft.EntityFrameworkCore;
using Npgsql;
using RepositoryContracts;

var builder = WebApplication.CreateBuilder(args);

// Add gRPC support with detailed logging
builder.Services.AddGrpc(options =>
{
    options.EnableDetailedErrors = true;
});

// Read appsettings.json connection string
var connectionString = builder.Configuration.GetConnectionString("LibraryDb");
Console.WriteLine($"Connected to PostgreSQL: {connectionString}");

// Register repositories for dependency injection
builder.Services.AddScoped<IBookRepository, EfcBookRepository>();
builder.Services.AddScoped<ILoanRepository, EfcLoanRepository>();


// Register DbContext with connection string from appsettings.json
builder.Services.AddDbContext<LibraryDbContext>(options =>
    options.UseNpgsql(connectionString));

var app = builder.Build();

// Map gRPC services
// Configure the HTTP request pipeline.
app.MapGrpcService<BookServiceImpl>();
app.MapGrpcService<LoanServiceImpl>();

// Add gRPC reflection for testing with tools like BloomRPC
app.MapGet("/", () => "Communication with gRPC endpoints must be made through a gRPC client. To learn how to create a client, visit: https://go.microsoft.com/fwlink/?linkid=2086909");

Console.WriteLine("gRPC services registered:");
Console.WriteLine("- BookService available at /BookService");
Console.WriteLine("- LoanService available at /LoanService");

app.Run();
