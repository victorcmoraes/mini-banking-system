package dev.victorcmoraes.minibanking.adapter.in.web;

import dev.victorcmoraes.minibanking.application.port.in.TransferCommand;
import dev.victorcmoraes.minibanking.application.port.in.TransferReceipt;
import dev.victorcmoraes.minibanking.application.port.in.TransferUseCase;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.Instant;

@RestController
public class TransferController {
    private final TransferUseCase transferUseCase;

    public TransferController(TransferUseCase transferUseCase) {
        this.transferUseCase = transferUseCase;
    }

    @PostMapping("/api/v1/transfers")
    public ResponseEntity<TransferResponse> transfer(
            @RequestHeader("Idempotency-Key") String idempotencyKey,
            @Valid @RequestBody TransferRequest request
    ) {
        TransferReceipt receipt = transferUseCase.transfer(new TransferCommand(
                request.sourceAccountId(),
                request.targetAccountId(),
                request.amount(),
                idempotencyKey
        ));

        HttpStatus status = receipt.replayed() ? HttpStatus.OK : HttpStatus.CREATED;
        return ResponseEntity.status(status).body(TransferResponse.from(receipt));
    }

    public record TransferRequest(
            @NotNull(message = "sourceAccountId e obrigatorio")
            Long sourceAccountId,
            @NotNull(message = "targetAccountId e obrigatorio")
            Long targetAccountId,
            @NotNull(message = "amount e obrigatorio")
            @DecimalMin(value = "0.01", message = "amount deve ser maior que zero")
            BigDecimal amount
    ) {
    }

    public record TransferResponse(
            Long transactionId,
            Long sourceAccountId,
            Long targetAccountId,
            BigDecimal amount,
            String status,
            Instant createdAt,
            boolean replayed
    ) {
        static TransferResponse from(TransferReceipt receipt) {
            return new TransferResponse(
                    receipt.transactionId(),
                    receipt.sourceAccountId(),
                    receipt.targetAccountId(),
                    receipt.amount(),
                    receipt.status().name(),
                    receipt.createdAt(),
                    receipt.replayed()
            );
        }
    }
}
