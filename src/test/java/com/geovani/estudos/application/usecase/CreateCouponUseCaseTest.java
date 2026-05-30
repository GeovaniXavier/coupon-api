package com.geovani.estudos.application.usecase;

import com.geovani.estudos.application.gateway.CouponGateway;
import com.geovani.estudos.domain.Coupon;
import com.geovani.estudos.domain.CouponStatus;
import com.geovani.estudos.domain.ErrorMessages;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateCouponUseCaseTest {

    @Mock
    private CouponGateway couponGateway;

    @InjectMocks
    private CreateCouponUseCase createCouponUseCase;

    private static final OffsetDateTime FUTURE_DATE = OffsetDateTime.now().plusDays(30);

    @Test
    void shouldCreateCouponSuccessfully() {
        CreateCouponCommand command = new CreateCouponCommand(
                "ABC123", "Desconto especial", 1.0, FUTURE_DATE, false
        );
        when(couponGateway.save(any(Coupon.class))).thenAnswer(inv -> inv.getArgument(0));

        Coupon result = createCouponUseCase.execute(command);

        assertNotNull(result);
        assertEquals("ABC123", result.getCode());
        assertEquals("Desconto especial", result.getDescription());
        assertEquals(1.0, result.getDiscountValue());
        assertEquals(CouponStatus.ACTIVE, result.getStatus());
        assertFalse(result.isPublished());
        assertFalse(result.isRedeemed());
        assertNotNull(result.getExpirationDate());
        verify(couponGateway, times(1)).save(any(Coupon.class));
    }

    @Test
    void shouldCreateCouponWithPublishedTrue() {
        CreateCouponCommand command = new CreateCouponCommand(
                "ABC123", "desc", 1.0, FUTURE_DATE, true
        );
        when(couponGateway.save(any(Coupon.class))).thenAnswer(inv -> inv.getArgument(0));

        Coupon result = createCouponUseCase.execute(command);

        assertTrue(result.isPublished());
        assertEquals(CouponStatus.ACTIVE, result.getStatus());
    }

    @Test
    void shouldSanitizeCodeBeforeSaving() {
        CreateCouponCommand command = new CreateCouponCommand(
                "AB#C-123", "desc", 1.0, FUTURE_DATE, false
        );
        ArgumentCaptor<Coupon> captor = ArgumentCaptor.forClass(Coupon.class);
        when(couponGateway.save(captor.capture())).thenAnswer(inv -> inv.getArgument(0));

        Coupon result = createCouponUseCase.execute(command);

        assertEquals(6, result.getCode().length());
        assertEquals("ABC123", result.getCode());
        assertTrue(result.getCode().matches("[A-Z0-9]+"));

        Coupon saved = captor.getValue();
        assertEquals("ABC123", saved.getCode());
    }

    @Test
    void shouldSaveCouponWithCorrectValues() {
        CreateCouponCommand command = new CreateCouponCommand(
                "ABC123", "Desconto", 2.5, FUTURE_DATE, false
        );
        ArgumentCaptor<Coupon> captor = ArgumentCaptor.forClass(Coupon.class);
        when(couponGateway.save(captor.capture())).thenAnswer(inv -> inv.getArgument(0));

        createCouponUseCase.execute(command);

        Coupon saved = captor.getValue();
        assertEquals("ABC123", saved.getCode());
        assertEquals("Desconto", saved.getDescription());
        assertEquals(2.5, saved.getDiscountValue());
        assertEquals(CouponStatus.ACTIVE, saved.getStatus());
        assertFalse(saved.isPublished());
        assertFalse(saved.isRedeemed());
    }

    @Test
    void shouldThrowWhenExpirationDateIsInThePast() {
        CreateCouponCommand command = new CreateCouponCommand(
                "ABC123", "desc", 1.0, OffsetDateTime.now().minusDays(1), false
        );

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> createCouponUseCase.execute(command));

        assertEquals(ErrorMessages.COUPON_EXPIRATION_DATE_PAST, ex.getMessage());
        verify(couponGateway, never()).save(any());
    }

    @Test
    void shouldThrowWhenDiscountValueIsBelowMinimum() {
        CreateCouponCommand command = new CreateCouponCommand(
                "ABC123", "desc", 0.3, FUTURE_DATE, false
        );

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> createCouponUseCase.execute(command));

        assertEquals(ErrorMessages.COUPON_DISCOUNT_MIN_VALUE, ex.getMessage());
        verify(couponGateway, never()).save(any());
    }

    @Test
    void shouldThrowWhenCodeTooShortAfterSanitization() {
        CreateCouponCommand command = new CreateCouponCommand(
                "A-B", "desc", 1.0, FUTURE_DATE, false
        );

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> createCouponUseCase.execute(command));

        assertEquals(ErrorMessages.COUPON_CODE_MIN_LENGTH, ex.getMessage());
        verify(couponGateway, never()).save(any());
    }
}
