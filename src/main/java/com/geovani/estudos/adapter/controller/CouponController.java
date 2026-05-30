package com.geovani.estudos.adapter.controller;

import com.geovani.estudos.adapter.controller.request.CreateCouponRequest;
import com.geovani.estudos.adapter.controller.response.CouponResponse;
import com.geovani.estudos.application.usecase.CreateCouponUseCase;
import com.geovani.estudos.application.usecase.DeleteCouponUseCase;
import com.geovani.estudos.application.usecase.FindCouponByIdUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/coupon")
@Tag(name = "Coupon", description = "Gerenciamento de cupons de desconto")
public class CouponController {

    private final CreateCouponUseCase createCouponUseCase;
    private final FindCouponByIdUseCase findCouponByIdUseCase;
    private final DeleteCouponUseCase deleteCouponUseCase;
    private static final Logger log = org.slf4j.LoggerFactory.getLogger(CouponController.class);

    public CouponController(final CreateCouponUseCase createCouponUseCase,
                            final FindCouponByIdUseCase findCouponByIdUseCase,
                            final DeleteCouponUseCase deleteCouponUseCase) {
        this.createCouponUseCase = createCouponUseCase;
        this.findCouponByIdUseCase = findCouponByIdUseCase;
        this.deleteCouponUseCase = deleteCouponUseCase;
    }

    @Operation(summary = "Criar cupom",
            description = "Cria um novo cupom de desconto. Caracteres especiais no código são removidos automaticamente.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Cupom criado com sucesso",
                    content = @Content(schema = @Schema(implementation = CouponResponse.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos", content = @Content)
    })
    @PostMapping
    public ResponseEntity<CouponResponse> create(@Valid @RequestBody final CreateCouponRequest request) {
        log.info("Recebendo requisição para criar cupom com code=: {}", request.code());
        CouponResponse response = CouponResponse.fromDomain(
                createCouponUseCase.execute(request.toCommand())
        );
        log.info("Cupom criado com sucesso com id=: {}", response.id());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Buscar cupom por ID",
            description = "Retorna os dados de um cupom pelo seu identificador único.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cupom encontrado",
                    content = @Content(schema = @Schema(implementation = CouponResponse.class))),
            @ApiResponse(responseCode = "404", description = "Cupom não encontrado", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<CouponResponse> findById(
            @Parameter(description = "ID UUID do cupom") @PathVariable final UUID id) {
        return ResponseEntity.ok(CouponResponse.fromDomain(findCouponByIdUseCase.execute(id)));
    }

    @Operation(summary = "Deletar cupom",
            description = "Realiza o soft delete do cupom, preservando os dados no banco.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Cupom deletado com sucesso", content = @Content),
            @ApiResponse(responseCode = "404", description = "Cupom não encontrado", content = @Content),
            @ApiResponse(responseCode = "409", description = "Cupom já foi deletado anteriormente", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @Parameter(description = "ID UUID do cupom") @PathVariable final UUID id) {
        log.info("Recebendo requisição para deletar cupom com id=: {}", id);
        deleteCouponUseCase.execute(id);
        return ResponseEntity.noContent().build();
    }
}
