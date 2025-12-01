using Microsoft.EntityFrameworkCore.Migrations;

#nullable disable

namespace EFCDatabaseRepositories.Migrations
{
    /// <inheritdoc />
    public partial class AddNumberOfExtensionsToLoan : Migration
    {
        /// <inheritdoc />
        protected override void Up(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.AddColumn<int>(
                name: "NumberOfExtensions",
                table: "Loan",
                type: "INTEGER",
                nullable: false,
                defaultValue: 0);
        }

        /// <inheritdoc />
        protected override void Down(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropColumn(
                name: "NumberOfExtensions",
                table: "Loan");
        }
    }
}
