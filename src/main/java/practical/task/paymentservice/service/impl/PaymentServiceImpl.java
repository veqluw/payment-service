package practical.task.paymentservice.service.impl;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import practical.task.paymentservice.dto.PaymentCreateDto;
import practical.task.paymentservice.dto.PaymentResponseDto;
import practical.task.paymentservice.mapper.PaymentMapper;
import practical.task.paymentservice.misc.PaymentStatus;
import practical.task.paymentservice.model.Payment;
import practical.task.paymentservice.repository.PaymentRepository;
import practical.task.paymentservice.service.PaymentService;
import practical.task.paymentservice.service.client.RandomNumberClient;

import java.util.List;

@Service
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;
    private final RandomNumberClient randomNumberClient;

    @Autowired
    public PaymentServiceImpl(PaymentRepository paymentRepository,
                              PaymentMapper paymentMapper,
                              RandomNumberClient randomNumberClient
                              ) {
        this.paymentRepository = paymentRepository;
        this.paymentMapper = paymentMapper;
        this.randomNumberClient = randomNumberClient;
    }

    @Override
    public PaymentResponseDto create(@Valid PaymentCreateDto paymentCreateDto) {
        Payment payment = paymentMapper.toEntity(paymentCreateDto);
        Integer randomNumber = randomNumberClient.getRandomNumber().getFirst();

        payment.setStatus(isEven(randomNumber) ? PaymentStatus.SUCCESS.toString() : PaymentStatus.FAILED.toString());

        paymentRepository.save(payment);

        return paymentMapper.toDto(payment);
    }

    private boolean isEven(Integer val) {return val % 2 == 0;}

    @Override
    public List<PaymentResponseDto> getByUserId(String id) {
        List<Payment> payments = paymentRepository.findPaymentsByUserId(id);

        return payments.stream()
                .map(paymentMapper::toDto)
                .toList();
    }

    @Override
    public List<PaymentResponseDto> getByOrderId(String id) {
        List<Payment> payments = paymentRepository.findPaymentsByOrderId(id);

        return payments.stream()
                .map(paymentMapper::toDto)
                .toList();
    }

    @Override
    public List<PaymentResponseDto> getByStatus(String status) {
        List<Payment> payments = paymentRepository.findPaymentsByStatus(status);

        return payments.stream()
                .map(paymentMapper::toDto)
                .toList();
    }
}
