package com.geovani.estudos.domain;

import com.geovani.estudos.domain.exception.CouponAlreadyDeletedException;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class CouponTest {

    private static final OffsetDateTime FUTURE_DATE = OffsetDateTime.now().plusDays(30);

    @Test
    void shouldCreateCouponWithAllFieldsCorrectly() {
        Coupon coupon = Coupon.create("ABC123", "Desconto especial", 1.5, FUTURE_DATE, false);

        assertNull(coupon.getId());
        assertEquals("ABC123", coupon.getCode());
        assertEquals("Desconto especial", coupon.getDescription());
        assertEquals(1.5, coupon.getDiscountValue());
        assertEquals(FUTURE_DATE, coupon.getExpirationDate());
        assertEquals(CouponStatus.ACTIVE, coupon.getStatus());
        assertFalse(coupon.isPublished());
        assertFalse(coupon.isRedeemed());
    }

    @Test
    void shouldRemoveSpecialCharactersFromCode() {
        Coupon coupon = Coupon.create("AB#C-123", "desc", 1.0, FUTURE_DATE, false);

        assertEquals("ABC123", coupon.getCode());
        assertEquals(6, coupon.getCode().length());
        assertTrue(coupon.getCode().matches("[A-Z0-9]+"));
    }

    @Test
    void shouldTrimCodeToSixCharacters() {
        Coupon coupon = Coupon.create("ABCDEFGH", "desc", 1.0, FUTURE_DATE, false);

        assertEquals("ABCDEF", coupon.getCode());
        assertEquals(6, coupon.getCode().length());
    }

    @Test
    void shouldUppercaseCode() {
        Coupon coupon = Coupon.create("abc123", "desc", 1.0, FUTURE_DATE, false);

        assertEquals("ABC123", coupon.getCode());
        assertEquals(coupon.getCode(), coupon.getCode().toUpperCase());
    }

    @Test
    void shouldSanitizeAndTrimCodeWithSpecialCharsAndExtraLength() {
        Coupon coupon = Coupon.create("a-b@c#1$2%3^extra", "desc", 1.0, FUTURE_DATE, false);

        assertEquals("ABC123", coupon.getCode());
        assertEquals(6, coupon.getCode().length());
    }

    @Test
    void shouldThrowWhenCodeIsNull() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> Coupon.create(null, "desc", 1.0, FUTURE_DATE, false));

        assertEquals(ErrorMessages.COUPON_CODE_MIN_LENGTH, ex.getMessage());
    }

    @Test
    void shouldThrowWhenCodeHasLessThanSixAlphanumericChars() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> Coupon.create("AB---", "desc", 1.0, FUTURE_DATE, false));

        assertEquals(ErrorMessages.COUPON_CODE_MIN_LENGTH, ex.getMessage());
    }

    @Test
    void shouldThrowWhenCodeIsOnlySpecialChars() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> Coupon.create("!@#$%^", "desc", 1.0, FUTURE_DATE, false));

        assertEquals(ErrorMessages.COUPON_CODE_MIN_LENGTH, ex.getMessage());
    }

    @Test
    void shouldThrowWhenDiscountValueIsBelowMinimum() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> Coupon.create("ABC123", "desc", 0.4, FUTURE_DATE, false));

        assertEquals(ErrorMessages.COUPON_DISCOUNT_MIN_VALUE, ex.getMessage());
    }

    @Test
    void shouldThrowWhenDiscountValueIsZero() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> Coupon.create("ABC123", "desc", 0.0, FUTURE_DATE, false));

        assertEquals(ErrorMessages.COUPON_DISCOUNT_MIN_VALUE, ex.getMessage());
    }

    @Test
    void shouldThrowWhenDiscountValueIsNegative() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> Coupon.create("ABC123", "desc", -1.0, FUTURE_DATE, false));

        assertEquals(ErrorMessages.COUPON_DISCOUNT_MIN_VALUE, ex.getMessage());
    }

    @Test
    void shouldThrowWhenDiscountValueIsNull() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> Coupon.create("ABC123", "desc", null, FUTURE_DATE, false));

        assertEquals(ErrorMessages.COUPON_DISCOUNT_MIN_VALUE, ex.getMessage());
    }

    @Test
    void shouldAcceptMinimumDiscountValue() {
        Coupon coupon = Coupon.create("ABC123", "desc", 0.5, FUTURE_DATE, false);

        assertNotNull(coupon);
        assertEquals(0.5, coupon.getDiscountValue());
    }

    @Test
    void shouldAcceptHighDiscountValue() {
        Coupon coupon = Coupon.create("ABC123", "desc", 99999.99, FUTURE_DATE, false);

        assertEquals(99999.99, coupon.getDiscountValue());
    }

    @Test
    void shouldThrowWhenExpirationDateIsInThePast() {
        OffsetDateTime pastDate = OffsetDateTime.now().minusDays(1);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> Coupon.create("ABC123", "desc", 1.0, pastDate, false));

        assertEquals(ErrorMessages.COUPON_EXPIRATION_DATE_PAST, ex.getMessage());
    }

    @Test
    void shouldThrowWhenExpirationDateIsNull() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> Coupon.create("ABC123", "desc", 1.0, null, false));

        assertEquals(ErrorMessages.COUPON_EXPIRATION_DATE_PAST, ex.getMessage());
    }

    @Test
    void shouldCreateCouponWithActiveStatus() {
        Coupon coupon = Coupon.create("ABC123", "desc", 1.0, FUTURE_DATE, false);

        assertEquals(CouponStatus.ACTIVE, coupon.getStatus());
    }

    @Test
    void shouldCreateCouponWithIdNull() {
        Coupon coupon = Coupon.create("ABC123", "desc", 1.0, FUTURE_DATE, false);

        assertNull(coupon.getId());
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
    void shouldCreateCouponWithPublishedFalseWhenExplicit() {
        Coupon coupon = Coupon.create("ABC123", "desc", 1.0, FUTURE_DATE, false);

        assertFalse(coupon.isPublished());
    }

    @Test
    void shouldCreateCouponWithRedeemedFalse() {
        Coupon coupon = Coupon.create("ABC123", "desc", 1.0, FUTURE_DATE, false);

        assertFalse(coupon.isRedeemed());
    }

    @Test
    void shouldMarkAsDeletedCoupon() {
        Coupon coupon = Coupon.create("ABC123", "desc", 1.0, FUTURE_DATE, false);
        Coupon deleted = coupon.markAsDeleted();

        assertEquals(CouponStatus.DELETED, deleted.getStatus());
        assertEquals(coupon.getCode(), deleted.getCode());
        assertEquals(coupon.getDescription(), deleted.getDescription());
        assertEquals(coupon.getDiscountValue(), deleted.getDiscountValue());
        assertEquals(coupon.getExpirationDate(), deleted.getExpirationDate());
        assertEquals(coupon.isPublished(), deleted.isPublished());
        assertEquals(coupon.isRedeemed(), deleted.isRedeemed());
    }

    @Test
    void shouldNotMutateOriginalCouponWhenDeleting() {
        Coupon coupon = Coupon.create("ABC123", "desc", 1.0, FUTURE_DATE, false);
        Coupon deleted = coupon.markAsDeleted();

        assertEquals(CouponStatus.ACTIVE, coupon.getStatus());
        assertEquals(CouponStatus.DELETED, deleted.getStatus());
        assertNotSame(coupon, deleted);
    }

    @Test
    void shouldThrowWhenDeletingAlreadyDeletedCoupon() {
        Coupon coupon = Coupon.create("ABC123", "desc", 1.0, FUTURE_DATE, false);
        Coupon deleted = coupon.markAsDeleted();

        CouponAlreadyDeletedException ex = assertThrows(
                CouponAlreadyDeletedException.class,
                deleted::markAsDeleted
        );

        assertEquals(ErrorMessages.COUPON_ALREADY_DELETED, ex.getMessage());
    }

    @Test
    void shouldRestoreCouponWithAllFields() {
        UUID id = UUID.randomUUID();
        Coupon restored = Coupon.restore(
                id, "ABC123", "desc", 2.0, FUTURE_DATE,
                CouponStatus.ACTIVE, true, false
        );

        assertEquals(id, restored.getId());
        assertEquals("ABC123", restored.getCode());
        assertEquals("desc", restored.getDescription());
        assertEquals(2.0, restored.getDiscountValue());
        assertEquals(FUTURE_DATE, restored.getExpirationDate());
        assertEquals(CouponStatus.ACTIVE, restored.getStatus());
        assertTrue(restored.isPublished());
        assertFalse(restored.isRedeemed());
    }

    @Test
    void shouldRestoreDeletedCoupon() {
        UUID id = UUID.randomUUID();
        Coupon restored = Coupon.restore(
                id, "DEL123", "deleted coupon", 1.0, FUTURE_DATE,
                CouponStatus.DELETED, false, false
        );

        assertEquals(CouponStatus.DELETED, restored.getStatus());
        assertEquals(id, restored.getId());
    }

    @Test
    void shouldBeEqualWhenAllFieldsMatch() {
        Coupon coupon1 = Coupon.create("ABC123", "desc", 1.0, FUTURE_DATE, false);
        Coupon coupon2 = Coupon.create("ABC123", "desc", 1.0, FUTURE_DATE, false);

        assertEquals(coupon1, coupon2);
        assertEquals(coupon1.hashCode(), coupon2.hashCode());
    }

    @Test
    void shouldNotBeEqualWhenCodesDiffer() {
        Coupon coupon1 = Coupon.create("ABC123", "desc", 1.0, FUTURE_DATE, false);
        Coupon coupon2 = Coupon.create("XYZ789", "desc", 1.0, FUTURE_DATE, false);

        assertNotEquals(coupon1, coupon2);
    }
}
