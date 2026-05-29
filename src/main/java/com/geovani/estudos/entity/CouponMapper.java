package com.geovani.estudos.entity;

import com.geovani.estudos.domain.CouponDomain;
import com.geovani.estudos.domain.CouponStatus;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.UUID;

public class CouponMapper {
    public static CouponEntity toEntity(CouponDomain domain) {
        CouponEntity entity = new CouponEntity();
        entity.setId(domain.getId());
        entity.setCode(domain.getCode());
        entity.setDescription(domain.getDescription());
        entity.setDiscountValue(domain.getDiscountValue());
        entity.setExpirationDate(domain.getExpirationDate().toLocalDateTime());
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
                entity.getExpirationDate().atOffset(OffsetDateTime.now().getOffset()),
                entity.getStatus(),
                entity.isPublished(),
                entity.isRedeemed()
        );
    }
}

