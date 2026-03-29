package com.wallet.wallet_service.user.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoginResponse {
    
    @JsonProperty("access_token")
    String token;

    @JsonProperty("user_id")
    Long userId;
}
