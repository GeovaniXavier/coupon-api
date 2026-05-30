package com.geovani.estudos.application.usecase;

import com.geovani.estudos.application.gateway.CouponGateway;
import com.geovani.estudos.domain.Coupon;
import com.geovani.estudos.domain.exception.CouponNotFoundException;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class FindCouponByIdUseCase {

    private final CouponGateway couponGateway;
    private static final Logger log = org.slf4j.LoggerFactory.getLogger(FindCouponByIdUseCase.class);

    public FindCouponByIdUseCase(final CouponGateway couponGateway) {
        this.couponGateway = couponGateway;
    }

    public Coupon execute(final UUID id) {
        log.info("Iniciando busca por cupom com id=: {}", id);
        return couponGateway.findById(id)
                .orElseThrow(() -> new CouponNotFoundException(id));
    }
}