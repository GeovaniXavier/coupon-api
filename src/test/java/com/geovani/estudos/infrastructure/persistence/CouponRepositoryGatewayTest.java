package com.geovani.estudos.infrastructure.persistence;

import com.geovani.estudos.domain.Coupon;
import com.geovani.estudos.domain.CouponStatus;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CouponRepositoryGatewayTest {
    @Mock
    private CouponJpaRepository jpaRepository;
    @InjectMocks
    private CouponRepositoryGateway gateway;

    @Test
    void shouldSaveCoupon() {
        Coupon coupon = Coupon.restore(UUID.randomUUID(), "CODE01", "desc", 5.0, OffsetDateTime.now().plusDays(5), CouponStatus.ACTIVE, true, false);
        CouponEntity entity = CouponEntityMapper.toEntity(coupon);
        when(jpaRepository.save(any(CouponEntity.class))).thenReturn(entity);
        Coupon saved = gateway.save(coupon);
        assertEquals(coupon.getCode(), saved.getCode());
    }

    @Test
    void shouldFindCouponById() {
        UUID id = UUID.randomUUID();
        CouponEntity entity = new CouponEntity();
        entity.setId(id);
        entity.setCode("CODE01");
        entity.setDescription("desc");
        entity.setDiscountValue(5.0);
        entity.setExpirationDate(OffsetDateTime.now().plusDays(5));
        entity.setStatus(CouponStatus.ACTIVE);
        entity.setPublished(true);
        entity.setRedeemed(false);
        when(jpaRepository.findById(id)).thenReturn(Optional.of(entity));
        Optional<Coupon> found = gateway.findById(id);
        assertTrue(found.isPresent());
        assertEquals(id, found.get().getId());
    }
}
