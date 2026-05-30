package com.geovani.estudos.application.usecase;

import com.geovani.estudos.application.gateway.CouponGateway;
import com.geovani.estudos.domain.Coupon;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;

@Service
public class CreateCouponUseCase {

    private final CouponGateway couponGateway;
    private static final Logger log = org.slf4j.LoggerFactory.getLogger(CreateCouponUseCase.class);

    public CreateCouponUseCase(final CouponGateway couponGateway) {
        this.couponGateway = couponGateway;
    }

    public Coupon execute(final CreateCouponCommand command) {
        log.info("Iniciando criação de cupom com code=: {}", command.code());
        Coupon coupon = Coupon.create(
                command.code(),
                command.description(),
                command.discountValue(),
                command.expirationDate(),
                command.published()
        );
        Coupon saved = couponGateway.save(coupon);
        log.info("Cupom criado com sucesso com id=: {}", saved.getId());
        return saved;
    }
}
