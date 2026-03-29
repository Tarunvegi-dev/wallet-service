package com.wallet.wallet_service.common.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;

import java.util.List;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
public class ApiErrorDTO
{
    HttpStatus status;
    String message;
    List<String> subErrors;
}
