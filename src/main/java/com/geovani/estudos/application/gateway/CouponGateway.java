package com.geovani.estudos.application.gateway;

import com.geovani.estudos.domain.Coupon;

import java.util.Optional;
import java.util.UUID;

/**
 * Port da camada de aplicação para persistência de Coupon.
 * A camada de infraestrutura é quem implementa esta interface
 * (Dependency Inversion - regra principal de Clean Architecture).
 */
public interface CouponGateway {

    Coupon save(Coupon coupon);

    Optional<Coupon> findById(UUID id);
}
