package com.keke125.vaultguard.web.spring.password.service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.keke125.vaultguard.web.spring.password.entity.Password;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service
public class FileService {
    public final static List<String> TEXT_CSV_TYPE_LIST = Arrays.asList("text/csv", "text/comma-separated-values");
    public final static List<String> APPLICATION_JSON_TYPE_LIST = List.of("application/json", "text/plain");

    public List<Password> readCsvFromGPM(InputStream inputStream) {
        try {
            CSVFormat csvFormat = CSVFormat.DEFAULT.builder().setHeader().setSkipHeaderRecord(true).build();
            CSVParser csvParser = CSVParser.parse(inputStream, StandardCharsets.UTF_8, csvFormat);
            List<Password> passwords = new java.util.ArrayList<>(Collections.emptyList());
            for (CSVRecord csvRecord : csvParser) {
                String name = csvRecord.get(0);
                String username = csvRecord.get(2);
                String password = csvRecord.get(3);
                String notes = csvRecord.get(4);
                List<String> urlList = Collections.singletonList(csvRecord.get(1));
                String timeStamp;
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
                timeStamp = LocalDateTime.now().format(formatter);
                Password passwordItem = new Password(name, username, password, urlList, notes, "", timeStamp, timeStamp, "");
                passwords.add(passwordItem);
            }
            return passwords;
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    public List<Password> readJsonFromVG(InputStream inputStream) {
        Gson gson = new Gson();
        Reader inputReader = new InputStreamReader(inputStream);
        JsonReader reader = new JsonReader(inputReader);
        TypeToken<List<Password>> listType = new TypeToken<>() {
        };
        List<Password> passwordsFromJson = gson.fromJson(reader, listType);
        List<Password> passwords = new java.util.ArrayList<>(Collections.emptyList());
        for (Password passwordRecord : passwordsFromJson) {
            Password passwordItem = getPasswordItem(passwordRecord);
            passwords.add(passwordItem);
        }
        return passwords;
    }

    private static Password getPasswordItem(Password passwordRecord) {
        String name = passwordRecord.getName();
        String username = passwordRecord.getUsername();
        String password = passwordRecord.getPassword();
        String notes = passwordRecord.getNotes();
        String totp = passwordRecord.getTotp();
        List<String> urlList = passwordRecord.getUrlList();
        String createdDateTime = passwordRecord.getCreatedDateTime();
        String lastModifiedDateTime = passwordRecord.getLastModifiedDateTime();
        return new Password(name, username, password, urlList, notes, totp, createdDateTime, lastModifiedDateTime, "");
    }
}
