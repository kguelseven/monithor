package com.trading.bot.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.math.BigDecimal;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Candlestick {

    @JsonFormat(shape = JsonFormat.Shape.NUMBER)
    private Long openTime;

    private BigDecimal open;
    private BigDecimal high;
    private BigDecimal low;
    private BigDecimal close;
    private BigDecimal volume;

    @JsonFormat(shape = JsonFormat.Shape.NUMBER)
    private Long closeTime;

    private BigDecimal quoteAssetVolume;
    private Integer numberOfTrades;
    private BigDecimal takerBuyBaseAssetVolume;
    private BigDecimal takerBuyQuoteAssetVolume;
}
