using Microsoft.EntityFrameworkCore.Migrations;

#nullable disable

namespace justaddgelang_web_api.Migrations
{
    public partial class migration5 : Migration
    {
        protected override void Up(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropColumn(
                name: "review_picture",
                table: "reviews");
        }

        protected override void Down(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.AddColumn<string>(
                name: "review_picture",
                table: "reviews",
                type: "text",
                nullable: true);
        }
    }
}
