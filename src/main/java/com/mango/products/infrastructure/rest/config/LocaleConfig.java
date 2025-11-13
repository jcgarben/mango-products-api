package com.mango.products.infrastructure.rest.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.FixedLocaleResolver;

import java.util.Locale;

@Configuration
public class LocaleConfig {

    @Bean
    public LocaleResolver localeResolver() {
        // Force locale to "en" for all app
        // ignoring la client Accept-Language
        return new FixedLocaleResolver(Locale.forLanguageTag("en"));
    }
}

