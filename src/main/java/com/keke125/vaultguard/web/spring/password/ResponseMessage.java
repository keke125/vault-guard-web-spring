package com.keke125.vaultguard.web.spring.password;

import java.util.Collections;
import java.util.Map;

public class ResponseMessage {
    public static String successSavePasswordMessage = "成功儲存密碼";
    public static String passwordDuplicatedMessage = "已有相同名稱及帳號的密碼!";
    public static String passwordNotFoundMessage = "找不到密碼!";
    public static String successUpdatePasswordMessage = "成功更新密碼";
    public static String successDeletePasswordMessage = "成功刪除密碼";
    public static Map<String, String> successSavePasswordResponse = Collections.singletonMap("message", successSavePasswordMessage);
    public static Map<String, String> passwordDuplicatedResponse = Collections.singletonMap("message", passwordDuplicatedMessage);
    public static Map<String, String> passwordNotFoundResponse = Collections.singletonMap("message", passwordNotFoundMessage);
    public static Map<String, String> successUpdatePasswordResponse = Collections.singletonMap("message", successUpdatePasswordMessage);
    public static Map<String, String> successDeletePasswordResponse = Collections.singletonMap("message", successDeletePasswordMessage);
}
