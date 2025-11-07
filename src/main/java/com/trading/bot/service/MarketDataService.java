package com.trading.bot.service;

import com.trading.bot.config.TradingProperties;
import com.trading.bot.model.Candlestick;
import com.trading.bot.model.Ticker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Slf4j
@Service
@RequiredArgsConstructor
public class MarketDataService {

    private final ExchangeApiService exchangeApiService;
    private final TradingProperties properties;

    private final List<Candlestick> recentCandlesticks = new CopyOnWriteArrayList<>();
    private Ticker currentTicker;

    /**
     * Periodically fetch and update market data
     */
    @Scheduled(fixedDelayString = "${trading.monitoring.update-interval}")
    public void updateMarketData() {
        String symbol = properties.getStrategy().getSymbol();
        String interval = properties.getStrategy().getInterval();

        try {
            // Fetch current ticker
            currentTicker = exchangeApiService.getTicker(symbol);
            if (currentTicker != null) {
                log.debug("Updated ticker for {}: Price={}, Change={}%",
                        symbol,
                        currentTicker.getLastPrice(),
                        currentTicker.getPriceChangePercent());
            }

            // Fetch candlestick data for strategy analysis
            int requiredPeriod = Math.max(
                    properties.getMovingAverage().getShortPeriod(),
                    properties.getMovingAverage().getLongPeriod()
            ) + 10; // Add buffer

            List<Candlestick> candlesticks = exchangeApiService.getCandlesticks(
                    symbol,
                    interval,
                    requiredPeriod
            );

            if (!candlesticks.isEmpty()) {
                recentCandlesticks.clear();
                recentCandlesticks.addAll(candlesticks);
                log.debug("Updated {} candlesticks for {}", candlesticks.size(), symbol);
            }

        } catch (Exception e) {
            log.error("Error updating market data: {}", e.getMessage());
        }
    }

    /**
     * Get current ticker data
     */
    public Ticker getCurrentTicker() {
        return currentTicker;
    }

    /**
     * Get recent candlestick data
     */
    public List<Candlestick> getRecentCandlesticks() {
        return List.copyOf(recentCandlesticks);
    }

    /**
     * Manually refresh market data (useful for testing)
     */
    public void refreshNow() {
        log.info("Manual market data refresh triggered");
        updateMarketData();
    }
}
