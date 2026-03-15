package com.analyzer.analyzer.stock;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.analyzer.analyzer.prediction.gemini.GeminiPredictionService;
import com.analyzer.analyzer.price_variation.PriceVariationService;
import com.analyzer.analyzer.stock.DTO.InsiderSentimentResponseDTO;
import com.analyzer.analyzer.stock.DTO.InsiderSentimentDTO;
import com.analyzer.analyzer.stock.DTO.NewsDTO;
import com.analyzer.analyzer.stock.DTO.StockDTO;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class StockServiceImpl implements StockService {
    private final StockRepository stockRepository;
    private final PriceVariationService priceVariationService;
    private final GeminiPredictionService geminiPredictionService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${app.finnhub-api-key}")
    private String apiKey;

    private static final String BASE_URL = "https://finnhub.io/api/v1/";

    public StockDTO getStockData(String stockSymbol) {
        String url = BASE_URL + "quote?symbol=" + stockSymbol + "&token=" + apiKey;
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.getForObject(url, StockDTO.class);
    }

    public String getPrediction(String stockSymbol) {
        try {
            String quote = getQuote(stockSymbol);
            String news = objectMapper.writeValueAsString(getNews(stockSymbol));
            String sentiment = getInsiderSentiment(stockSymbol);
            String priceChange = getPriceVariation(stockSymbol);
            return geminiPredictionService.generateStockAnalysis(stockSymbol, quote, news, sentiment, priceChange);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize stock data to JSON", e);
        }
    }

    public String getQuote(String stockSymbol) {
        String url = BASE_URL + "quote?" + "symbol=" + stockSymbol + "&token=" + apiKey;
        RestTemplate restTemplate = new RestTemplate();

        @SuppressWarnings("unchecked")
        Map<String, Object> response = restTemplate.getForObject(url, Map.class);
        assert response != null;
        return response.get("c").toString();
    }

    public List<NewsDTO> getNews(String stockSymbol) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime oneWeekAgo = now.minusDays(1);
        String date = "&from=" + oneWeekAgo.format(formatter) + "&to=" + now.format(formatter);
        String url = BASE_URL + "company-news?symbol=" + stockSymbol + date + "&token=" + apiKey;
        RestTemplate restTemplate = new RestTemplate();

        NewsDTO[] response = restTemplate.getForObject(url, NewsDTO[].class);
        return response != null ? List.of(response) : List.of();
    }

    public String getInsiderSentiment(String stockSymbol) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime oneYearAhead = now.plusMonths(2);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String to = oneYearAhead.format(formatter);
        String from = now.format(formatter);

        String url = BASE_URL + "stock/insider-sentiment?symbol=" + stockSymbol
                + "&from=" + from
                + "&to=" + to
                + "&token=" + apiKey;

        RestTemplate restTemplate = new RestTemplate();
        InsiderSentimentResponseDTO response = restTemplate.getForObject(url, InsiderSentimentResponseDTO.class);

        return summarizeInsiderSentiment(response);
    }

    private String summarizeInsiderSentiment(InsiderSentimentResponseDTO dto) {
        if (dto == null || dto.getData() == null || dto.getData().isEmpty()) {
            return "No insider sentiment data available.";
        }

        List<InsiderSentimentDTO> data = dto.getData();

        double avgMspr = data.stream()
                .mapToDouble(InsiderSentimentDTO::getMspr)
                .average()
                .orElse(0);

        InsiderSentimentDTO latest = data.getLast();
        InsiderSentimentDTO oldest = data.getFirst();
        double trend = latest.getMspr() - oldest.getMspr();

        return String.format(
                "Average MSPR: %.2f | Latest MSPR: %.2f (%d/%d) | Trend: %s | Data points: %d",
                avgMspr,
                latest.getMspr(),
                latest.getMonth(),
                latest.getYear(),
                trend > 0 ? "Improving" : trend < 0 ? "Deteriorating" : "Stable",
                data.size()
        );
    }
    private String getPriceVariation(String stockSymbol) {
        return priceVariationService.getPriceChange(stockSymbol);
    }

}
