package com.geovani.estudos.domain;

import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.UUID;

public class CouponDomain {
    private final UUID id;
    private final String code;
    private final String description;
    private final Double discountValue;
    private final OffsetDateTime expirationDate;
    private final CouponStatus status;
    private final boolean published;
    private final boolean redeemed;

    public CouponDomain(UUID id, String code, String description, Double discountValue, OffsetDateTime expirationDate, CouponStatus status, boolean published, boolean redeemed) {
        this.id = id;
        this.code = code;
        this.description = description;
        this.discountValue = discountValue;
        this.expirationDate = expirationDate;
        this.status = status;
        this.published = published;
        this.redeemed = redeemed;
    }

    public static CouponDomain create(String rawCode, String description, Double discountValue, OffsetDateTime expirationDate, Boolean published) {
        String sanitized = sanitize(rawCode);
        validateCode(sanitized);
        validateDiscountValue(discountValue);
        validateExpirationDate(expirationDate);
        return new CouponDomain(
                null,
                sanitized.substring(0, 6),
                description,
                discountValue,
                expirationDate,
                CouponStatus.ACTIVE,
                published != null && published,
                false
        );
    }

    public CouponDomain softDelete() {
        if (this.status == CouponStatus.DELETED) {
            throw new IllegalStateException("O cupom já foi deletado.");
        }
        return new CouponDomain(id, code, description, discountValue, expirationDate, CouponStatus.DELETED, published, redeemed);
    }

    private static String sanitize(String rawCode) {
        String sanitized = rawCode.replaceAll("[^a-zA-Z0-9]", "").toUpperCase();
        if (sanitized.length() < 6) {
            throw new IllegalArgumentException("O código do cupom deve conter ao menos 6 caracteres alfanuméricos.");
        }
        return sanitized;
    }

    private static void validateCode(String sanitizedCode) {
        if (sanitizedCode.length() < 6) {
            throw new IllegalArgumentException("O código do cupom deve conter ao menos 6 caracteres alfanuméricos.");
        }
    }

    private static void validateDiscountValue(Double discountValue) {
        if (discountValue == null || discountValue < 0.5) {
            throw new IllegalArgumentException("O valor de desconto mínimo é 0.5.");
        }
    }

    private static void validateExpirationDate(OffsetDateTime expirationDate) {
        if (expirationDate == null || expirationDate.isBefore(OffsetDateTime.now())) {
            throw new IllegalArgumentException("A data de expiração não pode estar no passado.");
        }
    }

    public UUID getId() { return id; }
    public String getCode() { return code; }
    public String getDescription() { return description; }
    public Double getDiscountValue() { return discountValue; }
    public OffsetDateTime getExpirationDate() { return expirationDate; }
    public CouponStatus getStatus() { return status; }
    public boolean isPublished() { return published; }
    public boolean isRedeemed() { return redeemed; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CouponDomain that = (CouponDomain) o;
        return published == that.published && redeemed == that.redeemed && Objects.equals(id, that.id) && Objects.equals(code, that.code) && Objects.equals(description, that.description) && Objects.equals(discountValue, that.discountValue) && Objects.equals(expirationDate, that.expirationDate) && status == that.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, code, description, discountValue, expirationDate, status, published, redeemed);
    }
}
