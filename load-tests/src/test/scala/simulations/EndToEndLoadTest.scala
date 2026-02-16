package simulations

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scala.concurrent.duration._

/**
 * End-to-end load test simulating complete order lifecycle
 * Tests cross-service integration under load
 * Covers: Customer → Order → Fleet → Payment → Rating
 */
class EndToEndLoadTest extends Simulation {

  // HTTP Configurations for different services
  val orderServiceHttp = http.baseUrl("http://localhost:8081")
  val customerServiceHttp = http.baseUrl("http://localhost:8082")
  val fleetServiceHttp = http.baseUrl("http://localhost:8084")
  val paymentServiceHttp = http.baseUrl("http://localhost:8086")

  val httpProtocol = http
    .acceptHeader("application/json")
    .contentTypeHeader("application/json")
    .userAgentHeader("Gatling E2E Load Test")

  // Test Data
  val customerIds = Iterator.continually(Map("customerId" -> (scala.util.Random.nextInt(1000) + 1)))
  val driverIds = Iterator.continually(Map("driverId" -> (scala.util.Random.nextInt(100) + 1)))

  // Complete Order Lifecycle Scenario
  val completeOrderLifecycleScenario = scenario("Complete Order Lifecycle")
    .feed(customerIds)
    
    // Step 1: Customer creates order
    .exec(
      http("Create Order")
        .post("http://localhost:8081/api/orders")
        .body(StringBody(session => s"""{
          "customerId": ${session("customerId").as[Int]},
          "pickupLocation": {
            "latitude": 28.7041,
            "longitude": 77.1025,
            "address": "Connaught Place, Delhi"
          },
          "dropLocation": {
            "latitude": 28.5355,
            "longitude": 77.3910,
            "address": "Noida Sector 18"
          },
          "packageDetails": {
            "weight": ${scala.util.Random.nextDouble() * 10 + 1},
            "dimensions": {
              "length": ${scala.util.Random.nextInt(50) + 10},
              "width": ${scala.util.Random.nextInt(50) + 10},
              "height": ${scala.util.Random.nextInt(50) + 10}
            },
            "description": "E2E test package"
          },
          "deliveryType": "STANDARD"
        }""")).asJson
        .check(status.is(201))
        .check(jsonPath("$.orderId").saveAs("orderId"))
    )
    .pause(1, 2)
    
    // Step 2: Find nearby drivers
    .exec(
      http("Find Nearby Drivers")
        .get("http://localhost:8084/api/fleet/search/nearby?latitude=28.7041&longitude=77.1025&radiusKm=5")
        .check(status.is(200))
        .check(jsonPath("$[0].id").optional.saveAs("nearbyDriverId"))
    )
    .pause(500.milliseconds, 1.second)
    
    // Step 3: Assign order to driver (if driver found)
    .doIf(session => session.contains("nearbyDriverId")) {
      exec(
        http("Assign Order to Driver")
          .put("http://localhost:8081/api/orders/${orderId}/assign")
          .body(StringBody("""{"driverId": ${nearbyDriverId}}""")).asJson
          .check(status.in(200, 204))
      )
    }
    .pause(1, 2)
    
    // Step 4: Update order status to PICKED_UP
    .exec(
      http("Update to Picked Up")
        .put("http://localhost:8081/api/orders/${orderId}/status")
        .body(StringBody("""{"status": "PICKED_UP"}""")).asJson
        .check(status.in(200, 204))
    )
    .pause(2, 4)
    
    // Step 5: Update order status to DELIVERED
    .exec(
      http("Update to Delivered")
        .put("http://localhost:8081/api/orders/${orderId}/status")
        .body(StringBody("""{"status": "DELIVERED"}""")).asJson
        .check(status.in(200, 204))
    )
    .pause(1, 2)
    
    // Step 6: Process payment
    .exec(
      http("Process Payment")
        .post("http://localhost:8086/api/payments")
        .body(StringBody(session => s"""{
          "orderId": "${session("orderId").as[String]}",
          "amount": ${scala.util.Random.nextDouble() * 500 + 100},
          "currency": "INR",
          "paymentMethod": "CARD",
          "gateway": "RAZORPAY"
        }""")).asJson
        .check(status.is(201))
        .check(jsonPath("$.paymentId").saveAs("paymentId"))
    )
    .pause(1, 2)
    
    // Step 7: Submit rating (if driver was assigned)
    .doIf(session => session.contains("nearbyDriverId")) {
      exec(
        http("Submit Rating")
          .post("http://localhost:8087/api/ratings")
          .body(StringBody(session => s"""{
            "orderId": "${session("orderId").as[String]}",
            "driverId": ${session("nearbyDriverId").as[String]},
            "customerId": ${session("customerId").as[Int]},
            "rating": ${scala.util.Random.nextInt(3) + 3},
            "comment": "E2E test rating",
            "categories": ["PUNCTUALITY", "PROFESSIONALISM"]
          }""")).asJson
          .check(status.is(201))
      )
    }
    .pause(2, 5)

  // Concurrent Order Creation Scenario (Stress Test)
  val concurrentOrdersScenario = scenario("Concurrent Order Creation")
    .feed(customerIds)
    .exec(
      http("Create Order")
        .post("http://localhost:8081/api/orders")
        .body(StringBody(session => s"""{
          "customerId": ${session("customerId").as[Int]},
          "pickupLocation": {
            "latitude": ${28.5 + scala.util.Random.nextDouble() * 0.5},
            "longitude": ${77.0 + scala.util.Random.nextDouble() * 0.5},
            "address": "Test Location"
          },
          "dropLocation": {
            "latitude": ${28.5 + scala.util.Random.nextDouble() * 0.5},
            "longitude": ${77.0 + scala.util.Random.nextDouble() * 0.5},
            "address": "Test Destination"
          },
          "packageDetails": {
            "weight": 5.0,
            "description": "Concurrent test"
          },
          "deliveryType": "EXPRESS"
        }""")).asJson
        .check(status.is(201))
    )
    .pause(1, 3)

  // Load Profile: Simulate realistic user behavior
  setUp(
    completeOrderLifecycleScenario.inject(
      rampUsers(50).during(3.minutes),
      constantUsersPerSec(20).during(7.minutes)
    ),
    concurrentOrdersScenario.inject(
      nothingFor(2.minutes),
      rampUsers(100).during(2.minutes),
      constantUsersPerSec(50).during(5.minutes)
    )
  ).protocols(httpProtocol)
    .assertions(
      global.responseTime.percentile3.lt(1000), // p95 < 1000ms (more lenient for E2E)
      global.responseTime.percentile4.lt(2000), // p99 < 2000ms
      global.successfulRequests.percent.gt(95) // > 95% success rate (accounting for dependencies)
    )
}
