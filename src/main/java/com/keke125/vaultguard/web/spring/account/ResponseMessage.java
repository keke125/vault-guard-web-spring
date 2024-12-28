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
    public static String userDisabledMessage = "帳號未啟用，請先啟用帳號!";
    public static String disallowedUpdateUserTypeMessage = "不允許的更新使用者的類型!";
    public static String emptyPasswordMessage = "請填寫主密碼!";
    public static String emptyNewPasswordMessage = "請填寫新的主密碼!";
    public static String emptyEmailMessage = "請填寫新的電子信箱!";
    public static String successSendResetPasswordMessage = "已寄出重設密碼確認信至您的信箱，請點擊確認信內的連結完成重設";
    public static String successFinishedResetMessage = "已成功重設密碼，將跳轉至登入頁面";
    public static String failedFinishedResetMessage = "重設連結無效或已過期，請至重設密碼頁面重新取得!";
    public static String successSendActivateAccountMessage = "已寄出啟用帳號確認信至您的信箱，請點擊確認信內的連結完成啟用";
    public static String successFinishedActivateAccountMessage = "已成功啟用帳號，將跳轉至登入頁面";
    public static String failedFinishedActivateAccountMessage = "啟用連結無效或已過期，請至啟用帳號頁面重新取得!";
    public static String alreadyActivatedAccountMessage = "您的帳號已於先前啟用，無須重複啟用!";
    public static String failedSendVerificationCodeMessage = "曾於3分鐘內寄出確認信，請稍後再試!";
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
    public static Map<String, String> successSendResetPasswordResponse = Collections.singletonMap("message", successSendResetPasswordMessage);
    public static Map<String, String> successFinishedResetResponse = Collections.singletonMap("message", successFinishedResetMessage);
    public static Map<String, String> failedFinishedResetResponse = Collections.singletonMap("message", failedFinishedResetMessage);
    public static Map<String, String> successSendActivateAccountResponse = Collections.singletonMap("message", successSendActivateAccountMessage);
    public static Map<String, String> successFinishedActivateAccountResponse = Collections.singletonMap("message", successFinishedActivateAccountMessage);
    public static Map<String, String> failedFinishedActivateAccountResponse = Collections.singletonMap("message", failedFinishedActivateAccountMessage);
    public static Map<String, String> alreadyActivatedAccountResponse = Collections.singletonMap("message", alreadyActivatedAccountMessage);
    public static Map<String, String> failedSendVerificationCodeResponse = Collections.singletonMap("message", failedSendVerificationCodeMessage);
}
