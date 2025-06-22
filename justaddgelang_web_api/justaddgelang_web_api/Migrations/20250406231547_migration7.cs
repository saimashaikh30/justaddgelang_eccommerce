using Microsoft.EntityFrameworkCore.Migrations;

#nullable disable

namespace justaddgelang_web_api.Migrations
{
    public partial class migration7 : Migration
    {
        protected override void Up(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.AddColumn<string>(
                name: "temp_order_id",
                table: "orders",
                type: "text",
                nullable: true);
        }

        protected override void Down(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropColumn(
                name: "temp_order_id",
                table: "orders");
        }
    }
}
