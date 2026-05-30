package com.geovani.estudos.application.usecase;

import java.time.OffsetDateTime;

/**
 * Input do caso de uso CreateCoupon. Independente de framework.
 */
public record CreateCouponCommand(
        String code,
        String description,
        Double discountValue,
        OffsetDateTime expirationDate,
        Boolean published
) {
}
