using System;
using Microsoft.EntityFrameworkCore.Migrations;

#nullable disable

namespace Infrastructure.Migrations
{
    /// <inheritdoc />
    public partial class AddNotificacaoVisualizadaToManutencao : Migration
    {
        /// <inheritdoc />
        protected override void Up(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.AddColumn<DateOnly>(
                name: "data_visualizacao_notificacao_a_vencer",
                schema: "public",
                table: "manutencoes",
                type: "date",
                nullable: true);

            migrationBuilder.AddColumn<DateOnly>(
                name: "data_visualizacao_notificacao_vencida",
                schema: "public",
                table: "manutencoes",
                type: "date",
                nullable: true);
        }

        /// <inheritdoc />
        protected override void Down(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropColumn(
                name: "data_visualizacao_notificacao_a_vencer",
                schema: "public",
                table: "manutencoes");

            migrationBuilder.DropColumn(
                name: "data_visualizacao_notificacao_vencida",
                schema: "public",
                table: "manutencoes");
        }
    }
}
