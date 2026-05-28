package com.geovani.estudos.service;

import com.geovani.estudos.domain.Coupon;
import com.geovani.estudos.domain.CouponStatus;
import com.geovani.estudos.domain.exception.CouponAlreadyDeletedException;
import com.geovani.estudos.exception.CouponNotFoundException;
import com.geovani.estudos.repository.CouponRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeleteCouponServiceTest {

    @Mock
    private CouponRepository couponRepository;

    @InjectMocks
    private DeleteCouponService deleteCouponService;

    @Test
    void shouldSoftDeleteCouponSuccessfully() {
        Coupon coupon = Coupon.create("ABC123", "desc", 1.0, OffsetDateTime.now().plusDays(30), false);
        UUID id = UUID.randomUUID();
        when(couponRepository.findById(id)).thenReturn(Optional.of(coupon));
        when(couponRepository.save(any(Coupon.class))).thenReturn(coupon);

        deleteCouponService.execute(id);

        assertEquals(CouponStatus.DELETED, coupon.getStatus());
        verify(couponRepository, times(1)).save(coupon);
    }

    @Test
    void shouldThrowWhenCouponNotFound() {
        UUID id = UUID.randomUUID();
        when(couponRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(CouponNotFoundException.class, () -> deleteCouponService.execute(id));
        verify(couponRepository, never()).save(any());
    }

    @Test
    void shouldThrowWhenCouponIsAlreadyDeleted() {
        Coupon coupon = Coupon.create("ABC123", "desc", 1.0, OffsetDateTime.now().plusDays(30), false);
        coupon.softDelete();
        UUID id = UUID.randomUUID();
        when(couponRepository.findById(id)).thenReturn(Optional.of(coupon));

        assertThrows(CouponAlreadyDeletedException.class, () -> deleteCouponService.execute(id));
        verify(couponRepository, never()).save(any());
    }
}

