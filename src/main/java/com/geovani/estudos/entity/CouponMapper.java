package com.geovani.estudos.entity;

import com.geovani.estudos.domain.CouponDomain;

public class CouponMapper {

    private CouponMapper() {
    }


    public static CouponEntity toEntity(CouponDomain domain) {
        CouponEntity entity = new CouponEntity();

        entity.setId(domain.getId());
        entity.setCode(domain.getCode());
        entity.setDescription(domain.getDescription());
        entity.setDiscountValue(domain.getDiscountValue());
        entity.setExpirationDate(domain.getExpirationDate());
        entity.setStatus(domain.getStatus());
        entity.setPublished(domain.isPublished());
        entity.setRedeemed(domain.isRedeemed());

        return entity;
    }

    public static CouponDomain toDomain(CouponEntity entity) {
        return new CouponDomain(
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

