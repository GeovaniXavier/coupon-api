package com.geovani.estudos.repository;

import com.geovani.estudos.entity.CouponEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CouponRepository extends JpaRepository<CouponEntity, UUID> {
}

