package com.keke125.vaultguard.web.spring.password.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.keke125.vaultguard.web.spring.account.entity.User;
import com.keke125.vaultguard.web.spring.account.response.UserIdentity;
import com.keke125.vaultguard.web.spring.account.service.UserService;
import com.keke125.vaultguard.web.spring.password.entity.Password;
import com.keke125.vaultguard.web.spring.password.request.DeletePasswordRequest;
import com.keke125.vaultguard.web.spring.password.request.DeletePasswordsRequest;
import com.keke125.vaultguard.web.spring.password.request.SavePasswordRequest;
import com.keke125.vaultguard.web.spring.password.request.UpdatePasswordRequest;
import com.keke125.vaultguard.web.spring.password.response.DeletePasswordsResponse;
import com.keke125.vaultguard.web.spring.password.response.SavePasswordsResponse;
import com.keke125.vaultguard.web.spring.password.service.FileService;
import com.keke125.vaultguard.web.spring.password.service.PasswordService;
import jakarta.validation.Valid;
import org.apache.tika.Tika;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static com.keke125.vaultguard.web.spring.account.ResponseMessage.userNotFoundMessage;
import static com.keke125.vaultguard.web.spring.password.ResponseMessage.*;
import static com.keke125.vaultguard.web.spring.password.service.FileService.APPLICATION_JSON_TYPE_LIST;
import static com.keke125.vaultguard.web.spring.password.service.FileService.TEXT_CSV_TYPE_LIST;

@RestController
@RequestMapping(value = "/api/v1/password", produces = MediaType.APPLICATION_JSON_VALUE)
public class PasswordController {
    private final PasswordService passwordService;
    private final UserIdentity userIdentity;
    private final static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Tika tika = new Tika();
    private final FileService fileService;
    private final UserService userService;

    public PasswordController(PasswordService passwordService, UserIdentity userIdentity, FileService fileService, UserService userService) {
        this.passwordService = passwordService;
        this.userIdentity = userIdentity;
        this.fileService = fileService;
        this.userService = userService;
    }

    @PostMapping("/password")
    public ResponseEntity<Map<String, String>> savePassword(@Valid @RequestBody SavePasswordRequest request) {
        Optional<User> user = userIdentity.getCurrentUser();
        if (user.isEmpty()) {
            throw new UsernameNotFoundException(userNotFoundMessage);
        }
        if (passwordService.isPasswordExists(request.getName(), request.getUsername(), user.get().getUid())) {
            return ResponseEntity.badRequest().body(passwordDuplicatedResponse);
        }
        passwordService.savePassword(request, user.get());
        return ResponseEntity.ok(successSavePasswordResponse);
    }

    @PatchMapping("/password")
    public ResponseEntity<Map<String, String>> updatePassword(@Valid @RequestBody UpdatePasswordRequest request) {
        Optional<User> user = userIdentity.getCurrentUser();
        if (user.isEmpty()) {
            throw new UsernameNotFoundException(userNotFoundMessage);
        }
        Optional<Password> oldPassword = passwordService.findByUidAndUserUid(request.getUid(), user.get().getUid());
        if (oldPassword.isEmpty()) {
            return ResponseEntity.badRequest().body(passwordNotFoundResponse);
        }
        if ((!Objects.equals(request.getName(), oldPassword.get().getName()) && !Objects.equals(request.getUsername(), oldPassword.get().getUsername())) && passwordService.isPasswordExists(request.getName(), request.getUsername(), user.get().getUid())) {
            return ResponseEntity.badRequest().body(passwordDuplicatedResponse);
        }
        passwordService.updatePassword(request, user.get(), oldPassword.get());
        return ResponseEntity.ok(successUpdatePasswordResponse);
    }

    @GetMapping("/passwords")
    public ResponseEntity<?> getAllPasswords(@RequestParam String type, @RequestHeader("mainPassword") Optional<String> header) {
        Optional<User> user = userIdentity.getCurrentUser();
        if (user.isEmpty()) {
            throw new UsernameNotFoundException(userNotFoundMessage);
        }
        List<Password> passwords = passwordService.findAllByUserUid(user.get().getUid());
        if (Objects.equals(type, "file")) {
            if (header.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(mainPasswordNotFoundResponse);
            }
            if (userService.isMainPasswordMismatch(user.get().getUsername(), header.get())) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorMainPasswordResponse);
            }
            ByteArrayResource resource;
            String fileName;
            try {
                String jsonData = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(passwords);
                resource = new ByteArrayResource(jsonData.getBytes());
                fileName = "vaultguard-" + LocalDateTime.now().format(formatter) + "-export.json";
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
            return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"").contentLength(resource.contentLength()).contentType(MediaType.APPLICATION_JSON).body(resource);
        } else if (Objects.equals(type, "json")) {
            return ResponseEntity.ok(passwordService.findAllByUserUid(user.get().getUid()));
        } else {
            return ResponseEntity.badRequest().body(disallowedExportFileTypeResponse);
        }
    }

    @GetMapping("/password/{uid}")
    public ResponseEntity<Password> getPasswordByUid(@PathVariable String uid) {
        Optional<User> user = userIdentity.getCurrentUser();
        if (user.isEmpty()) {
            throw new UsernameNotFoundException(userNotFoundMessage);
        }
        if (passwordService.findByUidAndUserUid(uid, user.get().getUid()).isPresent()) {
            return ResponseEntity.ok(passwordService.findByUidAndUserUid(uid, user.get().getUid()).get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/password")
    public ResponseEntity<Map<String, String>> deletePassword(@Valid @RequestBody DeletePasswordRequest request) {
        Optional<User> user = userIdentity.getCurrentUser();
        if (user.isEmpty()) {
            throw new UsernameNotFoundException(userNotFoundMessage);
        }
        Optional<Password> deletedPassword = passwordService.findByUidAndUserUid(request.getUid(), user.get().getUid());
        if (deletedPassword.isEmpty()) {
            return ResponseEntity.badRequest().body(passwordNotFoundResponse);
        }
        passwordService.deletePassword(deletedPassword.get());
        return ResponseEntity.ok(successDeletePasswordResponse);
    }

    @PostMapping(path = "/passwords", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    @ResponseBody
    public ResponseEntity<?> savePasswords(@RequestParam String type, @RequestParam("file") MultipartFile file) {
        Optional<User> user = userIdentity.getCurrentUser();
        if (user.isEmpty()) {
            throw new UsernameNotFoundException(userNotFoundMessage);
        }
        if (Objects.equals(type, "GPM") || Objects.equals(type, "VG")) {
            try {
                // Check File Exists
                if (file.isEmpty()) {
                    return ResponseEntity.badRequest().body(missingUploadFileResponse);
                }
                String mimeType;
                // Check MIME Type
                if (Objects.equals(type, "GPM")) {
                    // Check MIME Type
                    mimeType = tika.detect(file.getInputStream(), file.getOriginalFilename());
                    if (!TEXT_CSV_TYPE_LIST.contains(mimeType)) {
                        return ResponseEntity.badRequest().body(errorUploadFileTypeCSVResponse);
                    }
                } else if (Objects.equals(type, "VG")) {
                    mimeType = tika.detect(file.getInputStream());
                    if (!APPLICATION_JSON_TYPE_LIST.contains(mimeType)) {
                        return ResponseEntity.badRequest().body(errorUploadFileTypeJSONResponse);
                    }
                }
                InputStream inputStream = file.getInputStream();
                List<Password> passwords = Collections.emptyList();
                if (Objects.equals(type, "GPM")) {
                    passwords = fileService.readCsvFromGPM(inputStream);
                } else if (Objects.equals(type, "VG")) {
                    passwords = fileService.readJsonFromVG(inputStream);
                }
                int successCnt = 0;
                int failedCnt = 0;
                for (Password password : passwords) {
                    if (password.getName().isEmpty() || password.getUsername().isEmpty()) {
                        failedCnt += 1;
                        continue;
                    }
                    if (passwordService.isPasswordExists(password.getName(), password.getUsername(), user.get().getUid())) {
                        failedCnt += 1;
                        continue;
                    }
                    passwordService.savePasswordByPassword(password, user.get());
                    successCnt += 1;
                }
                return ResponseEntity.ok(new SavePasswordsResponse(successCnt, failedCnt));
            } catch (Exception e) {
                return ResponseEntity.badRequest().body(errorSavePasswordsResponse);
            }
        } else {
            return ResponseEntity.badRequest().body(disallowedImportTypeResponse);
        }
    }

    @DeleteMapping("/vault")
    public ResponseEntity<Map<String, String>> clearVault(@RequestHeader("mainPassword") Optional<String> header) {
        Optional<User> user = userIdentity.getCurrentUser();
        if (user.isEmpty()) {
            throw new UsernameNotFoundException(userNotFoundMessage);
        }
        if (header.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(mainPasswordNotFoundResponse);
        }
        if (userService.isMainPasswordMismatch(user.get().getUsername(), header.get())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorMainPasswordResponse);
        }
        passwordService.deletePasswords(user.get().getUid());
        return ResponseEntity.ok(successDeletePasswordsResponse);
    }

    @PostMapping("/delete-passwords")
    public ResponseEntity<DeletePasswordsResponse> deletePasswords(@Valid @RequestBody DeletePasswordsRequest request) {
        Optional<User> user = userIdentity.getCurrentUser();
        if (user.isEmpty()) {
            throw new UsernameNotFoundException(userNotFoundMessage);
        }
        int successCnt = 0;
        int failedCnt = 0;
        List<String> passwordUidList = request.getPasswordUidList();
        for (String passwordUid : passwordUidList) {
            Optional<Password> deletedPassword = passwordService.findByUidAndUserUid(passwordUid, user.get().getUid());
            if (deletedPassword.isEmpty()) {
                failedCnt++;
                continue;
            }
            passwordService.deletePassword(deletedPassword.get());
            successCnt += 1;
        }
        return ResponseEntity.ok(new DeletePasswordsResponse(successCnt, failedCnt));
    }
}
