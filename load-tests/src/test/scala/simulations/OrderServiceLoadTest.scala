package simulations

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scala.concurrent.duration._

/**
 * Comprehensive load test for Order Service
 * Tests order creation, retrieval, and status updates under load
 * Target: 1000+ orders per hour with p95 < 500ms
 */
class OrderServiceLoadTest extends Simulation {

  // HTTP Configuration
  val httpProtocol = http
    .baseUrl("http://localhost:8081") // order-service port
    .acceptHeader("application/json")
    .contentTypeHeader("application/json")
    .userAgentHeader("Gatling Load Test")

  // Test Data Feeders
  val customerIds = Iterator.continually(Map("customerId" -> scala.util.Random.nextInt(1000)))
  val pickupLocations = Array(
    """{"latitude": 28.7041, "longitude": 77.1025, "address": "Connaught Place, Delhi"}""",
    """{"latitude": 19.0760, "longitude": 72.8777, "address": "Mumbai Central"}""",
    """{"latitude": 12.9716, "longitude": 77.5946, "address": "MG Road, Bangalore"}"""
  )
  val dropLocations = Array(
    """{"latitude": 28.5355, "longitude": 77.3910, "address": "Noida Sector 18"}""",
    """{"latitude": 19.2183, "longitude": 72.9781, "address": "Andheri, Mumbai"}""",
    """{"latitude": 13.0827, "longitude": 80.2707, "address": "Chennai Central"}"""
  )

  // Scenario 1: Create Orders
  val createOrderScenario = scenario("Create Orders")
    .feed(customerIds)
    .exec(
      http("Create Order")
        .post("/api/orders")
        .body(StringBody(session => s"""{
          "customerId": ${session("customerId").as[Int]},
          "pickupLocation": ${pickupLocations(scala.util.Random.nextInt(pickupLocations.length))},
          "dropLocation": ${dropLocations(scala.util.Random.nextInt(dropLocations.length))},
          "packageDetails": {
            "weight": ${scala.util.Random.nextDouble() * 10 + 1},
            "dimensions": {
              "length": ${scala.util.Random.nextInt(50) + 10},
              "width": ${scala.util.Random.nextInt(50) + 10},
              "height": ${scala.util.Random.nextInt(50) + 10}
            },
            "description": "Load test package"
          },
          "deliveryType": "STANDARD"
        }""")).asJson
        .check(status.is(201))
        .check(jsonPath("$.orderId").saveAs("orderId"))
    )
    .pause(1, 3)

  // Scenario 2: Retrieve Orders (Test Caching)
  val retrieveOrderScenario = scenario("Retrieve Orders")
    .exec(
      http("Get Order by ID")
        .get("/api/orders/${orderId}")
        .check(status.is(200))
        .check(jsonPath("$.orderId").exists)
    )
    .pause(500.milliseconds, 2.seconds)

  // Scenario 3: Update Order Status
  val updateOrderScenario = scenario("Update Order Status")
    .exec(
      http("Update Order Status")
        .put("/api/orders/${orderId}/status")
        .body(StringBody("""{"status": "ASSIGNED"}""")).asJson
        .check(status.in(200, 204))
    )
    .pause(1, 2)

  // Scenario 4: List Orders (Pagination Test)
  val listOrdersScenario = scenario("List Orders")
    .exec(
      http("List Orders")
        .get("/api/orders?page=0&size=20")
        .check(status.is(200))
        .check(jsonPath("$.content").exists)
    )
    .pause(2, 5)

  // Load Profile: Ramp up to 500 concurrent users over 5 minutes
  setUp(
    createOrderScenario.inject(
      rampUsers(100).during(2.minutes),
      constantUsersPerSec(50).during(5.minutes),
      rampUsers(200).during(3.minutes)
    ),
    retrieveOrderScenario.inject(
      nothingFor(1.minute),
      rampUsers(200).during(3.minutes),
      constantUsersPerSec(100).during(5.minutes)
    ),
    updateOrderScenario.inject(
      nothingFor(2.minutes),
      rampUsers(50).during(2.minutes),
      constantUsersPerSec(25).during(4.minutes)
    ),
    listOrdersScenario.inject(
      nothingFor(1.minute),
      rampUsers(50).during(2.minutes),
      constantUsersPerSec(20).during(5.minutes)
    )
  ).protocols(httpProtocol)
    .assertions(
      global.responseTime.percentile3.lt(500), // p95 < 500ms
      global.responseTime.percentile4.lt(1000), // p99 < 1000ms
      global.successfulRequests.percent.gt(99.9) // > 99.9% success rate
    )
}
