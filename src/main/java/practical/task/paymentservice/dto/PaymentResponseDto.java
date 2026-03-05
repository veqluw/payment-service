package practical.task.paymentservice.dto;

import lombok.Data;

@Data
public class PaymentResponseDto {
    String orderId;
    String userId;
    Integer paymentAmount;
}
