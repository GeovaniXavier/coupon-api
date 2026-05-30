package com.geovani.estudos.infrastructure.persistence;

import com.geovani.estudos.application.gateway.CouponGateway;
import com.geovani.estudos.domain.Coupon;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

/**
 * Implementação JPA do port CouponGateway.
 * Vive na camada de infraestrutura e converte entre domínio e entidade JPA.
 */
@Component
public class CouponRepositoryGateway implements CouponGateway {

    private final CouponJpaRepository jpaRepository;
    private static final Logger log = org.slf4j.LoggerFactory.getLogger(CouponRepositoryGateway.class);

    public CouponRepositoryGateway(final CouponJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Coupon save(final Coupon coupon) {
        log.info("Persistindo cupom no banco, code=: {}", coupon.getCode());
        CouponEntity entity = CouponEntityMapper.toEntity(coupon);
        CouponEntity saved = jpaRepository.save(entity);
        return CouponEntityMapper.toDomain(saved);
    }

    @Override
    public Optional<Coupon> findById(final UUID id) {
        log.info("Buscando cupom pelo ID, id=: {}", id);
        return jpaRepository.findById(id).map(CouponEntityMapper::toDomain);
    }
}
