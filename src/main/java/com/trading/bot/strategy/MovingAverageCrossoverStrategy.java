package com.trading.bot.strategy;

import com.trading.bot.config.TradingProperties;
import com.trading.bot.model.Candlestick;
import com.trading.bot.model.TradeSignal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class MovingAverageCrossoverStrategy implements TradingStrategy {

    private final TradingProperties properties;
    private TradeSignal.SignalType lastSignal = TradeSignal.SignalType.HOLD;

    @Override
    public TradeSignal analyze(List<Candlestick> candlesticks) {
        if (candlesticks.isEmpty()) {
            return createHoldSignal("No data available");
        }

        int shortPeriod = properties.getMovingAverage().getShortPeriod();
        int longPeriod = properties.getMovingAverage().getLongPeriod();

        if (candlesticks.size() < longPeriod) {
            return createHoldSignal("Insufficient data for analysis");
        }

        // Calculate moving averages
        BigDecimal shortMA = calculateSMA(candlesticks, shortPeriod);
        BigDecimal longMA = calculateSMA(candlesticks, longPeriod);

        log.info("MA Analysis - Short MA ({}): {}, Long MA ({}): {}",
                shortPeriod, shortMA, longPeriod, longMA);

        // Get current price
        BigDecimal currentPrice = candlesticks.get(candlesticks.size() - 1).getClose();

        // Generate signal based on crossover
        TradeSignal signal;

        if (shortMA.compareTo(longMA) > 0 && lastSignal != TradeSignal.SignalType.BUY) {
            // Bullish crossover - Short MA crosses above Long MA
            signal = TradeSignal.builder()
                    .type(TradeSignal.SignalType.BUY)
                    .symbol(properties.getStrategy().getSymbol())
                    .price(currentPrice)
                    .quantity(calculateQuantity(currentPrice))
                    .reason(String.format("Bullish crossover: Short MA (%.2f) > Long MA (%.2f)",
                            shortMA, longMA))
                    .timestamp(LocalDateTime.now())
                    .build();
            lastSignal = TradeSignal.SignalType.BUY;
            log.info("BUY SIGNAL: {}", signal.getReason());

        } else if (shortMA.compareTo(longMA) < 0 && lastSignal == TradeSignal.SignalType.BUY) {
            // Bearish crossover - Short MA crosses below Long MA
            signal = TradeSignal.builder()
                    .type(TradeSignal.SignalType.SELL)
                    .symbol(properties.getStrategy().getSymbol())
                    .price(currentPrice)
                    .quantity(calculateQuantity(currentPrice))
                    .reason(String.format("Bearish crossover: Short MA (%.2f) < Long MA (%.2f)",
                            shortMA, longMA))
                    .timestamp(LocalDateTime.now())
                    .build();
            lastSignal = TradeSignal.SignalType.SELL;
            log.info("SELL SIGNAL: {}", signal.getReason());

        } else {
            signal = createHoldSignal(String.format("No crossover detected. Short MA: %.2f, Long MA: %.2f",
                    shortMA, longMA));
        }

        return signal;
    }

    /**
     * Calculate Simple Moving Average
     */
    private BigDecimal calculateSMA(List<Candlestick> candlesticks, int period) {
        int size = candlesticks.size();
        BigDecimal sum = BigDecimal.ZERO;

        for (int i = size - period; i < size; i++) {
            sum = sum.add(candlesticks.get(i).getClose());
        }

        return sum.divide(BigDecimal.valueOf(period), 8, RoundingMode.HALF_UP);
    }

    /**
     * Calculate trade quantity based on risk management
     */
    private BigDecimal calculateQuantity(BigDecimal currentPrice) {
        BigDecimal maxAmount = BigDecimal.valueOf(properties.getRisk().getMaxTradeAmount());
        return maxAmount.divide(currentPrice, 8, RoundingMode.HALF_UP);
    }

    /**
     * Create a HOLD signal
     */
    private TradeSignal createHoldSignal(String reason) {
        return TradeSignal.builder()
                .type(TradeSignal.SignalType.HOLD)
                .symbol(properties.getStrategy().getSymbol())
                .reason(reason)
                .timestamp(LocalDateTime.now())
                .build();
    }

    @Override
    public String getStrategyName() {
        return "Moving Average Crossover Strategy";
    }
}
