package practical.task.paymentservice.repository;

import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import practical.task.paymentservice.model.Payment;

import java.time.Instant;
import java.util.List;

@Repository
public interface PaymentRepository extends MongoRepository<Payment, String> {

    @Query("{'user_id' : ?0}")
    Payment findPaymentByUserId(String userId);

    @Aggregation(pipeline = {
            "{ $match: { userId: ?0, createdAt: { $gte: ?1, $lte: ?2 } } }",
            "{ $group: { _id: null, total: { $sum: '$amount' } } }"
    })
    Long getTotalSumByUserIdAndDateRange(String userId, Instant start, Instant end);

    @Aggregation(pipeline = {
            "{ $match: { createdAt: { $gte: ?0, $lte: ?1 } } }",
            "{ $group: { _id: null, total: { $sum: '$amount' } } }"
    })
    Long getTotalSumForAllUsersInDateRange(Instant start, Instant end);

    List<Payment> findPaymentsByUserId(String userId);

    List<Payment> findPaymentsByOrderId(String orderId);

    List<Payment> findPaymentsByStatus(String status);
}
