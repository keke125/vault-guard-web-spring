package com.keke125.vaultguard.web.spring.account.request;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SignupRequest {

    @NotBlank(message = "請填寫帳號!")
    @Size(min = 1, max = 128, message = "帳號長度必須在{min}-{max}字元之間!")
    @Column(unique = true)
    private String username;

    @NotBlank(message = "請填寫主密碼!")
    @Size(min = 8, max = 128, message = "主密碼長度必須在{min}-{max}字元之間!")
    private String password;

    @NotBlank(message = "請填寫電子信箱!")
    @Email(message = "電子信箱格式錯誤!")
    @Column(unique = true)
    private String email;
}
