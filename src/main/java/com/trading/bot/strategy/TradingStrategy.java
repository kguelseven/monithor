package com.trading.bot.strategy;

import com.trading.bot.model.Candlestick;
import com.trading.bot.model.TradeSignal;

import java.util.List;

public interface TradingStrategy {

    /**
     * Analyze market data and generate trade signals
     */
    TradeSignal analyze(List<Candlestick> candlesticks);

    /**
     * Get strategy name
     */
    String getStrategyName();
}
