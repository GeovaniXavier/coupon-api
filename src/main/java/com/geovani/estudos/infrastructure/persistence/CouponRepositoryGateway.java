package com.geovani.estudos.infrastructure.persistence;

import com.geovani.estudos.application.gateway.CouponGateway;
import com.geovani.estudos.domain.Coupon;
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

    public CouponRepositoryGateway(final CouponJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Coupon save(final Coupon coupon) {
        CouponEntity entity = CouponEntityMapper.toEntity(coupon);
        CouponEntity saved = jpaRepository.save(entity);
        return CouponEntityMapper.toDomain(saved);
    }

    @Override
    public Optional<Coupon> findById(final UUID id) {
        return jpaRepository.findById(id).map(CouponEntityMapper::toDomain);
    }
}
