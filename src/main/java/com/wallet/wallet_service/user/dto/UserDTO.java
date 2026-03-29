package com.wallet.wallet_service.user.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.Instant;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO
{
    Long userId;

    String email;

    String name;

    String mobileNumber;

    Instant createdAt;

    Instant updatedAt;
}
