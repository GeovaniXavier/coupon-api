package com.geovani.estudos.service;

import com.geovani.estudos.controller.request.CreateCouponRequest;
import com.geovani.estudos.controller.response.CouponResponse;
import com.geovani.estudos.domain.CouponDomain;
import com.geovani.estudos.domain.CouponStatus;
import com.geovani.estudos.entity.CouponEntity;
import com.geovani.estudos.entity.CouponMapper;
import com.geovani.estudos.repository.CouponRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateCouponServiceTest {

    @Mock
    private CouponRepository couponRepository;

    @InjectMocks
    private CreateCouponService createCouponService;

    @Test
    void shouldCreateCouponSuccessfully() {
        CreateCouponRequest request = new CreateCouponRequest(
                "ABC123", "Desconto especial", 1.0,
                OffsetDateTime.now().plusDays(30), false
        );
        CouponDomain couponDomain = CouponDomain.create(request.code(), request.description(), request.discountValue(), request.expirationDate(), request.published());
        CouponEntity couponEntity = CouponMapper.toEntity(couponDomain);
        when(couponRepository.save(any(CouponEntity.class))).thenReturn(couponEntity);

        CouponResponse response = createCouponService.execute(request);

        assertNotNull(response);
        assertEquals("ABC123", response.code());
        assertEquals(CouponStatus.ACTIVE.name(), response.status());
        assertFalse(response.redeemed());
        verify(couponRepository, times(1)).save(any(CouponEntity.class));
    }

    @Test
    void shouldSanitizeCodeBeforeSaving() {
        CreateCouponRequest request = new CreateCouponRequest(
                "AB#C-123", "desc", 1.0,
                OffsetDateTime.now().plusDays(30), false
        );
        CouponDomain couponDomain = CouponDomain.create(request.code(), request.description(), request.discountValue(), request.expirationDate(), request.published());
        CouponEntity couponEntity = CouponMapper.toEntity(couponDomain);
        when(couponRepository.save(any(CouponEntity.class))).thenReturn(couponEntity);

        CouponResponse response = createCouponService.execute(request);

        assertEquals(6, response.code().length());
        assertEquals("ABC123", response.code());
        verify(couponRepository, times(1)).save(any(CouponEntity.class));
    }

    @Test
    void shouldThrowWhenExpirationDateIsInThePast() {
        CreateCouponRequest request = new CreateCouponRequest(
                "ABC123", "desc", 1.0,
                OffsetDateTime.now().minusDays(1), false
        );
        assertThrows(IllegalArgumentException.class, () -> createCouponService.execute(request));
        verify(couponRepository, never()).save(any());
    }

    @Test
    void shouldThrowWhenDiscountValueIsBelowMinimum() {
        CreateCouponRequest request = new CreateCouponRequest(
                "ABC123", "desc", 0.3,
                OffsetDateTime.now().plusDays(30), false
        );
        assertThrows(IllegalArgumentException.class, () -> createCouponService.execute(request));
        verify(couponRepository, never()).save(any());
    }
}
