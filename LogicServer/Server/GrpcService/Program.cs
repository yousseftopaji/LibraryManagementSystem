using GrpcService.Services;

var builder = WebApplication.CreateBuilder(args);

// Add services to the container.
builder.Services.AddGrpc();


builder.WebHost.ConfigureKestrel(options =>
{
    // options.ListenLocalhost(9090, o => o.Protocols = Microsoft.AspNetCore.Server.Kestrel.Core.HttpProtocols.Http2);
    options.ListenAnyIP(9090);
});

var app = builder.Build();

// Configure the HTTP request pipeline.
app.MapGrpcService<BookServiceImpl>();
app.MapGet("/", () => "Communication with gRPC endpoints must be made through a gRPC client. To learn how to create a client, visit: https://go.microsoft.com/fwlink/?linkid=2086909");

app.Run();
