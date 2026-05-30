package com.geovani.estudos.infrastructure.persistence;

import com.geovani.estudos.domain.Coupon;
import com.geovani.estudos.domain.CouponStatus;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class CouponEntityTest {
    @Test
    void shouldSetAndGetAllFields() {
        CouponEntity entity = new CouponEntity();
        UUID id = UUID.randomUUID();
        String code = "CODE01";
        String description = "desc";
        double discount = 5.0;
        OffsetDateTime expiration = OffsetDateTime.now().plusDays(5);
        CouponStatus status = CouponStatus.ACTIVE;
        boolean published = true;
        boolean redeemed = false;

        entity.setId(id);
        entity.setCode(code);
        entity.setDescription(description);
        entity.setDiscountValue(discount);
        entity.setExpirationDate(expiration);
        entity.setStatus(status);
        entity.setPublished(published);
        entity.setRedeemed(redeemed);

        assertEquals(id, entity.getId());
        assertEquals(code, entity.getCode());
        assertEquals(description, entity.getDescription());
        assertEquals(discount, entity.getDiscountValue());
        assertEquals(expiration, entity.getExpirationDate());
        assertEquals(status, entity.getStatus());
        assertEquals(published, entity.isPublished());
        assertEquals(redeemed, entity.isRedeemed());
    }
}
