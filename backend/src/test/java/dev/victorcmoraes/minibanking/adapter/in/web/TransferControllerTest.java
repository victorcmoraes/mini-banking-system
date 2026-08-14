package dev.victorcmoraes.minibanking.adapter.in.web;

import dev.victorcmoraes.minibanking.application.port.in.TransferReceipt;
import dev.victorcmoraes.minibanking.application.port.in.TransferUseCase;
import dev.victorcmoraes.minibanking.domain.TransactionStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.Instant;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TransferController.class)
class TransferControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TransferUseCase transferUseCase;

    @Test
    void createsTransferWhenPayloadAndIdempotencyHeaderAreValid() throws Exception {
        when(transferUseCase.transfer(any())).thenReturn(new TransferReceipt(
                99L,
                1001L,
                1002L,
                new BigDecimal("25.00"),
                TransactionStatus.COMPLETED,
                Instant.parse("2026-05-22T12:00:00Z"),
                false
        ));

        mockMvc.perform(post("/api/v1/transfers")
                        .header("Idempotency-Key", "idem-controller")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "sourceAccountId": 1001,
                                  "targetAccountId": 1002,
                                  "amount": 25.00
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.transactionId").value(99))
                .andExpect(jsonPath("$.status").value("COMPLETED"))
                .andExpect(jsonPath("$.replayed").value(false));
    }

    @Test
    void rejectsInvalidPayloadBeforeCallingUseCase() throws Exception {
        mockMvc.perform(post("/api/v1/transfers")
                        .header("Idempotency-Key", "idem-controller")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "sourceAccountId": 1001,
                                  "targetAccountId": 1002,
                                  "amount": 0
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("VALIDATION"));
    }
}
