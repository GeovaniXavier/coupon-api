package com.geovani.estudos.application.usecase;

import com.geovani.estudos.application.gateway.CouponGateway;
import com.geovani.estudos.domain.Coupon;
import com.geovani.estudos.domain.exception.CouponNotFoundException;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class DeleteCouponUseCase {

    private final CouponGateway couponGateway;
    private static final Logger log = org.slf4j.LoggerFactory.getLogger(DeleteCouponUseCase.class);

    public DeleteCouponUseCase(final CouponGateway couponGateway) {
        this.couponGateway = couponGateway;
    }

    public void execute(final UUID id) {
        Coupon coupon = couponGateway.findById(id)
                .orElseThrow(() -> new CouponNotFoundException(id));
        log.info("Iniciando exclusão de cupom com id=: {}", id);
        couponGateway.save(coupon.markAsDeleted());
        log.info("Cupom excluído com sucesso, id=: {}", id);
    }
}