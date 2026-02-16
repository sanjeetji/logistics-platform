import sys
import os

# Add the project root to the python path
sys.path.append('/Users/sanjeet_kumar/Documents/SpringBoot/Logistic/LogisticsSystem/logistics-platform/ml-service')

from app.models.dynamic_pricing import DynamicPricingModel

def test_dynamic_pricing():
    pricing_model = DynamicPricingModel()
    
    # Test Case 1: Low Demand, Morning (Should be base price or close to it)
    price_1 = pricing_model.calculate_price(
        region="NORTH",
        distance_km=10.0,
        vehicle_type="CAR", # Base rate 8.0 -> 80.0
        time_of_day="MORNING", # Surge 1.1
        current_demand=30 # Low demand -> Surge 1.0
    )
    # Expected: 80 * 1.0 * 1.1 = 88.0
    print(f"Test 1 (Low Demand, Morning): {price_1}")
    assert price_1['final_price'] == 88.0
    assert price_1['surge_multiplier'] == 1.1

    # Test Case 2: High Demand, Evening (Should have high surge)
    price_2 = pricing_model.calculate_price(
        region="SOUTH",
        distance_km=10.0,
        vehicle_type="BIKE", # Base rate 5.0 -> 50.0
        time_of_day="EVENING", # Surge 1.2
        current_demand=120 # High demand (100-150) -> Surge 1.5
    )
    # Expected: 50 * 1.5 * 1.2 = 50 * 1.8 = 90.0
    print(f"Test 2 (High Demand, Evening): {price_2}")
    assert price_2['final_price'] == 90.0
    assert price_2['surge_multiplier'] == 1.8

    # Test Case 3: Very High Demand (Cap check)
    price_3 = pricing_model.calculate_price(
        region="EAST",
        distance_km=5.0,
        vehicle_type="VAN",
        time_of_day="EVENING", # 1.2
        current_demand=200 # Very High Demand (>150) -> Surge 2.0
    )
    # Expected Surge: 2.0 * 1.2 = 2.4
    print(f"Test 3 (Very High Demand): {price_3}")
    assert price_3['surge_multiplier'] == 2.4

    print("Dynamic Pricing Logic Verified Successfully!")

if __name__ == "__main__":
    test_dynamic_pricing()
