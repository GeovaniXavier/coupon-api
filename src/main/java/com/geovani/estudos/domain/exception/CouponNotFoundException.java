package com.geovani.estudos.domain.exception;

import com.geovani.estudos.domain.ErrorMessages;

import java.util.UUID;

public class CouponNotFoundException extends RuntimeException {

    public CouponNotFoundException(final UUID id) {
        super(String.format(ErrorMessages.COUPON_NOT_FOUND, id));
    }
}
