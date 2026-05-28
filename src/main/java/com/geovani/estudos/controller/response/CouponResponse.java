package com.geovani.estudos.controller.response;

import com.geovani.estudos.domain.Coupon;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

@Schema(description = "Dados do cupom retornado pela API")
public record CouponResponse(

        @Schema(description = "Identificador único do cupom (UUID)", example = "cef9d1e3-aae5-4ab6-a297-358c6032b1e7")
        UUID id,

        @Schema(description = "Código alfanumérico do cupom com 6 caracteres", example = "ABC123")
        String code,

        @Schema(description = "Descrição do cupom", example = "Desconto de verão")
        String description,

        @Schema(description = "Valor do desconto", example = "0.8")
        Double discountValue,

        @Schema(description = "Data de expiração no formato ISO-8601", example = "2027-12-31T23:59:59")
        String expirationDate,

        @Schema(description = "Status atual do cupom", example = "ACTIVE", allowableValues = {"ACTIVE", "INACTIVE", "DELETED"})
        String status,

        @Schema(description = "Indica se o cupom está publicado", example = "false")
        boolean published,

        @Schema(description = "Indica se o cupom foi resgatado", example = "false")
        boolean redeemed
) {
    public static CouponResponse from(Coupon coupon) {
        return new CouponResponse(
                coupon.getId(),
                coupon.getCode(),
                coupon.getDescription(),
                coupon.getDiscountValue(),
                coupon.getExpirationDate().toString(),
                coupon.getStatus().name(),
                coupon.isPublished(),
                coupon.isRedeemed()
        );
    }
}
