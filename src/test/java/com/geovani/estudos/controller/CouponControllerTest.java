package com.geovani.estudos.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.geovani.estudos.controller.request.CreateCouponRequest;
import com.geovani.estudos.controller.response.CouponResponse;
import com.geovani.estudos.domain.CouponStatus;
import com.geovani.estudos.domain.exception.CouponAlreadyDeletedException;
import com.geovani.estudos.exception.CouponNotFoundException;
import com.geovani.estudos.service.CreateCouponService;
import com.geovani.estudos.service.DeleteCouponService;
import com.geovani.estudos.service.FindCouponByIdService;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CouponController.class)
class CouponControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CreateCouponService createCouponService;

    @MockitoBean
    private FindCouponByIdService findCouponByIdService;

    @MockitoBean
    private DeleteCouponService deleteCouponService;

    private CouponResponse buildResponse() {
        return new CouponResponse(
                UUID.randomUUID(), "ABC123", "Desconto especial",
                1.0, OffsetDateTime.now().plusDays(30).toString(),
                CouponStatus.ACTIVE.name(), false, false
        );
    }

    @Test
    void shouldReturn201WhenCouponIsCreated() throws Exception {
        CreateCouponRequest request = new CreateCouponRequest(
                "ABC123", "Desconto especial", 1.0,
                OffsetDateTime.now().plusDays(30), false
        );
        when(createCouponService.execute(any())).thenReturn(buildResponse());

        mockMvc.perform(post("/coupon")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value("ABC123"))
                .andExpect(jsonPath("$.status").value("ACTIVE"))
                .andExpect(jsonPath("$.redeemed").value(false))
                .andExpect(jsonPath("$.published").value(false));
    }

    @Test
    void shouldReturn201WithSanitizedCodeWhenInputHasSpecialChars() throws Exception {
        CouponResponse sanitizedResponse = new CouponResponse(
                UUID.randomUUID(), "ABC123", "desc",
                1.0, OffsetDateTime.now().plusDays(30).toString(),
                CouponStatus.ACTIVE.name(), false, false
        );
        when(createCouponService.execute(any())).thenReturn(sanitizedResponse);

        CreateCouponRequest request = new CreateCouponRequest(
                "AB#C-123", "desc", 1.0,
                OffsetDateTime.now().plusDays(30), false
        );

        mockMvc.perform(post("/coupon")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value("ABC123"));
    }

    @Test
    void shouldReturn400WhenRequiredFieldsAreMissing() throws Exception {
        mockMvc.perform(post("/coupon")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn400WhenDiscountValueIsBelowMinimumByBeanValidation() throws Exception {
        CreateCouponRequest request = new CreateCouponRequest(
                "ABC123", "desc", 0.4,
                OffsetDateTime.now().plusDays(30), false
        );

        mockMvc.perform(post("/coupon")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(createCouponService, never()).execute(any());
    }

    @Test
    void shouldReturn400WhenExpirationDateIsInThePast() throws Exception {
        when(createCouponService.execute(any()))
                .thenThrow(new IllegalArgumentException("A data de expiração não pode estar no passado."));

        CreateCouponRequest request = new CreateCouponRequest(
                "ABC123", "desc", 1.0,
                OffsetDateTime.now().minusDays(1), false
        );

        mockMvc.perform(post("/coupon")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("A data de expiração não pode estar no passado."));
    }

    @Test
    void shouldReturn400WhenCodeHasNoAlphanumericCharsAfterSanitization() throws Exception {
        when(createCouponService.execute(any()))
                .thenThrow(new IllegalArgumentException("O código do cupom deve conter ao menos 6 caracteres alfanuméricos."));

        CreateCouponRequest request = new CreateCouponRequest(
                "-----", "desc", 1.0,
                OffsetDateTime.now().plusDays(30), false
        );

        mockMvc.perform(post("/coupon")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("O código do cupom deve conter ao menos 6 caracteres alfanuméricos."));
    }

    @Test
    void shouldReturn200WhenCouponIsFound() throws Exception {
        UUID id = UUID.randomUUID();
        when(findCouponByIdService.execute(id)).thenReturn(buildResponse());

        mockMvc.perform(get("/coupon/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("ABC123"))
                .andExpect(jsonPath("$.status").value("ACTIVE"));
    }

    @Test
    void shouldReturn404WhenCouponIsNotFound() throws Exception {
        UUID id = UUID.randomUUID();
        when(findCouponByIdService.execute(id)).thenThrow(new CouponNotFoundException(id));

        mockMvc.perform(get("/coupon/{id}", id))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturn204WhenCouponIsDeleted() throws Exception {
        UUID id = UUID.randomUUID();
        doNothing().when(deleteCouponService).execute(id);

        mockMvc.perform(delete("/coupon/{id}", id))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldReturn409WhenCouponIsAlreadyDeleted() throws Exception {
        UUID id = UUID.randomUUID();

        doThrow(new CouponAlreadyDeletedException("O cupom já foi deletado."))
                .when(deleteCouponService)
                .execute(id);

        mockMvc.perform(delete("/coupon/{id}", id))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message")
                        .value("O cupom já foi deletado."));
    }

    @Test
    void shouldReturn404WhenDeletingNonExistentCoupon() throws Exception {
        UUID id = UUID.randomUUID();
        doThrow(new CouponNotFoundException(id)).when(deleteCouponService).execute(id);

        mockMvc.perform(delete("/coupon/{id}", id))
                .andExpect(status().isNotFound());
    }
}
