package com.trading.bot.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "trading")
public class TradingProperties {

    private ExchangeConfig exchange = new ExchangeConfig();
    private StrategyConfig strategy = new StrategyConfig();
    private MovingAverageConfig movingAverage = new MovingAverageConfig();
    private RiskConfig risk = new RiskConfig();
    private MonitoringConfig monitoring = new MonitoringConfig();

    @Data
    public static class ExchangeConfig {
        private String name;
        private String apiKey;
        private String apiSecret;
        private boolean testnet;
        private String baseUrl;
        private String wsUrl;
    }

    @Data
    public static class StrategyConfig {
        private boolean enabled;
        private String symbol;
        private String interval;
    }

    @Data
    public static class MovingAverageConfig {
        private int shortPeriod;
        private int longPeriod;
    }

    @Data
    public static class RiskConfig {
        private double maxTradeAmount;
        private double stopLossPercentage;
        private double takeProfitPercentage;
    }

    @Data
    public static class MonitoringConfig {
        private long updateInterval;
    }
}
