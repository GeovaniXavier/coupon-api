package com.geovani.estudos.controller;

import com.geovani.estudos.controller.request.CreateCouponRequest;
import com.geovani.estudos.controller.response.CouponResponse;
import com.geovani.estudos.service.CreateCouponService;
import com.geovani.estudos.service.DeleteCouponService;
import com.geovani.estudos.service.FindCouponByIdService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/coupon")
@Tag(name = "Coupon", description = "Gerenciamento de cupons de desconto")
public class CouponController {

    private final CreateCouponService createCouponService;
    private final FindCouponByIdService findCouponByIdService;
    private final DeleteCouponService deleteCouponService;

    public CouponController(CreateCouponService createCouponService,
                            FindCouponByIdService findCouponByIdService,
                            DeleteCouponService deleteCouponService) {
        this.createCouponService = createCouponService;
        this.findCouponByIdService = findCouponByIdService;
        this.deleteCouponService = deleteCouponService;
    }

    @Operation(summary = "Criar cupom", description = "Cria um novo cupom de desconto. Caracteres especiais no código são removidos automaticamente.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Cupom criado com sucesso",
                    content = @Content(schema = @Schema(implementation = CouponResponse.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos", content = @Content)
    })
    @PostMapping
    public ResponseEntity<CouponResponse> create(@Valid @RequestBody CreateCouponRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(createCouponService.execute(request));
    }

    @Operation(summary = "Buscar cupom por ID", description = "Retorna os dados de um cupom pelo seu identificador único.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cupom encontrado",
                    content = @Content(schema = @Schema(implementation = CouponResponse.class))),
            @ApiResponse(responseCode = "404", description = "Cupom não encontrado", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<CouponResponse> findById(
            @Parameter(description = "ID UUID do cupom") @PathVariable UUID id) {
        return ResponseEntity.ok(findCouponByIdService.execute(id));
    }

    @Operation(summary = "Deletar cupom", description = "Realiza o soft delete do cupom, preservando os dados no banco.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Cupom deletado com sucesso", content = @Content),
            @ApiResponse(responseCode = "404", description = "Cupom não encontrado", content = @Content),
            @ApiResponse(responseCode = "409", description = "Cupom já foi deletado anteriormente", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @Parameter(description = "ID UUID do cupom") @PathVariable UUID id) {
        deleteCouponService.execute(id);
        return ResponseEntity.noContent().build();
    }
}
