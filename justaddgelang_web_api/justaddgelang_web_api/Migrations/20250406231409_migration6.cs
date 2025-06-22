using Microsoft.EntityFrameworkCore.Migrations;

#nullable disable

namespace justaddgelang_web_api.Migrations
{
    public partial class migration6 : Migration
    {
        protected override void Up(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.AddColumn<int>(
                name: "address_id",
                table: "orders",
                type: "integer",
                nullable: false,
                defaultValue: 0);

            migrationBuilder.AddColumn<string>(
                name: "recipient_name",
                table: "orders",
                type: "text",
                nullable: true);

            migrationBuilder.AddColumn<string>(
                name: "recipient_number",
                table: "orders",
                type: "text",
                nullable: true);
        }

        protected override void Down(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropColumn(
                name: "address_id",
                table: "orders");

            migrationBuilder.DropColumn(
                name: "recipient_name",
                table: "orders");

            migrationBuilder.DropColumn(
                name: "recipient_number",
                table: "orders");
        }
    }
}
