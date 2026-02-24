package simulations

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scala.concurrent.duration._

/**
 * Comprehensive load test combining all scenarios
 * Tests the entire platform under realistic load
 */
class ComprehensiveLoadSimulation extends Simulation {

  // Configuration
  val baseUrl = System.getProperty("baseUrl", "http://localhost:8080")
  val fleetUrl = System.getProperty("fleetUrl", "http://localhost:8083")
  val wsUrl = System.getProperty("wsUrl", "ws://localhost:8085")

  // Load parameters
  val ordersPerHour = System.getProperty("ordersPerHour", "5000").toInt
  val activeDrivers = System.getProperty("activeDrivers", "500").toInt
  val wsConnections = System.getProperty("wsConnections", "500").toInt
  val testDuration = System.getProperty("duration", "1800").toInt // 30 minutes

  // HTTP Protocols
  val httpProtocol = http
    .baseUrl(baseUrl)
    .acceptHeader("application/json")
    .contentTypeHeader("application/json")

  val fleetProtocol = http
    .baseUrl(fleetUrl)
    .acceptHeader("application/json")
    .contentTypeHeader("application/json")

  val wsProtocol = http
    .wsBaseUrl(wsUrl)

  // Data feeders
  val customerFeeder = Iterator.continually(Map(
    "customerId" -> s"CUST-${scala.util.Random.nextInt(10000)}",
    "pickupLat" -> (40.7128 + scala.util.Random.nextDouble() * 0.1),
    "pickupLon" -> (-74.0060 + scala.util.Random.nextDouble() * 0.1), 
    "deliveryLat" -> (40.7589 + scala.util.Random.nextDouble() * 0.1),
    "deliveryLon" -> (-73.9851 + scala.util.Random.nextDouble() * 0.1)
  ))

  // Scenarios
  val orderCreationScenario = scenario("Order Creation")
    .feed(customerFeeder)
    .exec(
      http("Create Order")
        .post("/api/orders")
        .body(StringBody("""{
          "customerId": "${customerId}",
          "pickupLocation": {
            "latitude": ${pickupLat},
            "longitude": ${pickupLon}
          },
          "deliveryLocation": {
            "latitude": ${deliveryLat},
            "longitude": ${deliveryLon}
          },
          "packageDetails": {
            "weight": 5.0,
            "dimensions": {"length": 30, "width": 20, "height": 15}
          }
        }"""))
        .check(status.is(201))
        .check(jsonPath("$.orderId").saveAs("orderId"))
    )
    .pause(60.seconds)

  val driverLocationScenario = scenario("Driver Location Updates")
    .exec(
      http("Send Location")
        .post("/api/fleet/location")
        .body(StringBody("""{
          "driverId": "DRIVER-${__Random(1,${activeDrivers})}",
          "latitude": ${40.7128 + __Random(-100,100)/1000.0},
          "longitude": ${-74.0060 + __Random(-100,100)/1000.0},
          "timestamp": "${__time()}"
        }"""))
        .check(status.is(200))
    )
    .pause(5.seconds)

  val trackingScenario = scenario("Order Tracking")
    .exec(
      ws("Connect to Tracking").connect("/tracking")
        .await(5.seconds)(ws.checkTextMessage("connected"))
    )
    .during(testDuration.seconds) {
      pause(10.seconds)
    }
    .exec(ws("Close").close)

  val aiForecastingScenario = scenario("Strategic ML Forecasting")
    .exec(
      http("Predict Macro Trends")
        .post("/predict/strategic-forecast")
        .body(StringBody("""{
          "target_horizon_days": 30,
          "region": "APAC",
          "business_vertical": "B2B"
        }"""))
        .check(status.is(200))
    )
    .pause(10.seconds)
    .exec(
      http("Prescriptive Action Analytics")
        .post("/decisions/recommendations")
        .body(StringBody("""{
          "region": "NORTH_AMERICA",
          "current_bottleneck": "DRIVER_SHORTAGE",
          "urgency_level": "CRITICAL"
        }"""))
        .check(status.is(200))
    )

  // Setup - run all scenarios concurrently
  setUp(
    orderCreationScenario.inject(
      constantUsersPerSec(ordersPerHour / 3600.0).during(testDuration.seconds)
    ).protocols(httpProtocol),
    
    driverLocationScenario.inject(
      rampUsers(activeDrivers).during(60.seconds)
    ).protocols(fleetProtocol),
    
    trackingScenario.inject(
      rampUsers(wsConnections).during(30.seconds)
    ).protocols(wsProtocol),

    aiForecastingScenario.inject(
      constantUsersPerSec(10).during(testDuration.seconds)
    ).protocols(mlProtocol)
  ).assertions(
    global.responseTime.percentile3.lt(3000), // 99th percentile < 3s
    global.responseTime.mean.lt(1000),
    global.successfulRequests.percent.gt(95)
  )
}
