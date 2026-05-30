package com.geovani.estudos.application.usecase;

import com.geovani.estudos.application.gateway.CouponGateway;
import com.geovani.estudos.domain.Coupon;
import org.springframework.stereotype.Service;

@Service
public class CreateCouponUseCase {

    private final CouponGateway couponGateway;

    public CreateCouponUseCase(final CouponGateway couponGateway) {
        this.couponGateway = couponGateway;
    }

    public Coupon execute(final CreateCouponCommand command) {
        Coupon coupon = Coupon.create(
                command.code(),
                command.description(),
                command.discountValue(),
                command.expirationDate(),
                command.published()
        );
        return couponGateway.save(coupon);
    }
}
