package com.trading.bot.service;

import com.trading.bot.config.TradingProperties;
import com.trading.bot.model.Candlestick;
import com.trading.bot.model.TradeSignal;
import com.trading.bot.strategy.TradingStrategy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TradingService {

    private final TradingStrategy tradingStrategy;
    private final MarketDataService marketDataService;
    private final ExchangeApiService exchangeApiService;
    private final TradingProperties properties;

    private TradeSignal lastExecutedSignal;

    /**
     * Main trading loop - analyze market and execute trades
     */
    @Scheduled(fixedDelayString = "${trading.monitoring.update-interval}")
    public void executeTradingCycle() {
        if (!properties.getStrategy().isEnabled()) {
            log.debug("Trading is disabled. Skipping trading cycle.");
            return;
        }

        try {
            // Get recent market data
            List<Candlestick> candlesticks = marketDataService.getRecentCandlesticks();

            if (candlesticks.isEmpty()) {
                log.warn("No market data available. Skipping trading cycle.");
                return;
            }

            // Analyze market using strategy
            TradeSignal signal = tradingStrategy.analyze(candlesticks);

            if (signal == null) {
                log.warn("Strategy returned null signal. Skipping.");
                return;
            }

            // Execute trade based on signal
            if (signal.getType() != TradeSignal.SignalType.HOLD) {
                executeTradeSignal(signal);
            }

        } catch (Exception e) {
            log.error("Error in trading cycle: {}", e.getMessage(), e);
        }
    }

    /**
     * Execute a trade signal
     */
    private void executeTradeSignal(TradeSignal signal) {
        log.info("========================================");
        log.info("TRADE SIGNAL RECEIVED: {}", signal.getType());
        log.info("Symbol: {}", signal.getSymbol());
        log.info("Price: {}", signal.getPrice());
        log.info("Quantity: {}", signal.getQuantity());
        log.info("Reason: {}", signal.getReason());
        log.info("========================================");

        // Prevent duplicate executions
        if (lastExecutedSignal != null &&
                lastExecutedSignal.getType() == signal.getType() &&
                lastExecutedSignal.getSymbol().equals(signal.getSymbol())) {
            log.info("Signal already executed. Skipping duplicate.");
            return;
        }

        try {
            String orderId;

            switch (signal.getType()) {
                case BUY -> {
                    orderId = executeBuyOrder(signal);
                    log.info("BUY order executed successfully. Order ID: {}", orderId);
                }
                case SELL -> {
                    orderId = executeSellOrder(signal);
                    log.info("SELL order executed successfully. Order ID: {}", orderId);
                }
                default -> log.warn("Unknown signal type: {}", signal.getType());
            }

            lastExecutedSignal = signal;

        } catch (Exception e) {
            log.error("Error executing trade signal: {}", e.getMessage(), e);
        }
    }

    /**
     * Execute a buy order
     */
    private String executeBuyOrder(TradeSignal signal) {
        validateTradeAmount(signal);

        String orderId = exchangeApiService.placeMarketOrder(
                signal.getSymbol(),
                "BUY",
                signal.getQuantity().doubleValue()
        );

        // In production, you might want to:
        // 1. Set stop-loss order
        // 2. Set take-profit order
        // 3. Track the position
        // 4. Store in database

        return orderId;
    }

    /**
     * Execute a sell order
     */
    private String executeSellOrder(TradeSignal signal) {
        String orderId = exchangeApiService.placeMarketOrder(
                signal.getSymbol(),
                "SELL",
                signal.getQuantity().doubleValue()
        );

        return orderId;
    }

    /**
     * Validate trade amount against risk parameters
     */
    private void validateTradeAmount(TradeSignal signal) {
        BigDecimal tradeValue = signal.getPrice().multiply(signal.getQuantity());
        BigDecimal maxAmount = BigDecimal.valueOf(properties.getRisk().getMaxTradeAmount());

        if (tradeValue.compareTo(maxAmount) > 0) {
            log.warn("Trade amount {} exceeds maximum allowed amount {}",
                    tradeValue, maxAmount);
            throw new IllegalArgumentException("Trade amount exceeds risk limit");
        }
    }

    /**
     * Get the last executed signal
     */
    public TradeSignal getLastExecutedSignal() {
        return lastExecutedSignal;
    }

    /**
     * Manually trigger a trading cycle (useful for testing)
     */
    public void runCycleNow() {
        log.info("Manual trading cycle triggered");
        executeTradingCycle();
    }
}
