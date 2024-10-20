package com.keke125.vaultguard.web.spring.password.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SavePasswordRequest {
    @NotBlank(message = "請填寫名稱!")
    private String name;
    @NotBlank(message = "請填寫帳號!")
    private String username;
    private String password;
    private List<String> urlList;
    private String notes;
    private String totp;
}
