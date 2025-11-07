package com.trading.bot.model;

import lombok.Data;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class TradeOrder {

    private String orderId;
    private String symbol;
    private OrderSide side;
    private OrderType type;
    private BigDecimal quantity;
    private BigDecimal price;
    private BigDecimal stopPrice;
    private OrderStatus status;
    private LocalDateTime timestamp;
    private String clientOrderId;

    public enum OrderSide {
        BUY, SELL
    }

    public enum OrderType {
        MARKET, LIMIT, STOP_LOSS, STOP_LOSS_LIMIT, TAKE_PROFIT, TAKE_PROFIT_LIMIT
    }

    public enum OrderStatus {
        NEW, PARTIALLY_FILLED, FILLED, CANCELED, PENDING_CANCEL, REJECTED, EXPIRED
    }
}
