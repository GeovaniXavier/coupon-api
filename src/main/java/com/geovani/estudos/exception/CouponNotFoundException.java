package com.geovani.estudos.exception;

import java.util.UUID;

public class CouponNotFoundException extends RuntimeException {

    public CouponNotFoundException(UUID id) {
        super("Cupom não encontrado com o id: " + id);
    }
}

