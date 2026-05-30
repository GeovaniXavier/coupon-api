package com.geovani.estudos.infrastructure.persistence;

import com.geovani.estudos.domain.Coupon;

public final class CouponEntityMapper {

    private CouponEntityMapper() {
    }

    public static CouponEntity toEntity(final Coupon coupon) {
        CouponEntity entity = new CouponEntity();
        entity.setId(coupon.getId());
        entity.setCode(coupon.getCode());
        entity.setDescription(coupon.getDescription());
        entity.setDiscountValue(coupon.getDiscountValue());
        entity.setExpirationDate(coupon.getExpirationDate());
        entity.setStatus(coupon.getStatus());
        entity.setPublished(coupon.isPublished());
        entity.setRedeemed(coupon.isRedeemed());
        return entity;
    }

    public static Coupon toDomain(final CouponEntity entity) {
        return Coupon.restore(
                entity.getId(),
                entity.getCode(),
                entity.getDescription(),
                entity.getDiscountValue(),
                entity.getExpirationDate(),
                entity.getStatus(),
                entity.isPublished(),
                entity.isRedeemed()
        );
    }
}
