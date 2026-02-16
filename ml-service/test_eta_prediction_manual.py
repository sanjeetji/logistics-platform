import sys
import os

# Add the project root to the python path
sys.path.append('/Users/sanjeet_kumar/Documents/SpringBoot/Logistic/LogisticsSystem/logistics-platform/ml-service')

from app.models.eta_prediction import ETAPredictionModel

def test_eta_prediction():
    eta_model = ETAPredictionModel()
    
    # Test Case 1: Short distance, Bike, Medium Traffic
    # Distance: 5km. Bike Speed: 30km/h -> 10 mins. Traffic 1.2 -> 12 mins. Buffer 5-10.
    # Total: 17-22 mins
    prediction_1 = eta_model.predict_eta(
        distance_km=5.0,
        vehicle_type="BIKE",
        traffic_condition="MEDIUM"
    )
    print(f"Test 1 (5km, Bike, Medium): {prediction_1}")
    assert prediction_1['estimated_minutes'] >= 15
    assert prediction_1['vehicle_type'] == "BIKE"

    # Test Case 2: Long distance, Car, High Traffic
    # Distance: 20km. Car Speed: 40km/h -> 30 mins. Traffic 1.5 -> 45 mins. Buffer 5-10.
    # Total: 50-55 mins
    prediction_2 = eta_model.predict_eta(
        distance_km=20.0,
        vehicle_type="CAR",
        traffic_condition="HIGH"
    )
    print(f"Test 2 (20km, Car, High): {prediction_2}")
    assert prediction_2['estimated_minutes'] >= 45
    assert prediction_2['traffic_condition'] == "HIGH"

    print("ETA Prediction Logic Verified Successfully!")

if __name__ == "__main__":
    test_eta_prediction()
