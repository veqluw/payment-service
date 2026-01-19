package practical.task.paymentservice.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "payments")
@Data
public class Payment {

    @Id
    private String id;

    private Integer orderId;
    private Integer userId;
    private String status;
    private Integer paymentAmount;
    private Instant createdAt;
    private Instant updatedAt;

}
