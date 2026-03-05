package practical.task.paymentservice.integration;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import practical.task.common.event.PaymentCreatedEvent;
import practical.task.paymentservice.dto.PaymentCreateDto;
import practical.task.paymentservice.dto.PaymentResponseDto;
import practical.task.paymentservice.model.Payment;
import practical.task.paymentservice.repository.PaymentRepository;
import practical.task.paymentservice.service.PaymentService;
import practical.task.paymentservice.service.client.RandomNumberClient;

import java.util.List;

import static com.mongodb.assertions.Assertions.assertNotNull;
import static org.mockito.BDDMockito.*;

@Testcontainers
@SpringBootTest
@ActiveProfiles("test")
class PaymentServiceIntegrationTest {

    @Container
    static MongoDBContainer mongo =
            new MongoDBContainer("mongo:7.0");

    @DynamicPropertySource
    static void mongoProps(DynamicPropertyRegistry registry) {
        registry.add(
                "spring.data.mongodb.uri",
                mongo::getReplicaSetUrl
        );
    }

    @Autowired
    PaymentService paymentService;

    @Autowired
    PaymentRepository paymentRepository;

    @MockitoBean
    KafkaTemplate<String, PaymentCreatedEvent> kafkaTemplate;

    @MockitoBean
    RandomNumberClient randomNumberClient;

    @Test
    void createPayment_integration_success() {
        //given
        given(randomNumberClient.getRandomNumber())
                .willReturn(List.of(2));

        PaymentCreateDto dto = new PaymentCreateDto();
        dto.setUserId("1");
        dto.setOrderId("1");
        dto.setPaymentAmount(50);

        //when
        PaymentResponseDto response = paymentService.create(dto);

        //then
        assertNotNull(response);

        List<Payment> saved =
                paymentRepository.findPaymentsByUserId("1");

        Assertions.assertEquals(1, saved.size());
        Assertions.assertEquals("SUCCESS", saved.getFirst().getStatus());

        then(kafkaTemplate).should()
                .send(anyString(), anyString(), any());
    }

    @Test
    void createPayment_integration_failed_whenRandomIsOdd() {
        //given
        given(randomNumberClient.getRandomNumber())
                .willReturn(List.of(3)); // odd → FAILED

        PaymentCreateDto dto = new PaymentCreateDto();
        dto.setUserId("2");
        dto.setOrderId("2");
        dto.setPaymentAmount(100);

        //when
        PaymentResponseDto response = paymentService.create(dto);

        //then
        assertNotNull(response);

        List<Payment> saved =
                paymentRepository.findPaymentsByUserId("2");

        Assertions.assertEquals(1, saved.size());
        Assertions.assertEquals("FAILED", saved.getFirst().getStatus());

        then(kafkaTemplate).should()
                .send(eq("create-payment"), anyString(), any(PaymentCreatedEvent.class));
    }

    @Test
    void createPayment_kafkaCalledOnce() {
        //given
        given(randomNumberClient.getRandomNumber())
                .willReturn(List.of(2));

        PaymentCreateDto dto = new PaymentCreateDto();
        dto.setUserId("3");
        dto.setOrderId("3");
        dto.setPaymentAmount(30);

        //when
        paymentService.create(dto);

        //then
        then(kafkaTemplate).should(times(1))
                .send(anyString(), anyString(), any());
    }

    @Test
    void getByUserId_returnsPayments() {
        //given
        given(randomNumberClient.getRandomNumber())
                .willReturn(List.of(2));

        PaymentCreateDto dto = new PaymentCreateDto();
        dto.setUserId("user-x");
        dto.setOrderId("order-x");
        dto.setPaymentAmount(10);

        paymentService.create(dto);

        //when
        List<PaymentResponseDto> result =
                paymentService.getByUserId("user-x");

        //then
        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals("order-x", result.getFirst().getOrderId());
    }

    @Test
    void getByStatus_returnsOnlyMatchingPayments() {
        //given
        given(randomNumberClient.getRandomNumber())
                .willReturn(List.of(2));

        PaymentCreateDto dto = new PaymentCreateDto();
        dto.setUserId("5");
        dto.setOrderId("5");
        dto.setPaymentAmount(20);

        paymentService.create(dto);

        //when
        List<PaymentResponseDto> successPayments =
                paymentService.getByStatus("SUCCESS");

        //then
        Assertions.assertFalse(successPayments.isEmpty());
    }
}
