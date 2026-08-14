package dev.victorcmoraes.minibanking.application.port.in;

import java.math.BigDecimal;

public record AccountView(Long id, String ownerName, BigDecimal balance) {
}
