package com.geovani.estudos.service;

import com.geovani.estudos.controller.response.CouponResponse;
import com.geovani.estudos.domain.CouponDomain;
import com.geovani.estudos.exception.CouponNotFoundException;
import com.geovani.estudos.repository.CouponRepository;
import com.geovani.estudos.entity.CouponEntity;
import com.geovani.estudos.entity.CouponMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FindCouponByIdServiceTest {

    @Mock
    private CouponRepository couponRepository;

    @InjectMocks
    private FindCouponByIdService findCouponByIdService;

    @Test
    void shouldReturnCouponWhenFound() {
        CouponDomain couponDomain = CouponDomain.create("ABC123", "desc", 1.0, OffsetDateTime.now().plusDays(30), false);
        CouponEntity couponEntity = CouponMapper.toEntity(couponDomain);
        UUID id = couponEntity.getId();
        when(couponRepository.findById(id)).thenReturn(Optional.of(couponEntity));

        CouponResponse response = findCouponByIdService.execute(id);

        assertNotNull(response);
        assertEquals("ABC123", response.code());
    }

    @Test
    void shouldThrowCouponNotFoundExceptionWhenCouponDoesNotExist() {
        UUID id = UUID.randomUUID();
        when(couponRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(CouponNotFoundException.class, () -> findCouponByIdService.execute(id));
    }
}
