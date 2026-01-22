package practical.task.paymentservice.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import practical.task.paymentservice.dto.PaymentCreateDto;
import practical.task.paymentservice.dto.PaymentResponseDto;
import practical.task.paymentservice.model.Payment;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface PaymentMapper {

    @Mapping(target = "status", ignore = true)
    Payment toEntity(PaymentCreateDto paymentCreateDto);

    PaymentResponseDto toDto(Payment payment);
}
