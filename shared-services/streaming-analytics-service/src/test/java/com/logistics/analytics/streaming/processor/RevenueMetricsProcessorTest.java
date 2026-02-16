package com.logistics.analytics.streaming.processor;

import com.logistics.analytics.streaming.model.RevenueMetrics;
import com.logistics.analytics.streaming.service.MetricsStorageService;
import com.logistics.platform.event.dto.PaymentCompletedEvent;
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

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
public class RevenueMetricsProcessorTest {

    @Mock
    private MetricsStorageService metricsStorageService;

    private RevenueMetricsProcessor processor;
    private TopologyTestDriver testDriver;
    private TestInputTopic<String, PaymentCompletedEvent> inputTopic;

    @BeforeEach
    public void setup() {
        processor = new RevenueMetricsProcessor(metricsStorageService);

        StreamsBuilder builder = new StreamsBuilder();
        KStream<String, PaymentCompletedEvent> stream = builder.stream("payment-events",
                Consumed.with(Serdes.String(), new JsonSerde<>(PaymentCompletedEvent.class)));

        processor.processRevenueMetrics().accept(stream);

        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "test-revenue-app");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "dummy:1234");
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, JsonSerde.class.getName());

        testDriver = new TopologyTestDriver(builder.build(), props);

        inputTopic = testDriver.createInputTopic("payment-events",
                new StringSerializer(), new JsonSerializer<>());
    }

    @AfterEach
    public void tearDown() {
        testDriver.close();
    }

    @Test
    public void testRevenueMetricsAggregation() {
        String paymentId = "payment-1";

        // Send Payment Event 1
        PaymentCompletedEvent event1 = PaymentCompletedEvent.builder()
                .paymentId(paymentId)
                .amount(new BigDecimal("100.00"))
                .paymentMethod("CREDIT_CARD")
                .completedAt(LocalDateTime.now())
                .build();

        inputTopic.pipeInput(paymentId, event1);

        // Send Payment Event 2
        PaymentCompletedEvent event2 = PaymentCompletedEvent.builder()
                .paymentId("payment-2")
                .amount(new BigDecimal("50.00"))
                .paymentMethod("PAYPAL")
                .completedAt(LocalDateTime.now())
                .build();

        inputTopic.pipeInput(paymentId, event2);

        ArgumentCaptor<RevenueMetrics> captor = ArgumentCaptor.forClass(RevenueMetrics.class);
        verify(metricsStorageService, times(2)).storeRevenueMetrics(captor.capture());

        RevenueMetrics lastMetrics = captor.getAllValues().get(1);

        // Check totals
        assertEquals(2, lastMetrics.getTotalTransactions());
        assertEquals(150.0, lastMetrics.getTotalRevenue());
        assertEquals(75.0, lastMetrics.getAverageOrderValue());
    }
}
