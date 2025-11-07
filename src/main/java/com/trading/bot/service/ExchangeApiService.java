package com.trading.bot.service;

import com.trading.bot.config.TradingProperties;
import com.trading.bot.model.Candlestick;
import com.trading.bot.model.Ticker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExchangeApiService {

    private final WebClient webClient;
    private final TradingProperties properties;

    /**
     * Get current ticker price for a symbol
     */
    public Ticker getTicker(String symbol) {
        try {
            return webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/api/v3/ticker/24hr")
                            .queryParam("symbol", symbol)
                            .build())
                    .retrieve()
                    .bodyToMono(Ticker.class)
                    .block();
        } catch (Exception e) {
            log.error("Error fetching ticker for {}: {}", symbol, e.getMessage());
            return null;
        }
    }

    /**
     * Get candlestick data for technical analysis
     */
    public List<Candlestick> getCandlesticks(String symbol, String interval, int limit) {
        try {
            List<List<Object>> response = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/api/v3/klines")
                            .queryParam("symbol", symbol)
                            .queryParam("interval", interval)
                            .queryParam("limit", limit)
                            .build())
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<List<List<Object>>>() {})
                    .block();

            if (response != null) {
                return response.stream()
                        .map(this::parseCandlestick)
                        .toList();
            }
        } catch (Exception e) {
            log.error("Error fetching candlesticks for {}: {}", symbol, e.getMessage());
        }
        return List.of();
    }

    /**
     * Place a market order (simplified - requires signature in production)
     */
    public String placeMarketOrder(String symbol, String side, double quantity) {
        if (!properties.getStrategy().isEnabled()) {
            log.warn("Trading is disabled. Set trading.strategy.enabled=true to enable.");
            return "TRADING_DISABLED";
        }

        log.info("Placing {} market order for {} of {}", side, quantity, symbol);

        // In a real implementation, you would:
        // 1. Create a signed request with timestamp
        // 2. Include API key and signature
        // 3. Send to /api/v3/order endpoint
        // 4. Handle the response and return order ID

        // For demonstration purposes:
        String orderId = "SIMULATED_" + System.currentTimeMillis();
        log.info("Order placed successfully. Order ID: {}", orderId);
        return orderId;
    }

    /**
     * Place a limit order (simplified)
     */
    public String placeLimitOrder(String symbol, String side, double quantity, double price) {
        if (!properties.getStrategy().isEnabled()) {
            log.warn("Trading is disabled. Set trading.strategy.enabled=true to enable.");
            return "TRADING_DISABLED";
        }

        log.info("Placing {} limit order for {} of {} at price {}", side, quantity, symbol, price);

        String orderId = "SIMULATED_" + System.currentTimeMillis();
        log.info("Limit order placed successfully. Order ID: {}", orderId);
        return orderId;
    }

    /**
     * Generate signature for authenticated requests
     */
    private String generateSignature(String data) {
        try {
            Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
            SecretKeySpec secret_key = new SecretKeySpec(
                    properties.getExchange().getApiSecret().getBytes(StandardCharsets.UTF_8),
                    "HmacSHA256"
            );
            sha256_HMAC.init(secret_key);

            byte[] hash = sha256_HMAC.doFinal(data.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            log.error("Error generating signature: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Parse raw candlestick data from API response
     */
    private Candlestick parseCandlestick(List<Object> data) {
        Candlestick candlestick = new Candlestick();
        candlestick.setOpenTime(((Number) data.get(0)).longValue());
        candlestick.setOpen(new java.math.BigDecimal(data.get(1).toString()));
        candlestick.setHigh(new java.math.BigDecimal(data.get(2).toString()));
        candlestick.setLow(new java.math.BigDecimal(data.get(3).toString()));
        candlestick.setClose(new java.math.BigDecimal(data.get(4).toString()));
        candlestick.setVolume(new java.math.BigDecimal(data.get(5).toString()));
        candlestick.setCloseTime(((Number) data.get(6)).longValue());
        return candlestick;
    }
}
