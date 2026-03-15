package com.walllet.wallet_service.user.model;

import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
public class SignupRequestDTO
{
    @Email(message = "Provided Email is invalid")
    @NotNull
    @NotBlank
    String email;

    @NotNull
    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 30, message = "Password must be between 8 and 30 characters")
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&+=])(?=\\S+$).{8,30}$",
            message = "Password must contain at least one digit, one lowercase, one uppercase, and one special character")
    String password;

    @NotNull
    @NotBlank(message = "Name is required")
    String name;

    @NotNull
    @NotBlank
    @Pattern(regexp="^[0-9]{10}$", message="Mobile number must be 10 digits")
    String mobileNumber;
}
