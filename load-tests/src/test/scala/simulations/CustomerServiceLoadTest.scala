package simulations

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scala.concurrent.duration._

/**
 * Load test for Customer Service with Redis caching validation
 * Tests customer profile operations and validates cache performance
 * Target: Cache hit ratio > 80%, p95 < 300ms
 */
class CustomerServiceLoadTest extends Simulation {

  // HTTP Configuration
  val httpProtocol = http
    .baseUrl("http://localhost:8082") // customer-service port
    .acceptHeader("application/json")
    .contentTypeHeader("application/json")
    .userAgentHeader("Gatling Load Test")

  // Test Data
  val customerIds = (1 to 1000).iterator.map(i => Map("customerId" -> i))
  val userIds = (1 to 1000).iterator.map(i => Map("userId" -> i))

  // Scenario 1: Get Customer by ID (Cache Test)
  val getCustomerByIdScenario = scenario("Get Customer by ID")
    .feed(customerIds)
    .exec(
      http("Get Customer by ID - First Call")
        .get("/api/customers/${customerId}")
        .check(status.is(200))
        .check(jsonPath("$.id").exists)
        .check(responseTimeInMillis.lt(300))
    )
    .pause(100.milliseconds)
    .exec(
      http("Get Customer by ID - Cached Call")
        .get("/api/customers/${customerId}")
        .check(status.is(200))
        .check(responseTimeInMillis.lt(50)) // Should be much faster from cache
    )
    .pause(1, 3)

  // Scenario 2: Get Customer by User ID (Cache Test)
  val getCustomerByUserIdScenario = scenario("Get Customer by User ID")
    .feed(userIds)
    .exec(
      http("Get Customer by User ID")
        .get("/api/customers/user/${userId}")
        .check(status.is(200))
        .check(jsonPath("$.userId").is("${userId}"))
    )
    .pause(500.milliseconds, 2.seconds)

  // Scenario 3: Get Customer Addresses (Cache Test)
  val getAddressesScenario = scenario("Get Customer Addresses")
    .feed(customerIds)
    .repeat(3) {
      exec(
        http("Get Addresses")
          .get("/api/customers/${customerId}/addresses")
          .check(status.is(200))
          .check(jsonPath("$").exists)
      )
      .pause(200.milliseconds)
    }
    .pause(2, 4)

  // Scenario 4: Update Customer Profile (Cache Eviction Test)
  val updateProfileScenario = scenario("Update Customer Profile")
    .feed(customerIds)
    .exec(
      http("Update Profile")
        .put("/api/customers/${customerId}/profile")
        .body(StringBody(s"""{
          "name": "Updated Customer ${scala.util.Random.nextInt(1000)}",
          "email": "customer${scala.util.Random.nextInt(1000)}@test.com",
          "phoneNumber": "+91${scala.util.Random.nextInt(900000000) + 1000000000}"
        }""")).asJson
        .check(status.in(200, 204))
    )
    .pause(1, 2)
    .exec(
      http("Get Updated Profile - Cache Miss")
        .get("/api/customers/${customerId}")
        .check(status.is(200))
        .check(jsonPath("$.name").exists)
    )
    .pause(2, 5)

  // Scenario 5: Add Address (Cache Eviction Test)
  val addAddressScenario = scenario("Add Customer Address")
    .feed(customerIds)
    .exec(
      http("Add Address")
        .post("/api/customers/${customerId}/addresses")
        .body(StringBody(s"""{
          "label": "Home",
          "address": "Test Address ${scala.util.Random.nextInt(1000)}",
          "city": "Delhi",
          "state": "Delhi",
          "pincode": "110001",
          "latitude": 28.7041,
          "longitude": 77.1025,
          "isDefault": false
        }""")).asJson
        .check(status.is(201))
    )
    .pause(500.milliseconds)
    .exec(
      http("Get Addresses After Add - Cache Miss")
        .get("/api/customers/${customerId}/addresses")
        .check(status.is(200))
    )
    .pause(2, 4)

  // Load Profile: Focus on read-heavy workload to test caching
  setUp(
    getCustomerByIdScenario.inject(
      rampUsers(200).during(2.minutes),
      constantUsersPerSec(100).during(5.minutes)
    ),
    getCustomerByUserIdScenario.inject(
      nothingFor(30.seconds),
      rampUsers(150).during(2.minutes),
      constantUsersPerSec(75).during(5.minutes)
    ),
    getAddressesScenario.inject(
      nothingFor(1.minute),
      rampUsers(100).during(2.minutes),
      constantUsersPerSec(50).during(5.minutes)
    ),
    updateProfileScenario.inject(
      nothingFor(2.minutes),
      rampUsers(30).during(2.minutes),
      constantUsersPerSec(10).during(4.minutes)
    ),
    addAddressScenario.inject(
      nothingFor(2.minutes),
      rampUsers(20).during(2.minutes),
      constantUsersPerSec(5).during(4.minutes)
    )
  ).protocols(httpProtocol)
    .assertions(
      global.responseTime.percentile3.lt(300), // p95 < 300ms (faster due to caching)
      global.responseTime.percentile4.lt(500), // p99 < 500ms
      global.successfulRequests.percent.gt(99.5) // > 99.5% success rate
    )
}
