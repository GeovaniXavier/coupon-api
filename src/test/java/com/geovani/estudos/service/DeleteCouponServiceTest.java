package com.geovani.estudos.service;

import com.geovani.estudos.domain.CouponDomain;
import com.geovani.estudos.domain.CouponStatus;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeleteCouponServiceTest {

    @Mock
    private CouponRepository couponRepository;

    @InjectMocks
    private DeleteCouponService deleteCouponService;

    @Test
    void shouldSoftDeleteCouponSuccessfully() {
        CouponDomain couponDomain = CouponDomain.create("ABC123", "desc", 1.0, OffsetDateTime.now().plusDays(30), false);
        CouponEntity couponEntity = CouponMapper.toEntity(couponDomain);
        UUID id = couponEntity.getId();
        when(couponRepository.findById(id)).thenReturn(Optional.of(couponEntity));
        when(couponRepository.save(any(CouponEntity.class))).thenReturn(couponEntity);

        deleteCouponService.execute(id);

        CouponDomain deleted = CouponMapper.toDomain(couponEntity).markAsDeleted();
        assertEquals(CouponStatus.DELETED, deleted.getStatus());
        verify(couponRepository, times(1)).save(any(CouponEntity.class));
    }

    @Test
    void shouldThrowWhenCouponNotFound() {
        UUID id = UUID.randomUUID();
        when(couponRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(CouponNotFoundException.class, () -> deleteCouponService.execute(id));
        verify(couponRepository, never()).save(any());
    }

    @Test
    void shouldThrowWhenCouponIsAlreadyDeleted() {
        CouponDomain couponDomain = CouponDomain.create("ABC123", "desc", 1.0, OffsetDateTime.now().plusDays(30), false);
        CouponDomain deletedDomain = couponDomain.markAsDeleted();
        CouponEntity deletedEntity = CouponMapper.toEntity(deletedDomain);
        UUID id = deletedEntity.getId();
        when(couponRepository.findById(id)).thenReturn(Optional.of(deletedEntity));

        assertThrows(IllegalStateException.class, () -> deleteCouponService.execute(id));
        verify(couponRepository, never()).save(any());
    }
}
