package com.geovani.estudos.application.usecase;

import com.geovani.estudos.application.gateway.CouponGateway;
import com.geovani.estudos.domain.Coupon;
import com.geovani.estudos.domain.exception.CouponNotFoundException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class FindCouponByIdUseCase {

    private final CouponGateway couponGateway;

    public FindCouponByIdUseCase(final CouponGateway couponGateway) {
        this.couponGateway = couponGateway;
    }

    public Coupon execute(final UUID id) {
        return couponGateway.findById(id)
                .orElseThrow(() -> new CouponNotFoundException(id));
    }
}
