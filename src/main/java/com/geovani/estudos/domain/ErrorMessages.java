package com.geovani.estudos.domain;

public final class ErrorMessages {

    public static final String COUPON_ALREADY_EXISTS =
            "Já existe um cupom com este código.";

    public static final String COUPON_NOT_FOUND =
            "Cupom não encontrado com o id: %s";

    public static final String COUPON_ALREADY_DELETED =
            "O cupom já foi deletado.";

    public static final String COUPON_CODE_MIN_LENGTH =
            "O código do cupom deve conter ao menos 6 caracteres alfanuméricos.";

    public static final String COUPON_DISCOUNT_MIN_VALUE =
            "O valor de desconto mínimo é 0.5.";

    public static final String COUPON_EXPIRATION_DATE_PAST =
            "A data de expiração não pode estar no passado.";

    private ErrorMessages() {
    }
}
