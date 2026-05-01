package com.analyzer.analyzer.prediction;

import com.analyzer.analyzer.stock.Stock;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PredictionRepository extends JpaRepository <Prediction, Integer> {
    boolean existsPredictionByStock(Stock stock);

    String getPredictionByStock(Stock stock);
}
