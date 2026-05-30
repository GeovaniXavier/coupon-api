package com.geovani.estudos.domain;

import com.geovani.estudos.domain.exception.CouponAlreadyDeletedException;

import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * Entidade de domínio Coupon (Enterprise Business Rules).
 * Encapsula todas as regras de negócio relativas a cupons,
 * mantendo-se livre de dependências de frameworks externos.
 */
public class Coupon {

    private static final int CODE_LENGTH = 6;
    private static final double MIN_DISCOUNT_VALUE = 0.5;

    private final UUID id;
    private final String code;
    private final String description;
    private final Double discountValue;
    private final OffsetDateTime expirationDate;
    private final CouponStatus status;
    private final boolean published;
    private final boolean redeemed;

    private Coupon(UUID id, String code, String description, Double discountValue,
                   OffsetDateTime expirationDate, CouponStatus status,
                   boolean published, boolean redeemed) {
        this.id = id;
        this.code = code;
        this.description = description;
        this.discountValue = discountValue;
        this.expirationDate = expirationDate;
        this.status = status;
        this.published = published;
        this.redeemed = redeemed;
    }

    public static Coupon create(String rawCode, String description, Double discountValue,
                                OffsetDateTime expirationDate, Boolean published) {
        String sanitizedCode = sanitizeCode(rawCode);
        validateDiscountValue(discountValue);
        validateExpirationDate(expirationDate);

        return new Coupon(
                null,
                sanitizedCode.substring(0, CODE_LENGTH),
                description,
                discountValue,
                expirationDate,
                CouponStatus.ACTIVE,
                Boolean.TRUE.equals(published),
                false
        );
    }

    public static Coupon restore(UUID id, String code, String description, Double discountValue,
                                 OffsetDateTime expirationDate, CouponStatus status,
                                 boolean published, boolean redeemed) {
        return new Coupon(id, code, description, discountValue, expirationDate, status, published, redeemed);
    }

    public Coupon markAsDeleted() {
        if (this.status == CouponStatus.DELETED) {
            throw new CouponAlreadyDeletedException(ErrorMessages.COUPON_ALREADY_DELETED);
        }

        return new Coupon(id, code, description, discountValue, expirationDate,
                CouponStatus.DELETED, published, redeemed);
    }

    private static String sanitizeCode(String rawCode) {
        if (rawCode == null) {
            throw new IllegalArgumentException(ErrorMessages.COUPON_CODE_MIN_LENGTH);
        }
        String sanitized = rawCode.replaceAll("[^a-zA-Z0-9]", "").toUpperCase();
        if (sanitized.length() < CODE_LENGTH) {
            throw new IllegalArgumentException(ErrorMessages.COUPON_CODE_MIN_LENGTH);
        }
        return sanitized;
    }

    private static void validateDiscountValue(Double discountValue) {
        if (discountValue == null || discountValue < MIN_DISCOUNT_VALUE) {
            throw new IllegalArgumentException(ErrorMessages.COUPON_DISCOUNT_MIN_VALUE);
        }
    }

    private static void validateExpirationDate(OffsetDateTime expirationDate) {
        if (expirationDate == null || expirationDate.isBefore(OffsetDateTime.now())) {
            throw new IllegalArgumentException(ErrorMessages.COUPON_EXPIRATION_DATE_PAST);
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
        Coupon that = (Coupon) o;
        return published == that.published && redeemed == that.redeemed
                && Objects.equals(id, that.id) && Objects.equals(code, that.code)
                && Objects.equals(description, that.description)
                && Objects.equals(discountValue, that.discountValue)
                && Objects.equals(expirationDate, that.expirationDate) && status == that.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, code, description, discountValue, expirationDate, status, published, redeemed);
    }
}
