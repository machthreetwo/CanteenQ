package com.canteenq;

import com.canteenq.pattern.TokenMachine;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Bean
    public TokenMachine tokenMachine() {
        return TokenMachine.getInstance();
    }
}
