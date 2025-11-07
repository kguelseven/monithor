package com.trading.bot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class CryptoTradingBotApplication {

    public static void main(String[] args) {
        SpringApplication.run(CryptoTradingBotApplication.class, args);
    }
}
