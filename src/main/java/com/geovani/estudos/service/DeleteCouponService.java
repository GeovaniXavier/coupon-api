package com.geovani.estudos.service;

import com.geovani.estudos.domain.CouponDomain;
import com.geovani.estudos.entity.CouponEntity;
import com.geovani.estudos.entity.CouponMapper;
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
        CouponEntity entity = couponRepository.findById(id)
                .orElseThrow(() -> new CouponNotFoundException(id));
        CouponDomain domain = CouponMapper.toDomain(entity).markAsDeleted();
        CouponEntity deleted = CouponMapper.toEntity(domain);
        couponRepository.save(deleted);
    }
}
