package com.logistics.analytics.streaming.processor;

import com.logistics.analytics.streaming.model.SLAMetrics;
import com.logistics.analytics.streaming.service.MetricsStorageService;
import com.logistics.platform.event.dto.SLABreachPredictedEvent;
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
public class SLAMetricsProcessorTest {

    @Mock
    private MetricsStorageService metricsStorageService;

    private SLAMetricsProcessor processor;
    private TopologyTestDriver testDriver;
    private TestInputTopic<String, SLABreachPredictedEvent> inputTopic;

    @BeforeEach
    public void setup() {
        processor = new SLAMetricsProcessor(metricsStorageService);

        StreamsBuilder builder = new StreamsBuilder();
        KStream<String, SLABreachPredictedEvent> stream = builder.stream("sla-events",
                Consumed.with(Serdes.String(), new JsonSerde<>(SLABreachPredictedEvent.class)));

        processor.processSLAMetrics().accept(stream);

        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "test-sla-app");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "dummy:1234");
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, JsonSerde.class.getName());

        testDriver = new TopologyTestDriver(builder.build(), props);

        inputTopic = testDriver.createInputTopic("sla-events",
                new StringSerializer(), new JsonSerializer<>());
    }

    @AfterEach
    public void tearDown() {
        testDriver.close();
    }

    @Test
    public void testSLAMetricsAggregation() {
        String slaId = "sla-1";

        // Critical Violation
        SLABreachPredictedEvent event1 = SLABreachPredictedEvent.builder()
                .orderId("order-1")
                .slaId(slaId)
                .riskLevel("CRITICAL")
                .timestamp(LocalDateTime.now())
                .build();

        inputTopic.pipeInput(slaId, event1);

        // Minor Violation
        SLABreachPredictedEvent event2 = SLABreachPredictedEvent.builder()
                .orderId("order-2")
                .slaId("sla-2")
                .riskLevel("LOW")
                .timestamp(LocalDateTime.now())
                .build();

        inputTopic.pipeInput(slaId, event2);

        ArgumentCaptor<SLAMetrics> captor = ArgumentCaptor.forClass(SLAMetrics.class);
        verify(metricsStorageService, times(2)).storeSLAMetrics(captor.capture());

        SLAMetrics lastMetrics = captor.getAllValues().get(1);

        // Check violations
        assertEquals(2, lastMetrics.getSlaViolations());
        assertEquals(1, lastMetrics.getCriticalViolations());
        assertEquals(1, lastMetrics.getMinorViolations());
    }
}
