package com.keke125.vaultguard.web.spring.account.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserRequest {

    @Size(min = 8, max = 128, message = "主密碼長度必須在{min}-{max}字元之間!")
    private String oldPassword;

    @Size(min = 8, max = 128, message = "主密碼長度必須在{min}-{max}字元之間!")
    private String newPassword;

    @Email(message = "電子信箱格式錯誤!")
    private String newEmail;

    private String verificationCode;

    private String newUsername;
}
