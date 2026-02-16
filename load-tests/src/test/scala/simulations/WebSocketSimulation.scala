package simulations

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.http.protocol.HttpProtocolBuilder
import scala.concurrent.duration._

/**
 * Load test simulation for WebSocket Connections
 * Goal: Test 1000+ concurrent WebSocket connections
 */
class WebSocketSimulation extends Simulation {

  // Configuration
  val baseUrl = System.getProperty("wsUrl", "ws://localhost:8085")
  val targetConnections = System.getProperty("targetConnections", "1000").toInt
  val rampUpDuration = System.getProperty("rampUp", "60").toInt // 1 minute default

  // WebSocket Protocol Configuration
  val wsProtocol = http
    .wsBaseUrl(baseUrl)
    .wsAutoReplyTextFrame { case "ping" => "pong" }

  // Scenario for real-time location tracking
  val locationTrackingScenario = scenario("WebSocket Location Tracking")
    .exec(
      ws("Connect").connect("/location-stream")
        .await(5.seconds)(
          ws.checkTextMessage("connected")
            .check(regex("connected").saveAs("status"))
        )
    )
    .pause(2.seconds)
    .exec(
      ws("Subscribe to Driver")
        .sendText("""{"action":"subscribe","driverId":"DRIVER-${__Random(1,1000)}"}""")
        .await(2.seconds)(
          ws.checkTextMessage("subscribed")
        )
    )
    .during(5.minutes) {
      exec(
        ws("Receive Location Update")
          .checkTextMessage("location")
          .check(jsonPath("$.latitude").exists)
          .check(jsonPath("$.longitude").exists)
      )
        .pause(5.seconds) // Expect location updates every 5 seconds
    }
    .exec(
      ws("Close Connection").close
    )

  // Scenario for order status updates
  val orderUpdatesScenario = scenario("WebSocket Order Updates")
    .exec(
      ws("Connect").connect("/order-updates")
        .await(5.seconds)(
          ws.checkTextMessage("connected")
            .check(regex("connected").saveAs("status"))
        )
    )
    .pause(1.second)
    .exec(
      ws("Subscribe to Order")
        .sendText("""{"action":"subscribe","orderId":"ORDER-${__UUID()}"}""")
        .await(2.seconds)(
          ws.checkTextMessage("subscribed")
        )
    )
    .during(10.minutes) {
      exec(
        ws("Receive Order Update")
          .checkTextMessage("update")
          .check(jsonPath("$.status").exists)
      )
        .pause(30.seconds) // Expect order updates periodically
    }
    .exec(
      ws("Close Connection").close
    )

  // Setup simulation - split connections between scenarios
  setUp(
    locationTrackingScenario.inject(
      rampUsers(targetConnections / 2).during(rampUpDuration.seconds)
    ),
    orderUpdatesScenario.inject(
      rampUsers(targetConnections / 2).during(rampUpDuration.seconds)
    )
  ).protocols(wsProtocol)
    .assertions(
      global.responseTime.max.lt(3000),
      global.failedRequests.count.lt((targetConnections * 0.01).toLong) // < 1% failure
    )
}
