package com.geovani.estudos.service;

import com.geovani.estudos.controller.request.CreateCouponRequest;
import com.geovani.estudos.controller.response.CouponResponse;
import com.geovani.estudos.domain.Coupon;
import com.geovani.estudos.repository.CouponRepository;
import org.springframework.stereotype.Service;

@Service
public class CreateCouponService {

    private final CouponRepository couponRepository;

    public CreateCouponService(CouponRepository couponRepository) {
        this.couponRepository = couponRepository;
    }

    public CouponResponse execute(CreateCouponRequest request) {
        Coupon coupon = Coupon.create(
                request.code(),
                request.description(),
                request.discountValue(),
                request.expirationDate(),
                request.published()
        );
        return CouponResponse.from(couponRepository.save(coupon));
    }
}

