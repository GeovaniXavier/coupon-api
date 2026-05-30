package com.geovani.estudos.application.usecase;

import com.geovani.estudos.application.gateway.CouponGateway;
import com.geovani.estudos.domain.Coupon;
import com.geovani.estudos.domain.CouponStatus;
import com.geovani.estudos.domain.ErrorMessages;
import com.geovani.estudos.domain.exception.CouponAlreadyDeletedException;
import com.geovani.estudos.domain.exception.CouponNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
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
class DeleteCouponUseCaseTest {

    @Mock
    private CouponGateway couponGateway;

    @InjectMocks
    private DeleteCouponUseCase deleteCouponUseCase;

    private static final OffsetDateTime FUTURE_DATE = OffsetDateTime.now().plusDays(30);

    @Test
    void shouldSoftDeleteCouponSuccessfully() {
        UUID id = UUID.randomUUID();
        Coupon coupon = Coupon.restore(id, "ABC123", "desc", 1.0,
                FUTURE_DATE, CouponStatus.ACTIVE, false, false);
        when(couponGateway.findById(id)).thenReturn(Optional.of(coupon));
        ArgumentCaptor<Coupon> captor = ArgumentCaptor.forClass(Coupon.class);
        when(couponGateway.save(captor.capture())).thenAnswer(inv -> inv.getArgument(0));

        deleteCouponUseCase.execute(id);

        Coupon saved = captor.getValue();
        assertEquals(CouponStatus.DELETED, saved.getStatus());
        assertEquals("ABC123", saved.getCode());
        assertEquals("desc", saved.getDescription());
        assertEquals(1.0, saved.getDiscountValue());
        assertEquals(id, saved.getId());
        assertFalse(saved.isPublished());
        assertFalse(saved.isRedeemed());
        verify(couponGateway, times(1)).findById(id);
        verify(couponGateway, times(1)).save(any(Coupon.class));
    }

    @Test
    void shouldPreserveAllFieldsAfterSoftDelete() {
        UUID id = UUID.randomUUID();
        Coupon coupon = Coupon.restore(id, "XYZ789", "Cupom premium", 50.0,
                FUTURE_DATE, CouponStatus.ACTIVE, true, false);
        when(couponGateway.findById(id)).thenReturn(Optional.of(coupon));
        ArgumentCaptor<Coupon> captor = ArgumentCaptor.forClass(Coupon.class);
        when(couponGateway.save(captor.capture())).thenAnswer(inv -> inv.getArgument(0));

        deleteCouponUseCase.execute(id);

        Coupon saved = captor.getValue();
        assertEquals(CouponStatus.DELETED, saved.getStatus());
        assertEquals("XYZ789", saved.getCode());
        assertEquals("Cupom premium", saved.getDescription());
        assertEquals(50.0, saved.getDiscountValue());
        assertTrue(saved.isPublished());
    }

    @Test
    void shouldThrowWhenCouponNotFound() {
        UUID id = UUID.randomUUID();
        when(couponGateway.findById(id)).thenReturn(Optional.empty());

        CouponNotFoundException ex = assertThrows(CouponNotFoundException.class,
                () -> deleteCouponUseCase.execute(id));

        assertTrue(ex.getMessage().contains(id.toString()));
        verify(couponGateway, never()).save(any());
    }

    @Test
    void shouldThrowWhenCouponIsAlreadyDeleted() {
        UUID id = UUID.randomUUID();
        Coupon deletedCoupon = Coupon.restore(id, "ABC123", "desc", 1.0,
                FUTURE_DATE, CouponStatus.DELETED, false, false);
        when(couponGateway.findById(id)).thenReturn(Optional.of(deletedCoupon));

        CouponAlreadyDeletedException ex = assertThrows(CouponAlreadyDeletedException.class,
                () -> deleteCouponUseCase.execute(id));

        assertEquals(ErrorMessages.COUPON_ALREADY_DELETED, ex.getMessage());
        verify(couponGateway, never()).save(any());
    }
}
