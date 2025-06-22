using justaddgelang_web_api.Models;
using Microsoft.EntityFrameworkCore;

var builder = WebApplication.CreateBuilder(args);

// Add services to the container.

builder.Services.AddControllers();

// Learn more about configuring Swagger/OpenAPI at https://aka.ms/aspnetcore/swashbuckle
builder.Services.AddEndpointsApiExplorer();
builder.Services.AddSwaggerGen();
builder.Services.AddScoped<IEmailService, EmailService>();
var provider = builder.Services.BuildServiceProvider();
var config = provider.GetService<IConfiguration>();

// Add DbContext
builder.Services.AddDbContext<dbcontext>(item =>
    item.UseNpgsql(config.GetConnectionString("conn")));

var app = builder.Build();
app.UseStaticFiles(); 

// Configure the HTTP request pipeline.
if (app.Environment.IsDevelopment())
{
    app.UseSwagger();
    app.UseSwaggerUI();
}

app.UseAuthorization();

app.MapControllers();

app.Run();
