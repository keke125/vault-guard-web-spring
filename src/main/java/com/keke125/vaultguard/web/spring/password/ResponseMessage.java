package com.keke125.vaultguard.web.spring.password;

import java.util.Collections;
import java.util.Map;

public class ResponseMessage {
    public static String successSavePasswordMessage = "成功儲存密碼";
    public static String passwordDuplicatedMessage = "已有相同名稱及帳號的密碼!";
    public static String passwordNotFoundMessage = "找不到密碼!";
    public static String successUpdatePasswordMessage = "成功更新密碼";
    public static String successDeletePasswordMessage = "成功刪除密碼";
    public static String disallowedExportFileTypeMessage = "不允許匯出的檔案類型!";
    public static String disallowedImportTypeMessage = "不允許匯入的類型!";
    public static String missingUploadFileMessage = "找不到上傳檔案!";
    public static String errorUploadFileTypeCSVMessage = "請上傳CSV檔!";
    public static String errorUploadFileTypeJSONMessage = "請上傳JSON檔!";
    public static String errorSavePasswordsMessage = "請檢查上傳檔案是否正確!";
    public static String errorMainPasswordMessage = "主密碼輸入錯誤!";
    public static String mainPasswordNotFoundMessage = "請輸入主密碼!";
    public static String successDeletePasswordsMessage = "成功清空密碼庫";
    public static Map<String, String> successSavePasswordResponse = Collections.singletonMap("message", successSavePasswordMessage);
    public static Map<String, String> passwordDuplicatedResponse = Collections.singletonMap("message", passwordDuplicatedMessage);
    public static Map<String, String> passwordNotFoundResponse = Collections.singletonMap("message", passwordNotFoundMessage);
    public static Map<String, String> successUpdatePasswordResponse = Collections.singletonMap("message", successUpdatePasswordMessage);
    public static Map<String, String> successDeletePasswordResponse = Collections.singletonMap("message", successDeletePasswordMessage);
    public static Map<String, String> disallowedExportFileTypeResponse = Collections.singletonMap("message", disallowedExportFileTypeMessage);
    public static Map<String, String> disallowedImportTypeResponse = Collections.singletonMap("message", disallowedImportTypeMessage);
    public static Map<String, String> missingUploadFileResponse = Collections.singletonMap("message", missingUploadFileMessage);
    public static Map<String, String> errorUploadFileTypeCSVResponse = Collections.singletonMap("message", errorUploadFileTypeCSVMessage);
    public static Map<String, String> errorUploadFileTypeJSONResponse = Collections.singletonMap("message", errorUploadFileTypeJSONMessage);
    public static Map<String, String> errorSavePasswordsResponse = Collections.singletonMap("message", errorSavePasswordsMessage);
    public static Map<String, String> errorMainPasswordResponse = Collections.singletonMap("message", errorMainPasswordMessage);
    public static Map<String, String> mainPasswordNotFoundResponse = Collections.singletonMap("message", mainPasswordNotFoundMessage);
    public static Map<String, String> successDeletePasswordsResponse = Collections.singletonMap("message", successDeletePasswordsMessage);
}
