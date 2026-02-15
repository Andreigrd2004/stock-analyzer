package com.analyzer.analyzer.prediction;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PredictionRepository extends JpaRepository <Prediction, Integer> {
}
