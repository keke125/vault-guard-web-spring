package com.keke125.vaultguard.web.spring.account;

import java.util.Collections;
import java.util.Map;

public class ResponseMessage {
    public static String successSignupMessage = "成功註冊";
    public static String successUpdateMessage = "成功更新帳號資訊";
    public static String usernameDuplicatedMessage = "輸入的帳號已被使用!";
    public static String emailDuplicatedMessage = "輸入的電子信箱已被使用!";
    public static String emailAndUsernameDuplicatedMessage = "輸入的帳號及電子信箱皆已被使用!";
    public static String passwordLengthRangeMessage = "密碼長度必須在8-128字元之間!";
    public static String userNotFoundMessage = "找不到使用者，請先註冊或重新登入!";
    public static String disallowedUpdateUserTypeMessage = "不允許的更新使用者的類型!";
    public static String emptyPasswordMessage = "請填寫主密碼!";
    public static String emptyNewPasswordMessage = "請填寫新的主密碼!";
    public static String emptyEmailMessage = "請填寫新的電子信箱!";
    public static Map<String, String> successSignupResponse = Collections.singletonMap("message", successSignupMessage);
    public static Map<String, String> successUpdateResponse = Collections.singletonMap("message", successUpdateMessage);
    public static Map<String, String> usernameDuplicatedResponse = Collections.singletonMap("message", usernameDuplicatedMessage);
    public static Map<String, String> emailDuplicatedResponse = Collections.singletonMap("message", emailDuplicatedMessage);
    public static Map<String, String> emailAndUsernameDuplicatedResponse = Collections.singletonMap("message", emailAndUsernameDuplicatedMessage);
    public static Map<String, String> passwordLengthRangeResponse = Collections.singletonMap("message", passwordLengthRangeMessage);
    public static Map<String, String> disallowedUpdateUserTypeResponse = Collections.singletonMap("message", disallowedUpdateUserTypeMessage);
    public static Map<String, String> emptyPasswordResponse = Collections.singletonMap("message", emptyPasswordMessage);
    public static Map<String, String> emptyNewPasswordResponse = Collections.singletonMap("message", emptyNewPasswordMessage);
    public static Map<String, String> emptyEmailResponse = Collections.singletonMap("message", emptyEmailMessage);
}
