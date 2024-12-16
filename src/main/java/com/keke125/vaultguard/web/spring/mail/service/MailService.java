package com.keke125.vaultguard.web.spring.mail.service;

import com.keke125.vaultguard.web.spring.account.entity.User;
import com.keke125.vaultguard.web.spring.util.AppConfig;
import jakarta.mail.Message;
import jakarta.mail.internet.InternetAddress;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class MailService {
    private final JavaMailSender mailSender;
    private final AppConfig appConfig;

    public MailService(JavaMailSender mailSender, AppConfig appConfig) {
        this.mailSender = mailSender;
        this.appConfig = appConfig;
    }

    @Async
    public void sendMailResetPassword(User user, String token) {

        MimeMessagePreparator preparator = mimeMessage -> {
            mimeMessage.setSubject("[Vault Guard Web] 重設密碼通知", "UTF-8");
            mimeMessage.setRecipient(Message.RecipientType.TO, new InternetAddress(user.getEmail()));
            mimeMessage.setFrom(new InternetAddress(appConfig.getSendFromAddress(), appConfig.getSendFromName()));
            mimeMessage.setText(
                    "親愛的 " + user.getUsername() + " 您好:\n\n"
                            + "我們收到您要求重設密碼的申請。如果您並未提出此請求，請忽略此電子郵件。\n\n"
                            + "若您需要重設密碼，請輸入以下驗證碼：\n\n"
                            + token + "\n\n"
                            + "或點擊以下連結" + "\n\n"
                            + appConfig.getWebUrl() + "/reset-password/" + user.getEmail() + "?token=" + token + "\n\n"
                            + "驗證碼及連結將在30分鐘後失效。請盡快完成重設。\n\n"
                            + "若有任何疑問，請隨時聯繫我們(info@vault.keke125.com)。\n\n"
                            + "感謝您使用我們的服務，Vault Guard Web 團隊敬上。"
                    , "UTF-8");
            mimeMessage.setSentDate(new Date());
        };

        try {
            this.mailSender.send(preparator);
        } catch (MailException ex) {
            System.err.println(ex.getMessage());
        }
    }
}
