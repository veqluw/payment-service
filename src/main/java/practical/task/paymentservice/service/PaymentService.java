package practical.task.paymentservice.service;

import practical.task.paymentservice.dto.PaymentCreateDto;
import practical.task.paymentservice.dto.PaymentResponseDto;

import java.util.List;

public interface PaymentService {
    PaymentResponseDto create(PaymentCreateDto paymentCreateDto);

    List<PaymentResponseDto> getByUserId(String id);

    List<PaymentResponseDto> getByOrderId(String id);

    List<PaymentResponseDto> getByStatus(String status);
}
