package com.keke125.vaultguard.web.spring.account.request;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResetPasswordRequest {

    @NotBlank(message = "請填寫電子信箱!")
    @Email(message = "電子信箱格式錯誤!")
    private String email;

    private String verificationCode;

    @Size(min = 8, max = 128, message = "主密碼長度必須在{min}-{max}字元之間!")
    private String newPassword;
}
