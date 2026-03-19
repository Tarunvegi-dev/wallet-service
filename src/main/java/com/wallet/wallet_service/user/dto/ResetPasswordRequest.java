package com.wallet.wallet_service.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
public class ResetPasswordRequest
{
    @NotNull
    @NotBlank(message = "Current Password is required")
    String oldPassword;

    @NotNull
    @NotBlank(message = "New Password is required")
    @Size(min = 8, max = 30, message = "New Password must be between 8 and 30 characters")
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&+=])(?=\\S+$).{8,30}$",
            message = "New Password must contain at least one digit, one lowercase, one uppercase, and one special character")
    String newPassword;
}
