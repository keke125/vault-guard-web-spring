package com.keke125.vaultguard.web.spring.password.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeletePasswordsRequest {
    private List<String> passwordUidList;
}
