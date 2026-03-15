package com.analyzer.analyzer.stock;

import com.analyzer.analyzer.stock.DTO.NewsDTO;
import com.analyzer.analyzer.stock.DTO.StockDTO;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/stocks")
@Validated
public class StockController {
    private StockService stockService;

    @GetMapping("/stock-data")
    public StockDTO getStockData(@RequestParam(defaultValue = "AAPL") String symbol) {
        return stockService.getStockData(symbol);
    }

    @GetMapping("/quote")
    public String getQuote(@RequestParam(defaultValue = "AAPL") String symbol) {
        return stockService.getQuote(symbol);
    }

    @GetMapping("/news")
    public List<NewsDTO> getNews(@RequestParam(defaultValue = "AAPL") String symbol) {
        return stockService.getNews(symbol);
    }

    @GetMapping("/insider-sentiment")
    public String getInsiderSentiment(@RequestParam(defaultValue = "AAPL") String symbol) {
        return stockService.getInsiderSentiment(symbol);
    }

    @GetMapping("/prediction")
    public String getPrediction(@RequestParam(defaultValue = "AAPL") String symbol) {
        return stockService.getPrediction(symbol);
    }
}
