package com.keke125.vaultguard.web.spring.account.request;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChangeEmailRequest {

    @NotBlank(message = "請填寫電子信箱!")
    @Email(message = "電子信箱格式錯誤!")
    private String newEmail;

}
