package practical.task.paymentservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PaymentCreateDto {
    @NotBlank(message = "order id cannot be null")
    String orderId;
    @NotBlank(message = "user id cannot be null")
    String userId;
    @NotNull(message = "paymentAmount cannot be null")
    Integer paymentAmount;
}
