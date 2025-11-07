package com.trading.bot.model;

import lombok.Data;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class TradeSignal {

    private SignalType type;
    private String symbol;
    private BigDecimal price;
    private BigDecimal quantity;
    private String reason;
    private LocalDateTime timestamp;

    public enum SignalType {
        BUY, SELL, HOLD
    }
}
