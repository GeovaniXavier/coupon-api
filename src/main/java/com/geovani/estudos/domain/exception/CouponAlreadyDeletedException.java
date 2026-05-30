package com.geovani.estudos.domain.exception;

public class CouponAlreadyDeletedException extends RuntimeException {

    public CouponAlreadyDeletedException(final String message) {
        super(message);
    }
}

