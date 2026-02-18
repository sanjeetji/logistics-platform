package com.logistics.analytics.streaming.processor;

import com.logistics.analytics.streaming.model.DriverMetrics;
import com.logistics.analytics.streaming.service.MetricsStorageService;
import com.logistics.platform.event.dto.FleetStatusChangedEvent;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.kafka.streams.*;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.support.serializer.JsonSerde;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.time.LocalDateTime;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
public class DriverMetricsProcessorTest {

    @Mock
    private MetricsStorageService metricsStorageService;

    private DriverMetricsProcessor processor;
    private TopologyTestDriver testDriver;
    private TestInputTopic<String, FleetStatusChangedEvent> inputTopic;

    @BeforeEach
    public void setup() {
        processor = new DriverMetricsProcessor(metricsStorageService);

        StreamsBuilder builder = new StreamsBuilder();
        KStream<String, FleetStatusChangedEvent> stream = builder.stream("driver-events",
                Consumed.with(Serdes.String(), new JsonSerde<>(FleetStatusChangedEvent.class)));

        processor.processDriverMetrics().accept(stream);

        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "test-app");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "dummy:1234");
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, JsonSerde.class.getName());

        testDriver = new TopologyTestDriver(builder.build(), props);

        inputTopic = testDriver.createInputTopic("driver-events",
                new StringSerializer(), new JsonSerializer<>());
    }

    @AfterEach
    public void tearDown() {
        testDriver.close();
    }

    @Test
    public void testDriverMetricsAggregation() {
        String driverId = "driver-1";

        // Send AVAILABLE event
        FleetStatusChangedEvent event1 = FleetStatusChangedEvent.builder()
                .driverId(driverId)
                .newStatus("AVAILABLE")
                .timestamp(LocalDateTime.now())
                .build();

        inputTopic.pipeInput(driverId, event1);

        // Send BUSY event
        FleetStatusChangedEvent event2 = FleetStatusChangedEvent.builder()
                .driverId(driverId)
                .newStatus("BUSY")
                .timestamp(LocalDateTime.now())
                .build();

        inputTopic.pipeInput(driverId, event2);

        // Verify that metrics were stored (captured by the mock)
        // Since it's a stream, it might output multiple times (once per event per
        // window update)
        ArgumentCaptor<DriverMetrics> captor = ArgumentCaptor.forClass(DriverMetrics.class);
        verify(metricsStorageService, times(2)).storeDriverMetrics(captor.capture());

        DriverMetrics lastMetrics = captor.getAllValues().get(1);

        // Check "Total Drivers" (activity count in our implementation)
        assertEquals(2, lastMetrics.getTotalDrivers());

        // Check status counts
        assertEquals(1, lastMetrics.getAvailableDrivers());
        assertEquals(1, lastMetrics.getBusyDrivers());
    }
}
