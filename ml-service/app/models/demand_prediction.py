"""
Demand Prediction Model
Predicts order demand for regions using Random Forest
"""
import pandas as pd
import numpy as np
from sklearn.ensemble import RandomForestRegressor
from sklearn.model_selection import train_test_split
from sklearn.metrics import r2_score, mean_absolute_error
import joblib
from datetime import datetime, timedelta
import logging
import os

logger = logging.getLogger(__name__)

class DemandPredictor:
    def __init__(self, model_path="trained_models/demand_model.pkl"):
        self.model = None
        self.model_path = model_path
        self.feature_names = ['day_of_week', 'month', 'is_weekend', 'is_holiday', 'day_of_month']
    
    def train(self, historical_data: pd.DataFrame) -> dict:
        """
        Train demand prediction model
        
        Args:
            historical_data: DataFrame with columns [date, region, order_count]
        
        Returns:
            dict with training metrics
        """
        logger.info("Starting demand model training...")
        
        # Feature engineering
        X = self._prepare_features(historical_data)
        y = historical_data['order_count']
        
        # Split data
        X_train, X_test, y_train, y_test = train_test_split(
            X, y, test_size=0.2, random_state=42, shuffle=True
        )
        
        logger.info(f"Training samples: {len(X_train)}, Test samples: {len(X_test)}")
        
        # Train model
        self.model = RandomForestRegressor(
            n_estimators=100,
            max_depth=10,
            min_samples_split=5,
            min_samples_leaf=2,
            random_state=42,
            n_jobs=-1
        )
        
        self.model.fit(X_train, y_train)
        
        # Evaluate
        y_pred = self.model.predict(X_test)
        r2 = r2_score(y_test, y_pred)
        mae = mean_absolute_error(y_test, y_pred)
        
        logger.info(f"Model R² score: {r2:.4f}")
        logger.info(f"Model MAE: {mae:.2f}")
        
        # Save model
        os.makedirs(os.path.dirname(self.model_path), exist_ok=True)
        joblib.dump(self.model, self.model_path)
        logger.info(f"Model saved to {self.model_path}")
        
        return {
            "r2_score": r2,
            "mae": mae,
            "training_samples": len(X_train),
            "test_samples": len(X_test)
        }
    
    def predict(self, region: str, target_date: datetime.date) -> dict:
        """
        Predict demand for a region and date
        
        Args:
            region: Region code
            target_date: Date to predict for
        
        Returns:
            dict with prediction and confidence
        """
        # Load model if not loaded
        if self.model is None:
            if os.path.exists(self.model_path):
                self.model = joblib.load(self.model_path)
                logger.info("Model loaded from disk")
            else:
                # Use mock model for demo
                logger.warning("No trained model found, using mock predictions")
                return self._mock_prediction(region, target_date)
        
        # Prepare features
        features = self._prepare_prediction_features(target_date)
        
        # Predict
        prediction = self.model.predict([features])[0]
        
        # Calculate confidence (simplified - in production use prediction intervals)
        confidence = 0.85
        
        # Feature importance for explanation
        factors = {
            "day_of_week": features[0],
            "month": features[1],
            "is_weekend": bool(features[2]),
            "is_holiday": bool(features[3])
        }
        
        return {
            "predicted_demand": int(max(0, prediction)),  # Ensure non-negative
            "confidence": confidence,
            "factors": factors
        }
    
    def _prepare_features(self, df: pd.DataFrame) -> pd.DataFrame:
        """Feature engineering for training"""
        df = df.copy()
        df['date'] = pd.to_datetime(df['date'])
        df['day_of_week'] = df['date'].dt.dayofweek
        df['month'] = df['date'].dt.month
        df['is_weekend'] = df['day_of_week'].isin([5, 6]).astype(int)
        df['is_holiday'] = 0  # In production, use holiday calendar
        df['day_of_month'] = df['date'].dt.day
        
        return df[self.feature_names]
    
    def _prepare_prediction_features(self, target_date: datetime.date) -> list:
        """Prepare features for a single prediction"""
        day_of_week = target_date.weekday()
        month = target_date.month
        is_weekend = 1 if day_of_week in [5, 6] else 0
        is_holiday = 0  # In production, check holiday calendar
        day_of_month = target_date.day
        
        return [day_of_week, month, is_weekend, is_holiday, day_of_month]
    
    def _mock_prediction(self, region: str, target_date: datetime.date) -> dict:
        """Mock prediction for demo purposes"""
        # Simple rule-based prediction
        base_demand = 100
        day_of_week = target_date.weekday()
        
        # Higher demand on weekends
        if day_of_week in [5, 6]:
            demand = base_demand * 1.5
        else:
            demand = base_demand
        
        # Add some randomness
        demand = int(demand * np.random.uniform(0.8, 1.2))
        
        return {
            "predicted_demand": demand,
            "confidence": 0.70,
            "factors": {
                "day_of_week": day_of_week,
                "is_weekend": day_of_week in [5, 6],
                "note": "Using mock model - train with real data for accurate predictions"
            }
        }
