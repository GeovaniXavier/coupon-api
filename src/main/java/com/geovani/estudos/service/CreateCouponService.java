package com.geovani.estudos.service;

import com.geovani.estudos.controller.request.CreateCouponRequest;
import com.geovani.estudos.controller.response.CouponResponse;
import com.geovani.estudos.domain.CouponDomain;
import com.geovani.estudos.entity.CouponEntity;
import com.geovani.estudos.entity.CouponMapper;
import com.geovani.estudos.repository.CouponRepository;
import org.springframework.stereotype.Service;

@Service
public class CreateCouponService {

    private final CouponRepository couponRepository;

    public CreateCouponService(CouponRepository couponRepository) {
        this.couponRepository = couponRepository;
    }

    public CouponResponse execute(CreateCouponRequest request) {
        CouponDomain domain = CouponDomain.create(
                request.code(),
                request.description(),
                request.discountValue(),
                request.expirationDate(),
                request.published()
        );
        CouponEntity entity = CouponMapper.toEntity(domain);
        CouponEntity saved = couponRepository.save(entity);
        return com.geovani.estudos.controller.response.CouponResponse.fromDomain(CouponMapper.toDomain(saved));
    }
}
