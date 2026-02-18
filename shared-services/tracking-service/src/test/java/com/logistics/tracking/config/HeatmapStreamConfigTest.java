package com.logistics.tracking.config;

import com.logistics.tracking.dto.HeatmapData;
import com.logistics.tracking.dto.TrackingEvent;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.streams.*;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.TestInputTopic;
import org.apache.kafka.streams.TestOutputTopic;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.support.serializer.JsonSerde;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import java.util.Map;

import java.time.LocalDateTime;
import java.util.Properties;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;

class HeatmapStreamConfigTest {

    private TopologyTestDriver testDriver;
    private TestInputTopic<String, TrackingEvent> inputTopic;
    private TestOutputTopic<String, HeatmapData> outputTopic;

    @BeforeEach
    void setup() {
        HeatmapStreamConfig config = new HeatmapStreamConfig();
        Function<KStream<String, TrackingEvent>, KStream<String, HeatmapData>> processHeatmap = config.processHeatmap();

        StreamsBuilder builder = new StreamsBuilder();
        KStream<String, TrackingEvent> stream = builder.stream("tracking.events");
        processHeatmap.apply(stream).to("market.heatmap");

        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "test-heatmap-app");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "dummy:1234");
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, JsonSerde.class.getName());
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "*");

        testDriver = new TopologyTestDriver(builder.build(), props);

        Map<String, Object> serdeProps = Map.of(JsonDeserializer.TRUSTED_PACKAGES, "*");

        JsonSerde<TrackingEvent> inputSerde = new JsonSerde<>(TrackingEvent.class);
        inputSerde.configure(serdeProps, false);

        JsonSerde<HeatmapData> outputSerde = new JsonSerde<>(HeatmapData.class);
        outputSerde.configure(serdeProps, false);

        inputTopic = testDriver.createInputTopic(
                "tracking.events",
                new StringSerializer(),
                inputSerde.serializer());

        outputTopic = testDriver.createOutputTopic(
                "market.heatmap",
                new StringDeserializer(),
                outputSerde.deserializer());
    }

    @AfterEach
    void tearDown() {
        if (testDriver != null) {
            testDriver.close();
        }
    }

    @Test
    void testHeatmapAggregation() {
        // Given: Two drivers in the same location (same geohash)
        TrackingEvent event1 = TrackingEvent.builder()
                .driverId(101L)
                .currentLat(40.7128)
                .currentLng(-74.0060) // New York
                .timestamp(LocalDateTime.now())
                .build();

        TrackingEvent event2 = TrackingEvent.builder()
                .driverId(102L)
                .currentLat(40.7129) // Very close, should be same geohash
                .currentLng(-74.0061)
                .timestamp(LocalDateTime.now())
                .build();

        // When: Events are piped
        inputTopic.pipeInput("k1", event1);
        inputTopic.pipeInput("k2", event2);

        // Then: Output should contain aggregated counts
        // Note: Due to windowing and caching, we might need to advance time or flush
        // But with TopologyTestDriver, it processes immediately unless suppressed.
        // The window size is 60s. We should see updates.

        assertFalse(outputTopic.isEmpty(), "Output topic should not be empty");

        KeyValue<String, HeatmapData> result = outputTopic.readKeyValue();
        assertNotNull(result.value);
        assertEquals(1L, result.value.getVehicleCount()); // First event

        result = outputTopic.readKeyValue();
        assertNotNull(result.value);
        assertEquals(2L, result.value.getVehicleCount()); // Second event update (or same bucket)
        assertEquals(result.key, result.value.getGeohash());
    }
}
