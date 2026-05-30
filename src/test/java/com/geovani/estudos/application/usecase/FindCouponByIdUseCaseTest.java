package com.geovani.estudos.application.usecase;

import com.geovani.estudos.application.gateway.CouponGateway;
import com.geovani.estudos.domain.Coupon;
import com.geovani.estudos.domain.CouponStatus;
import com.geovani.estudos.domain.exception.CouponNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FindCouponByIdUseCaseTest {

    @Mock
    private CouponGateway couponGateway;

    @InjectMocks
    private FindCouponByIdUseCase findCouponByIdUseCase;

    private static final OffsetDateTime FUTURE_DATE = OffsetDateTime.now().plusDays(30);

    @Test
    void shouldReturnCouponWithAllFieldsWhenFound() {
        UUID id = UUID.randomUUID();
        Coupon coupon = Coupon.restore(id, "ABC123", "Desconto especial", 2.5,
                FUTURE_DATE, CouponStatus.ACTIVE, true, false);
        when(couponGateway.findById(id)).thenReturn(Optional.of(coupon));

        Coupon result = findCouponByIdUseCase.execute(id);

        assertNotNull(result);
        assertEquals(id, result.getId());
        assertEquals("ABC123", result.getCode());
        assertEquals("Desconto especial", result.getDescription());
        assertEquals(2.5, result.getDiscountValue());
        assertEquals(CouponStatus.ACTIVE, result.getStatus());
        assertTrue(result.isPublished());
        assertFalse(result.isRedeemed());
        assertNotNull(result.getExpirationDate());
        verify(couponGateway, times(1)).findById(id);
    }

    @Test
    void shouldReturnDeletedCouponWhenFound() {
        UUID id = UUID.randomUUID();
        Coupon deleted = Coupon.restore(id, "DEL123", "desc", 1.0,
                FUTURE_DATE, CouponStatus.DELETED, false, false);
        when(couponGateway.findById(id)).thenReturn(Optional.of(deleted));

        Coupon result = findCouponByIdUseCase.execute(id);

        assertEquals(CouponStatus.DELETED, result.getStatus());
        assertEquals("DEL123", result.getCode());
    }

    @Test
    void shouldThrowCouponNotFoundExceptionWhenCouponDoesNotExist() {
        UUID id = UUID.randomUUID();
        when(couponGateway.findById(id)).thenReturn(Optional.empty());

        CouponNotFoundException ex = assertThrows(CouponNotFoundException.class,
                () -> findCouponByIdUseCase.execute(id));

        assertTrue(ex.getMessage().contains(id.toString()));
        verify(couponGateway, times(1)).findById(id);
    }
}
