package practical.task.paymentservice.unit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.kafka.core.KafkaTemplate;
import practical.task.common.event.PaymentCreatedEvent;
import practical.task.paymentservice.dto.PaymentCreateDto;
import practical.task.paymentservice.dto.PaymentResponseDto;
import practical.task.paymentservice.mapper.PaymentMapper;
import practical.task.paymentservice.model.Payment;
import practical.task.paymentservice.repository.PaymentRepository;
import practical.task.paymentservice.service.client.RandomNumberClient;
import practical.task.paymentservice.service.impl.PaymentServiceImpl;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
public class PaymentServiceTest {

    @Mock
    PaymentMapper paymentMapper;

    @Mock
    RandomNumberClient randomNumberClient;

    @Mock
    PaymentRepository paymentRepository;

    @Mock
    KafkaTemplate<String, PaymentCreatedEvent> kafkaTemplate;

    @InjectMocks
    PaymentServiceImpl paymentService;

    Payment payment;
    PaymentCreatedEvent event;
    PaymentCreateDto paymentCreateDto;
    PaymentResponseDto paymentResponseDto;

    @BeforeEach
    void setUp() {
        payment = new Payment();
        payment.setId("1");
        payment.setUserId("1");
        payment.setOrderId("1");

        paymentCreateDto = new PaymentCreateDto();
        paymentCreateDto.setOrderId("1");
        paymentCreateDto.setUserId("1");
        paymentCreateDto.setPaymentAmount(20);

        paymentResponseDto = new PaymentResponseDto();
        paymentResponseDto.setUserId("1");
        paymentResponseDto.setOrderId("1");
        paymentResponseDto.setPaymentAmount(20);
    }

    @Test
    void createPayment_success() {
        //given
        when(paymentMapper.toEntity(paymentCreateDto)).thenReturn(payment);
        when(randomNumberClient.getRandomNumber()).thenReturn(List.of(2));
        when(paymentRepository.save(payment)).thenReturn(payment);
        when(paymentMapper.toDto(payment)).thenReturn(paymentResponseDto);

        //when
        PaymentResponseDto response = paymentService.create(paymentCreateDto);

        //then
        assertNotNull(response);
        verify(paymentRepository, times(1)).save(payment);
        verify(kafkaTemplate, times(1))
                .send(
                        eq("create-payment"),
                        eq(payment.getId()),
                        argThat(event ->
                                event.getPaymentId().equals("1") &&
                                        event.getOrderId().equals("1") &&
                                        event.getStatus().equals("SUCCESS")
                        )
                );
    }

    @Test
    void getByUserId_success() {
        //given
        when(paymentRepository.findPaymentsByUserId("1"))
                .thenReturn(List.of(payment));

        when(paymentMapper.toDto(payment))
                .thenReturn(paymentResponseDto);

        //when
        List<PaymentResponseDto> result = paymentService.getByUserId("1");

        //then
        assertEquals(1, result.size());
        verify(paymentRepository).findPaymentsByUserId("1");
        verify(paymentMapper).toDto(payment);
    }

    @Test
    void getByOrderId_success() {
        //given
        when(paymentRepository.findPaymentsByOrderId("1"))
                .thenReturn(List.of(payment));

        when(paymentMapper.toDto(payment))
                .thenReturn(paymentResponseDto);

        //when
        List<PaymentResponseDto> result = paymentService.getByOrderId("1");

        //then
        assertEquals(1, result.size());
        verify(paymentRepository).findPaymentsByOrderId("1");
    }

    @Test
    void getByStatus_success() {
        //given
        when(paymentRepository.findPaymentsByStatus("SUCCESS"))
                .thenReturn(List.of(payment));

        when(paymentMapper.toDto(payment))
                .thenReturn(paymentResponseDto);

        //when
        List<PaymentResponseDto> result = paymentService.getByStatus("SUCCESS");

        //then
        assertEquals(1, result.size());
        verify(paymentRepository).findPaymentsByStatus("SUCCESS");
    }

    @Test
    void getByUserId_empty() {
        //given
        when(paymentRepository.findPaymentsByUserId("1"))
                .thenReturn(List.of());

        //when
        List<PaymentResponseDto> result = paymentService.getByUserId("1");

        //then
        assertTrue(result.isEmpty());
    }

    @Test
    void getByOrderId_empty() {
        //given
        when(paymentRepository.findPaymentsByOrderId("1"))
                .thenReturn(List.of());

        //when
        List<PaymentResponseDto> result = paymentService.getByOrderId("1");

        //then
        assertTrue(result.isEmpty());
    }
}
