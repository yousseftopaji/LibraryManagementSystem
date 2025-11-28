using EFCDatabaseRepositories.Repositories;
using GrpcService.Services;
using Microsoft.EntityFrameworkCore;
using RepositoryContracts;

var builder = WebApplication.CreateBuilder(args);

// Add gRPC support with detailed logging
builder.Services.AddGrpc(options =>
{
    options.EnableDetailedErrors = true;
});

// Read app settings.json connection string
var connectionString = builder.Configuration.GetConnectionString("LibraryDb");
Console.WriteLine($"Connected to database: {connectionString}");

// Register DbContext
builder.Services.AddDbContext<EFCDatabaseRepositories.DBContext.LibraryDbContext>(options =>
    options.UseSqlite(connectionString));

// Register repositories for dependency injection
builder.Services.AddScoped<IBookRepository, EfcBookRepository>();
builder.Services.AddScoped<ILoanRepository, EfcLoanRepository>();
builder.Services.AddScoped<IUserRepository, EfcUserRepository>();
builder.Services.AddScoped<IReservationRepository, EfcReservationRepository>();

var app = builder.Build();

// Map gRPC services
// Configure the HTTP request pipeline.
app.MapGrpcService<BookServiceImpl>();
app.MapGrpcService<LoanServiceImpl>();
app.MapGrpcService<UserServiceImpl>();
app.MapGrpcService<ReservationServiceImpl>();

// Add gRPC reflection for testing with tools like BloomRPC
app.MapGet("/", () => "Communication with gRPC endpoints must be made through a gRPC client. To learn how to create a client, visit: https://go.microsoft.com/fwlink/?linkid=2086909");

Console.WriteLine("gRPC services registered:");
Console.WriteLine("- BookService available at /BookService");
Console.WriteLine("- LoanService available at /LoanService");
Console.WriteLine("- UserService available at /UserService");
Console.WriteLine("- ReservationService available at /ReservationService");

app.Run();
