# Crypto Trading Bot

A simple cryptocurrency trading bot built with Java and Spring Boot that implements a Moving Average Crossover strategy for automated trading on Binance.

## Features

- **Moving Average Crossover Strategy**: Implements a classic technical analysis strategy
- **Real-time Market Data**: Fetches live market data from Binance API
- **Automated Trading**: Executes buy/sell orders based on strategy signals
- **Risk Management**: Configurable trade limits and stop-loss parameters
- **REST API**: Monitor bot status and manually trigger operations
- **Testnet Support**: Safe testing environment using Binance testnet

## Architecture

The bot consists of several key components:

- **ExchangeApiService**: Handles communication with Binance API
- **MarketDataService**: Monitors and updates market data
- **TradingStrategy**: Implements the Moving Average Crossover algorithm
- **TradingService**: Executes trades based on strategy signals
- **TradingController**: REST API for bot management

## Prerequisites

- Java 17 or higher
- Maven 3.6+
- Binance API credentials (testnet or production)

## Getting Started

### 1. Clone the Repository

```bash
git clone <repository-url>
cd crypto-trading-bot
```

### 2. Configure the Bot

Edit `src/main/resources/application.yml` or use environment variables:

```yaml
trading:
  exchange:
    api-key: your-api-key
    api-secret: your-api-secret
    testnet: true  # Use testnet for testing

  strategy:
    enabled: false  # Set to true to enable live trading
    symbol: BTCUSDT
    interval: 1m

  moving-average:
    short-period: 10
    long-period: 30

  risk:
    max-trade-amount: 100.0
```

**Environment Variables** (recommended for production):

```bash
export EXCHANGE_API_KEY="your-api-key"
export EXCHANGE_API_SECRET="your-api-secret"
```

### 3. Build the Project

```bash
mvn clean install
```

### 4. Run the Bot

```bash
mvn spring-boot:run
```

Or run the JAR directly:

```bash
java -jar target/crypto-trading-bot-1.0.0.jar
```

## Configuration

### Trading Strategy

The bot uses a **Moving Average Crossover** strategy:

- **Buy Signal**: When short-term MA crosses above long-term MA (bullish crossover)
- **Sell Signal**: When short-term MA crosses below long-term MA (bearish crossover)
- **Hold**: No action when no crossover is detected

### Risk Management Parameters

```yaml
risk:
  max-trade-amount: 100.0        # Maximum amount per trade in quote currency
  stop-loss-percentage: 2.0      # Stop loss at 2%
  take-profit-percentage: 5.0    # Take profit at 5%
```

### Monitoring Interval

```yaml
monitoring:
  update-interval: 5000  # Update market data every 5 seconds
```

## API Endpoints

The bot exposes REST endpoints for monitoring and control:

### Get Bot Status

```bash
curl http://localhost:8080/api/trading/status
```

Response:
```json
{
  "tradingEnabled": false,
  "symbol": "BTCUSDT",
  "interval": "1m",
  "strategy": "Moving Average Crossover",
  "currentPrice": 50000.00,
  "priceChange24h": 2.5,
  "lastSignal": {
    "type": "BUY",
    "price": 49500.00,
    "timestamp": "2024-01-15T10:30:00"
  }
}
```

### Get Current Ticker

```bash
curl http://localhost:8080/api/trading/ticker
```

### Manually Refresh Market Data

```bash
curl -X POST http://localhost:8080/api/trading/refresh-market-data
```

### Manually Run Trading Cycle

```bash
curl -X POST http://localhost:8080/api/trading/run-cycle
```

## Testing

### Using Binance Testnet

1. Create a testnet account at [Binance Testnet](https://testnet.binance.vision/)
2. Generate API keys
3. Configure the bot to use testnet:

```yaml
trading:
  exchange:
    testnet: true
    base-url: https://testnet.binance.vision
```

### Running Tests

```bash
mvn test
```

## Trading Flow

1. **Market Data Collection**: Bot continuously fetches candlestick data
2. **Strategy Analysis**: Moving averages are calculated from recent data
3. **Signal Generation**: Strategy generates BUY/SELL/HOLD signals
4. **Order Execution**: Valid signals trigger market orders
5. **Risk Validation**: All trades are validated against risk parameters

## Safety Features

- **Testnet Mode**: Test with virtual funds before going live
- **Trading Toggle**: Enable/disable trading without stopping the bot
- **Risk Limits**: Maximum trade amounts and position sizes
- **Duplicate Prevention**: Prevents executing the same signal multiple times
- **Error Handling**: Comprehensive logging and error recovery

## Going Live

⚠️ **Warning**: Trading cryptocurrency involves significant risk. Only use funds you can afford to lose.

To enable live trading:

1. Set up production Binance API credentials
2. Update configuration:
   ```yaml
   trading:
     exchange:
       testnet: false
       base-url: https://api.binance.com
     strategy:
       enabled: true  # Enable live trading
   ```
3. Start with small amounts
4. Monitor the bot closely
5. Implement additional safety measures (stop-loss, take-profit orders)

## Customization

### Adding a New Strategy

1. Implement the `TradingStrategy` interface
2. Add your strategy logic in the `analyze()` method
3. Register as a Spring component
4. Update configuration to select your strategy

Example:
```java
@Component
public class MyCustomStrategy implements TradingStrategy {
    @Override
    public TradeSignal analyze(List<Candlestick> candlesticks) {
        // Your strategy logic here
    }
}
```

### Modifying Trading Intervals

Change the interval in `application.yml`:
- `1m` - 1 minute
- `5m` - 5 minutes
- `15m` - 15 minutes
- `1h` - 1 hour
- `4h` - 4 hours
- `1d` - 1 day

## Monitoring and Logging

Logs are output to the console with the following format:
```
2024-01-15 10:30:00 - BUY SIGNAL: Bullish crossover: Short MA (50100.50) > Long MA (49800.25)
```

Adjust logging levels in `application.yml`:
```yaml
logging:
  level:
    com.trading.bot: DEBUG  # Set to DEBUG for verbose logs
```

## Troubleshooting

### Common Issues

1. **API Authentication Errors**
   - Verify API key and secret are correct
   - Check if IP is whitelisted on Binance
   - Ensure API permissions include trading

2. **Insufficient Data**
   - Wait for enough candlesticks to be collected
   - Reduce moving average periods for faster testing

3. **Order Execution Fails**
   - Check account balance
   - Verify symbol is correct (e.g., BTCUSDT not BTC/USDT)
   - Ensure quantity meets minimum order requirements

## Disclaimer

This bot is provided for educational purposes only. Cryptocurrency trading involves substantial risk of loss. The authors are not responsible for any financial losses incurred while using this software. Always test thoroughly in a testnet environment before using real funds.

## License

MIT License - see LICENSE file for details

## Contributing

Contributions are welcome! Please submit pull requests or open issues for bugs and feature requests.

## Resources

- [Binance API Documentation](https://binance-docs.github.io/apidocs/)
- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Technical Analysis Strategies](https://www.investopedia.com/terms/t/technicalanalysis.asp)
