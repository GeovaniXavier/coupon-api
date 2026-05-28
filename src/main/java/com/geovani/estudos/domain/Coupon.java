package com.geovani.estudos.domain;

import com.geovani.estudos.domain.exception.CouponAlreadyDeletedException;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "coupons")
public class Coupon {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "code", nullable = false, unique = true, length = 6)
    private String code;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "discount_value", nullable = false)
    private Double discountValue;

    @Column(name = "expiration_date", nullable = false)
    private LocalDateTime expirationDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private CouponStatus status;

    @Column(name = "published", nullable = false)
    private boolean published;

    @Column(name = "redeemed", nullable = false)
    private boolean redeemed;

    protected Coupon() {
    }

    public static Coupon create(String rawCode, String description, Double discountValue, OffsetDateTime expirationDate, Boolean published) {
        String sanitizedCode = sanitize(rawCode);
        validateCode(sanitizedCode);
        validateDiscountValue(discountValue);
        validateExpirationDate(expirationDate);

        Coupon coupon = new Coupon();
        coupon.code = sanitizedCode.substring(0, 6);
        coupon.description = description;
        coupon.discountValue = discountValue;
        coupon.expirationDate = expirationDate.toLocalDateTime();
        coupon.status = CouponStatus.ACTIVE;
        coupon.published = published != null && published;
        coupon.redeemed = false;
        return coupon;
    }

    public void softDelete() {
        if (this.status == CouponStatus.DELETED) {
            throw new CouponAlreadyDeletedException();
        }
        this.status = CouponStatus.DELETED;
    }

    private static String sanitize(String rawCode) {
        return rawCode.replaceAll("[^a-zA-Z0-9]", "").toUpperCase();
    }

    private static void validateCode(String sanitizedCode) {
        if (sanitizedCode.length() < 6) {
            throw new IllegalArgumentException("O código do cupom deve conter ao menos 6 caracteres alfanuméricos.");
        }
    }

    private static void validateDiscountValue(Double discountValue) {
        if (discountValue < 0.5) {
            throw new IllegalArgumentException("O valor de desconto mínimo é 0.5.");
        }
    }

    private static void validateExpirationDate(OffsetDateTime expirationDate) {
        if (expirationDate.isBefore(OffsetDateTime.now())) {
            throw new IllegalArgumentException("A data de expiração não pode estar no passado.");
        }
    }

    public UUID getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public Double getDiscountValue() {
        return discountValue;
    }

    public LocalDateTime getExpirationDate() {
        return expirationDate;
    }

    public CouponStatus getStatus() {
        return status;
    }

    public boolean isPublished() {
        return published;
    }

    public boolean isRedeemed() {
        return redeemed;
    }
}

