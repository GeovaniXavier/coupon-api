package com.geovani.estudos.infrastructure.persistence;

import com.geovani.estudos.domain.Coupon;
import com.geovani.estudos.domain.CouponStatus;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class CouponEntityMapperTest {
    @Test
    void shouldMapDomainToEntityAndBack() {
        Coupon coupon = Coupon.restore(
                UUID.randomUUID(),
                "ABC123",
                "Test Coupon",
                10.0,
                OffsetDateTime.now().plusDays(10),
                CouponStatus.ACTIVE,
                true,
                false
        );
        CouponEntity entity = CouponEntityMapper.toEntity(coupon);
        assertEquals(coupon.getId(), entity.getId());
        assertEquals(coupon.getCode(), entity.getCode());
        assertEquals(coupon.getDescription(), entity.getDescription());
        assertEquals(coupon.getDiscountValue(), entity.getDiscountValue());
        assertEquals(coupon.getExpirationDate(), entity.getExpirationDate());
        assertEquals(coupon.getStatus(), entity.getStatus());
        assertEquals(coupon.isPublished(), entity.isPublished());
        assertEquals(coupon.isRedeemed(), entity.isRedeemed());

        Coupon mappedBack = CouponEntityMapper.toDomain(entity);
        assertEquals(coupon.getId(), mappedBack.getId());
        assertEquals(coupon.getCode(), mappedBack.getCode());
        assertEquals(coupon.getDescription(), mappedBack.getDescription());
        assertEquals(coupon.getDiscountValue(), mappedBack.getDiscountValue());
        assertEquals(coupon.getExpirationDate(), mappedBack.getExpirationDate());
        assertEquals(coupon.getStatus(), mappedBack.getStatus());
        assertEquals(coupon.isPublished(), mappedBack.isPublished());
        assertEquals(coupon.isRedeemed(), mappedBack.isRedeemed());
    }
}
