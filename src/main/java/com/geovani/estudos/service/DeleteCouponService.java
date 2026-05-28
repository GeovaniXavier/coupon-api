package com.geovani.estudos.service;

import com.geovani.estudos.domain.Coupon;
import com.geovani.estudos.exception.CouponNotFoundException;
import com.geovani.estudos.repository.CouponRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class DeleteCouponService {

    private final CouponRepository couponRepository;

    public DeleteCouponService(CouponRepository couponRepository) {
        this.couponRepository = couponRepository;
    }

    public void execute(UUID id) {
        Coupon coupon = couponRepository.findById(id)
                .orElseThrow(() -> new CouponNotFoundException(id));
        coupon.softDelete();
        couponRepository.save(coupon);
    }
}

