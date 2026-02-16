package simulations

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scala.concurrent.duration._

/**
 * Load test for Fleet Service with geospatial operations
 * Tests driver location updates and spatial queries
 * Target: 1000+ drivers streaming location, p95 < 400ms
 */
class FleetServiceLoadTest extends Simulation {

  // HTTP Configuration
  val httpProtocol = http
    .baseUrl("http://localhost:8084") // fleet-service port
    .acceptHeader("application/json")
    .contentTypeHeader("application/json")
    .userAgentHeader("Gatling Load Test")

  // Test Data
  val driverIds = (1 to 1000).iterator.map(i => Map("driverId" -> i))
  val locations = Array(
    (28.7041, 77.1025), // Delhi
    (19.0760, 72.8777), // Mumbai
    (12.9716, 77.5946), // Bangalore
    (13.0827, 80.2707), // Chennai
    (22.5726, 88.3639)  // Kolkata
  )

  // Scenario 1: Update Driver Location (Streaming Simulation)
  val updateLocationScenario = scenario("Update Driver Location")
    .feed(driverIds)
    .repeat(20) { // Simulate 20 location updates per driver
      exec(session => {
        val baseLocation = locations(scala.util.Random.nextInt(locations.length))
        val lat = baseLocation._1 + (scala.util.Random.nextDouble() - 0.5) * 0.1
        val lon = baseLocation._2 + (scala.util.Random.nextDouble() - 0.5) * 0.1
        session.set("latitude", lat).set("longitude", lon)
      })
      .exec(
        http("Update Location")
          .post("/api/fleet/drivers/${driverId}/location")
          .body(StringBody(session => s"""{
            "latitude": ${session("latitude").as[Double]},
            "longitude": ${session("longitude").as[Double]},
            "accuracy": ${scala.util.Random.nextInt(20) + 5},
            "speed": ${scala.util.Random.nextInt(60)},
            "bearing": ${scala.util.Random.nextInt(360)}
          }""")).asJson
          .check(status.in(200, 204))
      )
      .pause(2.seconds, 5.seconds) // Simulate real-time updates
    }

  // Scenario 2: Find Drivers Near Location (Geospatial Query)
  val findNearbyDriversScenario = scenario("Find Nearby Drivers")
    .exec(session => {
      val location = locations(scala.util.Random.nextInt(locations.length))
      session.set("searchLat", location._1).set("searchLon", location._2)
    })
    .exec(
      http("Find Drivers Near Me")
        .get("/api/fleet/search/nearby?latitude=${searchLat}&longitude=${searchLon}&radiusKm=5")
        .check(status.is(200))
        .check(jsonPath("$").exists)
        .check(responseTimeInMillis.lt(400))
    )
    .pause(3, 7)

  // Scenario 3: Get Driver Status
  val getDriverStatusScenario = scenario("Get Driver Status")
    .feed(driverIds)
    .exec(
      http("Get Driver Status")
        .get("/api/fleet/drivers/${driverId}")
        .check(status.is(200))
        .check(jsonPath("$.id").is("${driverId}"))
    )
    .pause(2, 5)

  // Scenario 4: Update Driver Status
  val updateDriverStatusScenario = scenario("Update Driver Status")
    .feed(driverIds)
    .exec(
      http("Update Driver Status")
        .put("/api/fleet/drivers/${driverId}/status")
        .body(StringBody("""{"status": "AVAILABLE"}""")).asJson
        .check(status.in(200, 204))
    )
    .pause(5, 10)

  // Scenario 5: Geofencing Query (Polygon Search)
  val geofencingScenario = scenario("Geofencing Query")
    .exec(
      http("Drivers in Area")
        .post("/api/fleet/search/in-area")
        .body(StringBody(s"""{
          "polygon": [
            {"latitude": 28.6, "longitude": 77.0},
            {"latitude": 28.8, "longitude": 77.0},
            {"latitude": 28.8, "longitude": 77.2},
            {"latitude": 28.6, "longitude": 77.2},
            {"latitude": 28.6, "longitude": 77.0}
          ]
        }""")).asJson
        .check(status.is(200))
        .check(jsonPath("$").exists)
    )
    .pause(5, 10)

  // Load Profile: Simulate 1000 drivers with continuous location updates
  setUp(
    updateLocationScenario.inject(
      rampUsers(1000).during(3.minutes),
      constantUsersPerSec(50).during(5.minutes)
    ),
    findNearbyDriversScenario.inject(
      nothingFor(1.minute),
      rampUsers(100).during(2.minutes),
      constantUsersPerSec(50).during(5.minutes)
    ),
    getDriverStatusScenario.inject(
      nothingFor(30.seconds),
      rampUsers(50).during(2.minutes),
      constantUsersPerSec(25).during(5.minutes)
    ),
    updateDriverStatusScenario.inject(
      nothingFor(2.minutes),
      rampUsers(30).during(2.minutes),
      constantUsersPerSec(10).during(4.minutes)
    ),
    geofencingScenario.inject(
      nothingFor(2.minutes),
      rampUsers(20).during(2.minutes),
      constantUsersPerSec(5).during(4.minutes)
    )
  ).protocols(httpProtocol)
    .assertions(
      global.responseTime.percentile3.lt(400), // p95 < 400ms
      global.responseTime.percentile4.lt(800), // p99 < 800ms
      global.successfulRequests.percent.gt(99.5) // > 99.5% success rate
    )
}
