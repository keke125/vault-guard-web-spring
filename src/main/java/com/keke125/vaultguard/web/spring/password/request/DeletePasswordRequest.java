package com.keke125.vaultguard.web.spring.password.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeletePasswordRequest {
    @NotBlank(message = "請填寫UID!")
    private String uid;
}
