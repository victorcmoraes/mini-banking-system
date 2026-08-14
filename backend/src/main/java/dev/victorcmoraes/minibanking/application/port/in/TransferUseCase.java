package dev.victorcmoraes.minibanking.application.port.in;

public interface TransferUseCase {
    TransferReceipt transfer(TransferCommand command);
}
