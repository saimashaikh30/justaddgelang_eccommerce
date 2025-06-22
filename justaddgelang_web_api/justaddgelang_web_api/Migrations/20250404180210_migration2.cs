using Microsoft.EntityFrameworkCore.Migrations;

#nullable disable

namespace justaddgelang_web_api.Migrations
{
    public partial class migration2 : Migration
    {
        protected override void Up(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.AddColumn<string>(
                name: "city",
                table: "user_addresses",
                type: "varchar(100)",
                nullable: false,
                defaultValue: "");

            migrationBuilder.AddColumn<string>(
                name: "pincode",
                table: "user_addresses",
                type: "varchar(10)",
                nullable: false,
                defaultValue: "");
        }

        protected override void Down(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropColumn(
                name: "city",
                table: "user_addresses");

            migrationBuilder.DropColumn(
                name: "pincode",
                table: "user_addresses");
        }
    }
}
