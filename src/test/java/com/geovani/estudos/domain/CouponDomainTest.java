package com.geovani.estudos.domain;

import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;

import static org.junit.jupiter.api.Assertions.*;

class CouponDomainTest {

    private static final OffsetDateTime FUTURE_DATE = OffsetDateTime.now().plusDays(30);

    @Test
    void shouldRemoveSpecialCharactersFromCode() {
        CouponDomain coupon = CouponDomain.create("AB#C-123", "desc", 1.0, FUTURE_DATE, false);
        assertEquals("ABC123", coupon.getCode());
        assertEquals(6, coupon.getCode().length());
    }

    @Test
    void shouldTrimCodeToSixCharacters() {
        CouponDomain coupon = CouponDomain.create("ABCDEFGH", "desc", 1.0, FUTURE_DATE, false);
        assertEquals("ABCDEF", coupon.getCode());
    }

    @Test
    void shouldUppercaseCode() {
        CouponDomain coupon = CouponDomain.create("abc123", "desc", 1.0, FUTURE_DATE, false);
        assertEquals("ABC123", coupon.getCode());
    }

    @Test
    void shouldThrowWhenCodeHasLessThanSixAlphanumericChars() {
        assertThrows(IllegalArgumentException.class,
                () -> CouponDomain.create("AB---", "desc", 1.0, FUTURE_DATE, false));
    }

    @Test
    void shouldThrowWhenDiscountValueIsBelowMinimum() {
        assertThrows(IllegalArgumentException.class,
                () -> CouponDomain.create("ABC123", "desc", 0.4, FUTURE_DATE, false));
    }

    @Test
    void shouldAcceptMinimumDiscountValue() {
        assertDoesNotThrow(() -> CouponDomain.create("ABC123", "desc", 0.5, FUTURE_DATE, false));
    }

    @Test
    void shouldThrowWhenExpirationDateIsInThePast() {
        OffsetDateTime pastDate = OffsetDateTime.now().minusDays(1);
        assertThrows(IllegalArgumentException.class,
                () -> CouponDomain.create("ABC123", "desc", 1.0, pastDate, false));
    }

    @Test
    void shouldCreateCouponWithActiveStatus() {
        CouponDomain coupon = CouponDomain.create("ABC123", "desc", 1.0, FUTURE_DATE, false);
        assertEquals(CouponStatus.ACTIVE, coupon.getStatus());
    }

    @Test
    void shouldCreateCouponWithPublishedTrue() {
        CouponDomain coupon = CouponDomain.create("ABC123", "desc", 1.0, FUTURE_DATE, true);
        assertTrue(coupon.isPublished());
    }

    @Test
    void shouldCreateCouponWithPublishedFalseByDefault() {
        CouponDomain coupon = CouponDomain.create("ABC123", "desc", 1.0, FUTURE_DATE, null);
        assertFalse(coupon.isPublished());
    }

    @Test
    void shouldCreateCouponWithRedeemedFalse() {
        CouponDomain coupon = CouponDomain.create("ABC123", "desc", 1.0, FUTURE_DATE, false);
        assertFalse(coupon.isRedeemed());
    }

    @Test
    void shouldMarkAsDeletedCoupon() {
        CouponDomain coupon = CouponDomain.create("ABC123", "desc", 1.0, FUTURE_DATE, false);
        CouponDomain deleted = coupon.markAsDeleted();
        assertEquals(CouponStatus.DELETED, deleted.getStatus());
    }

    @Test
    void shouldThrowWhenDeletingAlreadyDeletedCoupon() {
        CouponDomain coupon = CouponDomain.create("ABC123", "desc", 1.0, FUTURE_DATE, false);
        CouponDomain deleted = coupon.markAsDeleted();
        assertThrows(IllegalStateException.class, deleted::markAsDeleted);
    }
}
