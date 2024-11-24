package com.keke125.vaultguard.web.spring.password.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SavePasswordsResponse {
    private int successCnt;
    private int failedCnt;
}
