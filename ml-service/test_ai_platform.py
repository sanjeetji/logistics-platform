import sys
import os

# Ensure the app module can be found
sys.path.insert(0, os.path.abspath(os.path.join(os.path.dirname(__file__), 'app')))

from app.models.ai_forecasting import StrategicForecaster
from app.models.decision_platform import DecisionEngine

def run_tests():
    print("Initializing components...")
    forecaster = StrategicForecaster()
    engine = DecisionEngine()
    
    print("\n--- Testing Strategic Forecaster ---")
    f_res = forecaster.predict_macro_trends("APAC", 45, "B2B")
    print(f_res)
    assert "predicted_volume" in f_res
    assert f_res["trend_direction"] in ["UP", "DOWN", "STABLE"]
    
    print("\n--- Testing Decision Engine ---")
    d_res = engine.generate_recommendations("EMEA", "WAREHOUSE_CAPACITY_LIMIT", "HIGH")
    print(d_res)
    assert len(d_res["actions"]) > 0
    assert d_res["bottleneck_analyzed"] == "WAREHOUSE_CAPACITY_LIMIT"

    print("\nTests passed successfully! 🚀")

if __name__ == "__main__":
    run_tests()
