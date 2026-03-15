package com.analyzer.analyzer.stock;

import com.analyzer.analyzer.stock.DTO.NewsDTO;
import com.analyzer.analyzer.stock.DTO.StockDTO;

import java.util.List;

public interface StockService {
    StockDTO getStockData(String stockSymbol);
    String getQuote(String stockSymbol);
    List<NewsDTO> getNews(String stockSymbol);
    String getInsiderSentiment(String stockSymbol);
    String getPrediction(String stockSymbol);
}
