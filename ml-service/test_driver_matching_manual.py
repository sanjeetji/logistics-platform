import sys
import os

# Add the project root to the python path
sys.path.append('/Users/sanjeet_kumar/Documents/SpringBoot/Logistic/LogisticsSystem/logistics-platform/ml-service')

from app.models.driver_matching import DriverMatchingModel

def test_driver_matching():
    matcher = DriverMatchingModel()
    
    order_details = {
        "order_id": "ORDER-123",
        "pickup_lat": 28.7041,
        "pickup_lng": 77.1025,
        "required_vehicle": "VAN"
    }
    
    candidates = [
        {
            "driver_id": "D1",
            "current_lat": 28.7050, # Very close
            "current_lng": 77.1030,
            "vehicle_type": "VAN",
            "rating": 4.9,
            "acceptance_rate": 0.95
        },
        {
            "driver_id": "D2",
            "current_lat": 28.7500, # Further away
            "current_lng": 77.1500,
            "vehicle_type": "VAN",
            "rating": 4.5,
            "acceptance_rate": 0.80
        },
        {
            "driver_id": "D3",
            "current_lat": 28.7050,
            "current_lng": 77.1030,
            "vehicle_type": "BIKE", # Wrong vehicle
            "rating": 5.0,
            "acceptance_rate": 1.0
        }
    ]
    
    ranked = matcher.score_drivers(order_details, candidates)
    
    print(f"Ranked Drivers: {ranked}")
    
    assert len(ranked) == 3
    assert ranked[0]['driver_id'] == "D1" # Closest and good rating and correct vehicle
    assert ranked[2]['score'] == 0.0 # Wrong vehicle should be 0 or very low (logic dependent)
    
    print("Test Passed!")

if __name__ == "__main__":
    test_driver_matching()
