package com.keke125.vaultguard.web.spring.password.service;

import com.keke125.vaultguard.web.spring.account.entity.User;
import com.keke125.vaultguard.web.spring.password.entity.Password;
import com.keke125.vaultguard.web.spring.password.repository.PasswordRepository;
import com.keke125.vaultguard.web.spring.password.request.SavePasswordRequest;
import com.keke125.vaultguard.web.spring.password.request.UpdatePasswordRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class PasswordService {
    private final PasswordRepository repository;

    public PasswordService(PasswordRepository repository) {
        this.repository = repository;
    }

    public void savePassword(SavePasswordRequest request, User user) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        String timeStamp = LocalDateTime.now().format(formatter);
        Password password = new Password();
        password.setPassword(request.getPassword());
        password.setUsername(request.getUsername());
        password.setName(request.getName());
        password.setTotp(request.getTotp());
        password.setNotes(request.getNotes());
        password.setUrlList(request.getUrlList());
        password.setCreatedDateTime(timeStamp);
        password.setLastModifiedDateTime(timeStamp);
        password.setUser(user);
        repository.save(password);
    }

    public void updatePassword(UpdatePasswordRequest request, User user, Password oldPassword) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        String timeStamp = LocalDateTime.now().format(formatter);
        oldPassword.setPassword(request.getPassword());
        oldPassword.setUsername(request.getUsername());
        oldPassword.setName(request.getName());
        oldPassword.setTotp(request.getTotp());
        oldPassword.setNotes(request.getNotes());
        oldPassword.setUrlList(request.getUrlList());
        oldPassword.setLastModifiedDateTime(timeStamp);
        oldPassword.setUser(user);
        repository.save(oldPassword);
    }

    public List<Password> findAllByUser(User user) {
        return repository.findAllByUser(user);
    }

    public Optional<Password> findByUidAndUser(String uid, User user) {
        return repository.findByUidAndUser(uid, user);
    }

    public void deletePassword(Password password) {
        repository.delete(password);
    }

    public Boolean isPasswordExists(String name, String username, User user) {
        return repository.findByNameAndUsernameAndUser(name, username, user).isPresent();
    }

    public void savePasswordByPassword(Password password, User user) {
        password.setUser(user);
        repository.save(password);
    }

    public void deletePasswords(User user) {
        repository.deleteAllByUser(user);
    }
}
