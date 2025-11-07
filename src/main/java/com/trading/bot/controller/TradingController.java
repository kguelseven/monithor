package com.trading.bot.controller;

import com.trading.bot.config.TradingProperties;
import com.trading.bot.model.Ticker;
import com.trading.bot.model.TradeSignal;
import com.trading.bot.service.MarketDataService;
import com.trading.bot.service.TradingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/trading")
@RequiredArgsConstructor
public class TradingController {

    private final TradingService tradingService;
    private final MarketDataService marketDataService;
    private final TradingProperties properties;

    /**
     * Get current bot status
     */
    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getStatus() {
        Map<String, Object> status = new HashMap<>();
        status.put("tradingEnabled", properties.getStrategy().isEnabled());
        status.put("symbol", properties.getStrategy().getSymbol());
        status.put("interval", properties.getStrategy().getInterval());
        status.put("strategy", "Moving Average Crossover");

        Ticker ticker = marketDataService.getCurrentTicker();
        if (ticker != null) {
            status.put("currentPrice", ticker.getLastPrice());
            status.put("priceChange24h", ticker.getPriceChangePercent());
        }

        TradeSignal lastSignal = tradingService.getLastExecutedSignal();
        if (lastSignal != null) {
            status.put("lastSignal", Map.of(
                    "type", lastSignal.getType(),
                    "price", lastSignal.getPrice(),
                    "timestamp", lastSignal.getTimestamp()
            ));
        }

        return ResponseEntity.ok(status);
    }

    /**
     * Manually trigger market data refresh
     */
    @PostMapping("/refresh-market-data")
    public ResponseEntity<String> refreshMarketData() {
        marketDataService.refreshNow();
        return ResponseEntity.ok("Market data refresh triggered");
    }

    /**
     * Manually trigger a trading cycle
     */
    @PostMapping("/run-cycle")
    public ResponseEntity<String> runTradingCycle() {
        tradingService.runCycleNow();
        return ResponseEntity.ok("Trading cycle executed");
    }

    /**
     * Get current market ticker
     */
    @GetMapping("/ticker")
    public ResponseEntity<Ticker> getTicker() {
        Ticker ticker = marketDataService.getCurrentTicker();
        if (ticker == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(ticker);
    }
}
