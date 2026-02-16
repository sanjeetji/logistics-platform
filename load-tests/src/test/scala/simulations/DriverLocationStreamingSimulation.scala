package simulations

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scala.concurrent.duration._
import scala.util.Random

/**
 * Load test simulation for Driver Location Streaming
 * Goal: Test 1000+ drivers streaming location data
 */
class DriverLocationStreamingSimulation extends Simulation {

  // Configuration
  val baseUrl = System.getProperty("baseUrl", "http://localhost:8083")
  val targetDrivers = System.getProperty("targetDrivers", "1000").toInt
  val rampUpDuration = System.getProperty("rampUp", "120").toInt // 2 minutes default
  val testDuration = System.getProperty("duration", "600").toInt // 10 minutes default

  // HTTP Protocol Configuration
  val httpProtocol = http
    .baseUrl(baseUrl)
    .acceptHeader("application/json")
    .contentTypeHeader("application/json")

  // Helper to generate realistic location updates
  val locationFeeder = Iterator.continually(Map(
    "driverId" -> s"DRIVER-${Random.nextInt(targetDrivers)}",
    "latitude" -> (40.7128 + Random.nextDouble() * 0.1), // NYC area
    "longitude" -> (-74.0060 + Random.nextDouble() * 0.1),
    "speed" -> Random.nextInt(60),
    "heading" -> Random.nextInt(360),
    "accuracy" -> (5 + Random.nextInt(15))
  ))

  // Scenario for continuous location streaming
  val locationStreamingScenario = scenario("Driver Location Streaming")
    .feed(locationFeeder)
    .exec(
      http("Authenticate Driver")
        .post("/api/driver/auth")
        .body(StringBody("""{"driverId":"${driverId}","deviceId":"DEVICE-${driverId}"}"""))
        .check(status.is(200))
        .check(jsonPath("$.token").saveAs("driverToken"))
    )
    .pause(1.second)
    .during(testDuration.seconds) {
      feed(locationFeeder)
        .exec(
          http("Send Location Update")
            .post("/api/fleet/location")
            .header("Authorization", "Bearer ${driverToken}")
            .body(StringBody("""{
              "driverId": "${driverId}",
              "latitude": ${latitude},
              "longitude": ${longitude},
              "speed": ${speed},
              "heading": ${heading},
              "accuracy": ${accuracy},
              "timestamp": "${__time()}"
            }"""))
            .check(status.is(200))
        )
        .pause(5.seconds) // Send location every 5 seconds
    }

  // Scenario for driver status updates
  val statusUpdateScenario = scenario("Driver Status Updates")
    .feed(locationFeeder)
    .exec(
      http("Authenticate Driver")
        .post("/api/driver/auth")
        .body(StringBody("""{"driverId":"${driverId}","deviceId":"DEVICE-${driverId}"}"""))
        .check(status.is(200))
        .check(jsonPath("$.token").saveAs("driverToken"))
    )
    .pause(2.seconds)
    .exec(
      http("Update Status to Available")
        .put("/api/fleet/${driverId}/status")
        .header("Authorization", "Bearer ${driverToken}")
        .body(StringBody("""{"status":"AVAILABLE"}"""))
        .check(status.is(200))
    )
    .pause(10.seconds)
    .during((testDuration - 30).seconds) {
      feed(locationFeeder)
        .exec(
          http("Send Location Update")
            .post("/api/fleet/location")
            .header("Authorization", "Bearer ${driverToken}")
            .body(StringBody("""{
              "driverId": "${driverId}",
              "latitude": ${latitude},
              "longitude": ${longitude},
              "speed": ${speed},
              "heading": ${heading},
              "accuracy": ${accuracy},
              "timestamp": "${__time()}"
            }"""))
            .check(status.is(200))
        )
        .pause(5.seconds)
    }
    .exec(
      http("Update Status to Offline")
        .put("/api/fleet/${driverId}/status")
        .header("Authorization", "Bearer ${driverToken}")
        .body(StringBody("""{"status":"OFFLINE"}"""))
        .check(status.is(200))
    )

  // Setup simulation
  setUp(
    locationStreamingScenario.inject(
      rampUsers(targetDrivers).during(rampUpDuration.seconds)
    ).protocols(httpProtocol)
  ).assertions(
    global.responseTime.max.lt(2000), // Max response time < 2s
    global.responseTime.mean.lt(500), // Mean response time < 500ms
    global.successfulRequests.percent.gt(99) // 99% success rate
  )
}
