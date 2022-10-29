package io.citizenjournalist.as.config;

import com.google.cloud.translate.v3beta1.TranslationServiceClient;
import java.io.IOException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GoogleCloudConfiguration {

    @Bean
    public TranslationServiceClient translationServiceClient() throws IOException {
        return TranslationServiceClient.create();
    }
}
