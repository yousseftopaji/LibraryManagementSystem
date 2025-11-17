using Microsoft.EntityFrameworkCore;
using Entities;

namespace EFCDatabaseRepositories;

public class LibraryDbContext : DbContext
{
    public DbSet<Book> Book => Set<Book>();
    public DbSet<User> User => Set<User>();
    public DbSet<Loan> Loan => Set<Loan>();
    public DbSet<Genre> Genre => Set<Genre>();

    protected override void OnConfiguring(DbContextOptionsBuilder optionsBuilder)
    {
        if (!optionsBuilder.IsConfigured)
        {
            optionsBuilder.UseNpgsql("Host=localhost;Port=5432;Database=postgres;Username=postgres;Password=Via@12345");
        }
    }

    protected override void OnModelCreating(ModelBuilder modelBuilder)
    {
        // Set default schema to kitabkhana
        modelBuilder.HasDefaultSchema("kitabkhana_2");

        modelBuilder.Entity<User>().HasKey(u => u.Username);
        modelBuilder.Entity<Genre>().HasKey(g => g.Name);
    }
}
