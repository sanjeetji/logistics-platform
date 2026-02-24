package com.logistics.tracking.service;

import com.logistics.tracking.dto.HeatmapData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.state.KeyValueIterator;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyWindowStore;
import org.apache.kafka.streams.state.WindowStoreIterator;
import org.springframework.cloud.stream.binder.kafka.streams.InteractiveQueryService;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class HeatmapService {

    private static final String HEATMAP_STORE_NAME = "heatmap-counts";
    private final InteractiveQueryService interactiveQueryService;

    public List<HeatmapData> getLiveHeatmap() {
        List<HeatmapData> heatmapDataList = new ArrayList<>();
        try {
            ReadOnlyWindowStore<String, Long> windowStore = interactiveQueryService.getQueryableStore(
                    HEATMAP_STORE_NAME,
                    QueryableStoreTypes.windowStore());

            if (windowStore == null) {
                log.warn("Heatmap state store not ready or available yet: {}", HEATMAP_STORE_NAME);
                return heatmapDataList;
            }

            // Fetch data for the last 5 minutes as an example
            Instant timeTo = Instant.now();
            Instant timeFrom = timeTo.minusSeconds(300);

            // In a real distributed app, you would query across instances or just local.
            // Using a simple fetch all keys might be heavy, but for demonstration it works
            // The method 'all()' exists in ReadOnlyWindowStore
            try (KeyValueIterator<org.apache.kafka.streams.kstream.Windowed<String>, Long> iterator = windowStore
                    .all()) {
                while (iterator.hasNext()) {
                    KeyValue<org.apache.kafka.streams.kstream.Windowed<String>, Long> next = iterator.next();
                    String geohash = next.key.key();
                    Instant windowStart = Instant.ofEpochMilli(next.key.window().start());

                    if (windowStart.isAfter(timeFrom)) {
                        LocalDateTime start = LocalDateTime.ofInstant(windowStart, ZoneId.systemDefault());
                        LocalDateTime end = LocalDateTime.ofInstant(Instant.ofEpochMilli(next.key.window().end()),
                                ZoneId.systemDefault());

                        heatmapDataList.add(HeatmapData.builder()
                                .geohash(geohash)
                                .vehicleCount(next.value)
                                .windowStart(start)
                                .windowEnd(end)
                                .build());
                    }
                }
            }

        } catch (Exception e) {
            log.error("Error querying heatmap state store", e);
        }

        return heatmapDataList;
    }
}
