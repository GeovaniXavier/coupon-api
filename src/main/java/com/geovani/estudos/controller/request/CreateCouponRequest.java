package com.geovani.estudos.controller.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.OffsetDateTime;

@Schema(description = "Dados para criação de um cupom")
public record CreateCouponRequest(

        @Schema(description = "Código do cupom. Caracteres especiais são removidos automaticamente. Mínimo de 6 caracteres alfanuméricos.", example = "ABC-123")
        @NotBlank(message = "é obrigatório") String code,

        @Schema(description = "Descrição do cupom", example = "Desconto de verão")
        @NotBlank(message = "é obrigatório") String description,

        @Schema(description = "Valor do desconto. Mínimo: 0.5", example = "0.8")
        @NotNull(message = "é obrigatório") @DecimalMin(value = "0.5", message = "deve ser no mínimo 0.5") Double discountValue,

        @Schema(description = "Data de expiração no formato ISO-8601", example = "2027-12-31T23:59:59.000Z")
        @NotNull(message = "é obrigatório") OffsetDateTime expirationDate,

        @Schema(description = "Indica se o cupom já será criado como publicado", example = "false", defaultValue = "false")
        Boolean published
) {
}
