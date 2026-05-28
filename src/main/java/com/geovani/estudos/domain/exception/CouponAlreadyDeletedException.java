package com.geovani.estudos.domain.exception;

public class CouponAlreadyDeletedException extends RuntimeException {

    public CouponAlreadyDeletedException() {
        super("O cupom já foi deletado.");
    }
}

