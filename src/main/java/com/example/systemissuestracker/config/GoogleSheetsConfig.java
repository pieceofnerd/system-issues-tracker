package com.example.systemissuestracker.config;

import com.google.api.services.sheets.v4.Sheets;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.security.GeneralSecurityException;

@Configuration
public class GoogleSheetsConfig {

    @Bean
    public Sheets sheetsService() throws GeneralSecurityException, IOException {
        return GoogleSheetsServiceUtil.getSheetsService();
    }
}
