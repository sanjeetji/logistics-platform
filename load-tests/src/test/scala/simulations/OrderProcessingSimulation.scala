package simulations

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scala.concurrent.duration._

/**
 * Load test simulation for Order Processing
 * Goal: Test 10,000+ orders per hour
 */
class OrderProcessingSimulation extends Simulation {

  // Configuration
  val baseUrl = System.getProperty("baseUrl", "http://localhost:8080")
  val targetOrdersPerHour = System.getProperty("targetOrders", "10000").toInt
  val rampUpDuration = System.getProperty("rampUp", "300").toInt // 5 minutes default
  
  // Calculate users needed to achieve target orders per hour
  // Assuming each user creates 1 order per minute
  val concurrentUsers = (targetOrdersPerHour / 60).toInt

  // HTTP Protocol Configuration
  val httpProtocol = http
    .baseUrl(baseUrl)
    .acceptHeader("application/json")
    .contentTypeHeader("application/json")
    .userAgentHeader("Gatling Load Test")

  // Scenarios
  val scn = scenario("Order Creation Flow")
    .exec(
      http("Authenticate")
        .post("/api/auth/login")
        .body(StringBody("""{"username":"testuser","password":"testpass"}"""))
        .check(status.is(200))
        .check(jsonPath("$.token").saveAs("authToken"))
    )
    .pause(1.second, 3.seconds)
    .exec(
      http("Create B2C Order")
        .post("/api/orders")
        .header("Authorization", "Bearer ${authToken}")
        .body(StringBody("""{
          "customerId": "CUST-${__UUID()}",
          "pickupLocation": {
            "address": "123 Main St",
            "latitude": 40.7128,
            "longitude": -74.0060
          },
          "deliveryLocation": {
            "address": "456 Oak Ave",
            "latitude": 40.7589,
            "longitude": -73.9851
          },
          "packageDetails": {
            "weight": 5.5,
            "dimensions": {
              "length": 30,
              "width": 20,
              "height": 15
            }
          },
          "priority": "STANDARD"
        }"""))
        .check(status.is(201))
        .check(jsonPath("$.orderId").saveAs("orderId"))
    )
    .pause(2.seconds, 5.seconds)
    .exec(
      http("Get Order Status")
        .get("/api/orders/${orderId}")
        .header("Authorization", "Bearer ${authToken}")
        .check(status.is(200))
    )
    .pause(1.second, 2.seconds)
    .exec(
      http("Track Order")
        .get("/api/tracking/${orderId}")
        .header("Authorization", "Bearer ${authToken}")
        .check(status.is(200))
    )

  // Setup simulation
  setUp(
    scn.inject(
      rampUsers(concurrentUsers).during(rampUpDuration.seconds),
      constantUsersPerSec(concurrentUsers / 60.0).during(1.hour)
    ).protocols(httpProtocol)
  ).assertions(
    global.responseTime.max.lt(5000), // Max response time < 5s
    global.responseTime.mean.lt(1000), // Mean response time < 1s
    global.successfulRequests.percent.gt(95) // 95% success rate
  )
}
