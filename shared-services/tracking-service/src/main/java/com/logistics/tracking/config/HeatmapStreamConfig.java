package com.logistics.tracking.config;

import com.logistics.tracking.dto.HeatmapData;
import com.logistics.tracking.dto.TrackingEvent;
import com.logistics.tracking.util.GeohashUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.Grouped;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.TimeWindows;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.function.Function;

@Configuration
@Slf4j
public class HeatmapStreamConfig {

    private static final int GEOHASH_PRECISION = 6; // ~1.2km x 0.6km
    private static final long WINDOW_SIZE_SECONDS = 60;

    @Bean
    public Function<KStream<String, TrackingEvent>, KStream<String, HeatmapData>> processHeatmap() {
        return stream -> stream
                .filter((key, event) -> event.getCurrentLat() != null && event.getCurrentLng() != null)
                .map((key, event) -> {
                    String geohash = GeohashUtils.encode(event.getCurrentLat(), event.getCurrentLng(),
                            GEOHASH_PRECISION);
                    return KeyValue.pair(geohash, 1L);
                })
                .groupByKey(Grouped.with(Serdes.String(), Serdes.Long()))
                .windowedBy(TimeWindows.ofSizeWithNoGrace(Duration.ofSeconds(WINDOW_SIZE_SECONDS)))
                .count(Materialized.as("heatmap-counts"))
                .toStream()
                .map((windowedKey, count) -> {
                    String geohash = windowedKey.key();
                    LocalDateTime start = LocalDateTime.ofInstant(Instant.ofEpochMilli(windowedKey.window().start()),
                            ZoneId.systemDefault());
                    LocalDateTime end = LocalDateTime.ofInstant(Instant.ofEpochMilli(windowedKey.window().end()),
                            ZoneId.systemDefault());

                    HeatmapData heatmapData = HeatmapData.builder()
                            .geohash(geohash)
                            .vehicleCount(count)
                            .windowStart(start)
                            .windowEnd(end)
                            .build();

                    log.debug("Emitting heatmap data for geohash {}: count={}", geohash, count);
                    return KeyValue.pair(geohash, heatmapData);
                });
    }
}
