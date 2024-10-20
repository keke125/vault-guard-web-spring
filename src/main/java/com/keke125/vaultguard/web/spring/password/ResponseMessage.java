package com.keke125.vaultguard.web.spring.password;

import java.util.Collections;
import java.util.Map;

public class ResponseMessage {
    public static String successSavePasswordMessage = "密碼儲存成功";
    public static String passwordDuplicatedMessage = "已有相同名稱及帳號的密碼!";
    public static Map<String, String> successSavePasswordResponse = Collections.singletonMap("message", successSavePasswordMessage);
    public static Map<String, String> passwordDuplicatedResponse = Collections.singletonMap("message", passwordDuplicatedMessage);
}
