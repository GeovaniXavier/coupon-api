package com.geovani.estudos.domain;

import com.geovani.estudos.domain.exception.CouponAlreadyDeletedException;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;

import static org.junit.jupiter.api.Assertions.*;

class CouponTest {

    private static final OffsetDateTime FUTURE_DATE = OffsetDateTime.now().plusDays(30);

    @Test
    void shouldRemoveSpecialCharactersFromCode() {
        Coupon coupon = Coupon.create("AB#C-123", "desc", 1.0, FUTURE_DATE, false);
        assertEquals("ABC123", coupon.getCode());
        assertEquals(6, coupon.getCode().length());
    }

    @Test
    void shouldTrimCodeToSixCharacters() {
        Coupon coupon = Coupon.create("ABCDEFGH", "desc", 1.0, FUTURE_DATE, false);
        assertEquals("ABCDEF", coupon.getCode());
    }

    @Test
    void shouldUppercaseCode() {
        Coupon coupon = Coupon.create("abc123", "desc", 1.0, FUTURE_DATE, false);
        assertEquals("ABC123", coupon.getCode());
    }

    @Test
    void shouldThrowWhenCodeHasLessThanSixAlphanumericChars() {
        assertThrows(IllegalArgumentException.class,
                () -> Coupon.create("AB---", "desc", 1.0, FUTURE_DATE, false));
    }

    @Test
    void shouldThrowWhenDiscountValueIsBelowMinimum() {
        assertThrows(IllegalArgumentException.class,
                () -> Coupon.create("ABC123", "desc", 0.4, FUTURE_DATE, false));
    }

    @Test
    void shouldAcceptMinimumDiscountValue() {
        assertDoesNotThrow(() -> Coupon.create("ABC123", "desc", 0.5, FUTURE_DATE, false));
    }

    @Test
    void shouldThrowWhenExpirationDateIsInThePast() {
        OffsetDateTime pastDate = OffsetDateTime.now().minusDays(1);
        assertThrows(IllegalArgumentException.class,
                () -> Coupon.create("ABC123", "desc", 1.0, pastDate, false));
    }

    @Test
    void shouldCreateCouponWithActiveStatus() {
        Coupon coupon = Coupon.create("ABC123", "desc", 1.0, FUTURE_DATE, false);
        assertEquals(CouponStatus.ACTIVE, coupon.getStatus());
    }

    @Test
    void shouldCreateCouponWithPublishedTrue() {
        Coupon coupon = Coupon.create("ABC123", "desc", 1.0, FUTURE_DATE, true);
        assertTrue(coupon.isPublished());
    }

    @Test
    void shouldCreateCouponWithPublishedFalseByDefault() {
        Coupon coupon = Coupon.create("ABC123", "desc", 1.0, FUTURE_DATE, null);
        assertFalse(coupon.isPublished());
    }

    @Test
    void shouldCreateCouponWithRedeemedFalse() {
        Coupon coupon = Coupon.create("ABC123", "desc", 1.0, FUTURE_DATE, false);
        assertFalse(coupon.isRedeemed());
    }

    @Test
    void shouldSoftDeleteCoupon() {
        Coupon coupon = Coupon.create("ABC123", "desc", 1.0, FUTURE_DATE, false);
        coupon.softDelete();
        assertEquals(CouponStatus.DELETED, coupon.getStatus());
    }

    @Test
    void shouldThrowWhenDeletingAlreadyDeletedCoupon() {
        Coupon coupon = Coupon.create("ABC123", "desc", 1.0, FUTURE_DATE, false);
        coupon.softDelete();
        assertThrows(CouponAlreadyDeletedException.class, coupon::softDelete);
    }
}

