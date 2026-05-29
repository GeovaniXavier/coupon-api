package com.geovani.estudos.service;

import com.geovani.estudos.controller.response.CouponResponse;
import com.geovani.estudos.entity.CouponEntity;
import com.geovani.estudos.entity.CouponMapper;
import com.geovani.estudos.exception.CouponNotFoundException;
import com.geovani.estudos.repository.CouponRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class FindCouponByIdService {

    private final CouponRepository couponRepository;

    public FindCouponByIdService(CouponRepository couponRepository) {
        this.couponRepository = couponRepository;
    }

    public CouponResponse execute(UUID id) {
        CouponEntity entity = couponRepository.findById(id)
                .orElseThrow(() -> new CouponNotFoundException(id));
        return CouponResponse.fromDomain(CouponMapper.toDomain(entity));
    }
}
