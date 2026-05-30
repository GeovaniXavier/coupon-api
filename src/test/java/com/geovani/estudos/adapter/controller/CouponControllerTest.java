package com.geovani.estudos.adapter.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.geovani.estudos.adapter.controller.request.CreateCouponRequest;
import com.geovani.estudos.application.usecase.CreateCouponUseCase;
import com.geovani.estudos.application.usecase.DeleteCouponUseCase;
import com.geovani.estudos.application.usecase.FindCouponByIdUseCase;
import com.geovani.estudos.domain.Coupon;
import com.geovani.estudos.domain.CouponStatus;
import com.geovani.estudos.domain.ErrorMessages;
import com.geovani.estudos.domain.exception.CouponAlreadyDeletedException;
import com.geovani.estudos.domain.exception.CouponNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.OffsetDateTime;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CouponController.class)
class CouponControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CreateCouponUseCase createCouponUseCase;

    @MockitoBean
    private FindCouponByIdUseCase findCouponByIdUseCase;

    @MockitoBean
    private DeleteCouponUseCase deleteCouponUseCase;

    private static final UUID COUPON_ID = UUID.randomUUID();
    private static final String COUPON_CODE = "ABC123";
    private static final String COUPON_DESCRIPTION = "Desconto especial";
    private static final double COUPON_DISCOUNT = 1.0;
    private static final OffsetDateTime FUTURE_DATE = OffsetDateTime.now().plusDays(30);

    private Coupon buildActiveCoupon() {
        return Coupon.restore(COUPON_ID, COUPON_CODE, COUPON_DESCRIPTION, COUPON_DISCOUNT,
                FUTURE_DATE, CouponStatus.ACTIVE, false, false);
    }

    private Coupon buildPublishedCoupon() {
        return Coupon.restore(COUPON_ID, COUPON_CODE, COUPON_DESCRIPTION, COUPON_DISCOUNT,
                FUTURE_DATE, CouponStatus.ACTIVE, true, false);
    }

    @Test
    void shouldReturn201WhenCouponIsCreated() throws Exception {
        CreateCouponRequest request = new CreateCouponRequest(
                COUPON_CODE, COUPON_DESCRIPTION, COUPON_DISCOUNT, FUTURE_DATE, false
        );
        when(createCouponUseCase.execute(any())).thenReturn(buildActiveCoupon());

        mockMvc.perform(post("/coupon")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(COUPON_ID.toString()))
                .andExpect(jsonPath("$.code").value(COUPON_CODE))
                .andExpect(jsonPath("$.description").value(COUPON_DESCRIPTION))
                .andExpect(jsonPath("$.discountValue").value(COUPON_DISCOUNT))
                .andExpect(jsonPath("$.status").value("ACTIVE"))
                .andExpect(jsonPath("$.published").value(false))
                .andExpect(jsonPath("$.redeemed").value(false));

        verify(createCouponUseCase, times(1)).execute(any());
    }

    @Test
    void shouldReturn201WithPublishedTrueWhenRequested() throws Exception {
        CreateCouponRequest request = new CreateCouponRequest(
                COUPON_CODE, COUPON_DESCRIPTION, COUPON_DISCOUNT, FUTURE_DATE, true
        );
        when(createCouponUseCase.execute(any())).thenReturn(buildPublishedCoupon());

        mockMvc.perform(post("/coupon")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.published").value(true))
                .andExpect(jsonPath("$.status").value("ACTIVE"));
    }

    @Test
    void shouldReturn201WithSanitizedCodeWhenInputHasSpecialChars() throws Exception {
        when(createCouponUseCase.execute(any())).thenReturn(buildActiveCoupon());

        CreateCouponRequest request = new CreateCouponRequest(
                "AB#C-123", "desc", COUPON_DISCOUNT, FUTURE_DATE, false
        );

        mockMvc.perform(post("/coupon")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value(COUPON_CODE))
                .andExpect(jsonPath("$.code").isString());
    }

    @Test
    void shouldReturn400WhenRequiredFieldsAreMissing() throws Exception {
        mockMvc.perform(post("/coupon")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").isNotEmpty());

        verify(createCouponUseCase, never()).execute(any());
    }

    @Test
    void shouldReturn400WhenCodeIsBlank() throws Exception {
        CreateCouponRequest request = new CreateCouponRequest(
                "", COUPON_DESCRIPTION, COUPON_DISCOUNT, FUTURE_DATE, false
        );

        mockMvc.perform(post("/coupon")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").isNotEmpty());

        verify(createCouponUseCase, never()).execute(any());
    }

    @Test
    void shouldReturn400WhenDescriptionIsBlank() throws Exception {
        CreateCouponRequest request = new CreateCouponRequest(
                COUPON_CODE, "", COUPON_DISCOUNT, FUTURE_DATE, false
        );

        mockMvc.perform(post("/coupon")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").isNotEmpty());

        verify(createCouponUseCase, never()).execute(any());
    }

    @Test
    void shouldReturn400WhenDiscountValueIsBelowMinimumByBeanValidation() throws Exception {
        CreateCouponRequest request = new CreateCouponRequest(
                COUPON_CODE, COUPON_DESCRIPTION, 0.4, FUTURE_DATE, false
        );

        mockMvc.perform(post("/coupon")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").isNotEmpty());

        verify(createCouponUseCase, never()).execute(any());
    }

    @Test
    void shouldReturn400WhenExpirationDateIsInThePast() throws Exception {
        when(createCouponUseCase.execute(any()))
                .thenThrow(new IllegalArgumentException(ErrorMessages.COUPON_EXPIRATION_DATE_PAST));

        CreateCouponRequest request = new CreateCouponRequest(
                COUPON_CODE, COUPON_DESCRIPTION, COUPON_DISCOUNT,
                OffsetDateTime.now().minusDays(1), false
        );

        mockMvc.perform(post("/coupon")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(ErrorMessages.COUPON_EXPIRATION_DATE_PAST));
    }

    @Test
    void shouldReturn400WhenCodeHasNoAlphanumericCharsAfterSanitization() throws Exception {
        when(createCouponUseCase.execute(any()))
                .thenThrow(new IllegalArgumentException(ErrorMessages.COUPON_CODE_MIN_LENGTH));

        CreateCouponRequest request = new CreateCouponRequest(
                "-----", COUPON_DESCRIPTION, COUPON_DISCOUNT, FUTURE_DATE, false
        );

        mockMvc.perform(post("/coupon")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(ErrorMessages.COUPON_CODE_MIN_LENGTH));
    }

    @Test
    void shouldReturn200WhenCouponIsFound() throws Exception {
        UUID id = UUID.randomUUID();
        Coupon coupon = Coupon.restore(id, COUPON_CODE, COUPON_DESCRIPTION, COUPON_DISCOUNT,
                FUTURE_DATE, CouponStatus.ACTIVE, false, false);
        when(findCouponByIdUseCase.execute(id)).thenReturn(coupon);

        mockMvc.perform(get("/coupon/{id}", id))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.code").value(COUPON_CODE))
                .andExpect(jsonPath("$.description").value(COUPON_DESCRIPTION))
                .andExpect(jsonPath("$.discountValue").value(COUPON_DISCOUNT))
                .andExpect(jsonPath("$.status").value("ACTIVE"))
                .andExpect(jsonPath("$.published").value(false))
                .andExpect(jsonPath("$.redeemed").value(false));

        verify(findCouponByIdUseCase, times(1)).execute(id);
    }

    @Test
    void shouldReturn404WhenCouponIsNotFound() throws Exception {
        UUID id = UUID.randomUUID();
        when(findCouponByIdUseCase.execute(id)).thenThrow(new CouponNotFoundException(id));

        mockMvc.perform(get("/coupon/{id}", id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").isNotEmpty());

        verify(findCouponByIdUseCase, times(1)).execute(id);
    }

    @Test
    void shouldReturn204WhenCouponIsDeleted() throws Exception {
        UUID id = UUID.randomUUID();
        doNothing().when(deleteCouponUseCase).execute(id);

        mockMvc.perform(delete("/coupon/{id}", id))
                .andExpect(status().isNoContent());

        verify(deleteCouponUseCase, times(1)).execute(id);
    }

    @Test
    void shouldReturn409WhenCouponIsAlreadyDeleted() throws Exception {
        UUID id = UUID.randomUUID();

        doThrow(new CouponAlreadyDeletedException(ErrorMessages.COUPON_ALREADY_DELETED))
                .when(deleteCouponUseCase)
                .execute(id);

        mockMvc.perform(delete("/coupon/{id}", id))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value(ErrorMessages.COUPON_ALREADY_DELETED));
    }

    @Test
    void shouldReturn404WhenDeletingNonExistentCoupon() throws Exception {
        UUID id = UUID.randomUUID();
        doThrow(new CouponNotFoundException(id)).when(deleteCouponUseCase).execute(id);

        mockMvc.perform(delete("/coupon/{id}", id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").isNotEmpty());
    }
}
