using Entities;
using Microsoft.EntityFrameworkCore;

namespace EFCDatabaseRepositories.DBContext;

public class LibraryDbContext : DbContext
{
    public LibraryDbContext(DbContextOptions<LibraryDbContext> options) : base(options) { }

    public DbSet<Book> Book => Set<Book>();
    public DbSet<User> User => Set<User>();
    public DbSet<Genre> Genre => Set<Genre>();
    public DbSet<Loan> Loan => Set<Loan>();
    public DbSet<Reservation> Reservation => Set<Reservation>();

    protected override void OnConfiguring(DbContextOptionsBuilder optionsBuilder)
    {
        if (!optionsBuilder.IsConfigured)
        {
            optionsBuilder.UseSqlite("Data Source=library.db");
        }
    }

    protected override void OnModelCreating(ModelBuilder modelBuilder)
    {
        modelBuilder.Entity<User>().HasKey(u => u.Username);
        modelBuilder.Entity<Genre>().HasKey(g => g.Name);
    }
}
