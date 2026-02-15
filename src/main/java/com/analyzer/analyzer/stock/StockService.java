package com.analyzer.analyzer.stock;

import com.analyzer.analyzer.stock.DTO.StockDTO;

public interface StockService {
    public StockDTO getStockData(String stockSymbol);
}
