package practical.task.paymentservice.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import practical.task.paymentservice.dto.PaymentCreateDto;
import practical.task.paymentservice.dto.PaymentResponseDto;
import practical.task.paymentservice.service.PaymentService;

import java.util.List;

@RestController
@RequestMapping("/payments")
public class PaymentController {

    private final PaymentService paymentService;

    @Autowired
    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/create")
    public ResponseEntity<PaymentResponseDto> create(@Valid @RequestBody PaymentCreateDto paymentCreateDto) {
        PaymentResponseDto paymentResponseDto = paymentService.create(paymentCreateDto);
        return ResponseEntity.ok(paymentResponseDto);
    }

    @GetMapping("/by-user")
    public List<PaymentResponseDto> getByUserId(@RequestParam String userId) {
        return paymentService.getByUserId(userId);
    }

    @GetMapping("/by-order")
    public List<PaymentResponseDto> getByOrderId(@RequestParam String orderId) {
        return paymentService.getByOrderId(orderId);
    }

    @GetMapping("/by-status")
    public List<PaymentResponseDto> getByStatus(@RequestParam String status) {
        return paymentService.getByStatus(status.toUpperCase());
    }
}
