package com.logistics.tracking.service;

import com.logistics.tracking.dto.HeatmapData;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.Window;
import org.apache.kafka.streams.kstream.Windowed;
import org.apache.kafka.streams.state.KeyValueIterator;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyWindowStore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.cloud.stream.binder.kafka.streams.InteractiveQueryService;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class HeatmapQueryTest {

    @Mock
    private InteractiveQueryService interactiveQueryService;

    @Mock
    private ReadOnlyWindowStore<String, Long> windowStore;

    @InjectMocks
    private HeatmapService heatmapService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getLiveHeatmap_returnsData_whenStoreAvailable() {
        when(interactiveQueryService.getQueryableStore(eq("heatmap-counts"), any()))
                .thenReturn(windowStore);

        KeyValueIterator<Windowed<String>, Long> iterator = mock(KeyValueIterator.class);

        long now = Instant.now().toEpochMilli();
        Window mockWindow = new Window(now - 60000, now) {
            @Override
            public boolean overlap(Window other) {
                return false;
            }
        };

        Windowed<String> windowedKey = new Windowed<>("tdr123", mockWindow);
        KeyValue<Windowed<String>, Long> kv = new KeyValue<>(windowedKey, 5L);

        when(iterator.hasNext()).thenReturn(true, false);
        when(iterator.next()).thenReturn(kv);

        when(windowStore.all()).thenReturn(iterator);

        List<HeatmapData> result = heatmapService.getLiveHeatmap();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("tdr123", result.get(0).getGeohash());
        assertEquals(5L, result.get(0).getVehicleCount());
    }

    @Test
    void getLiveHeatmap_returnsEmpty_whenStoreNotAvailable() {
        when(interactiveQueryService.getQueryableStore(eq("heatmap-counts"), any()))
                .thenReturn(null);

        List<HeatmapData> result = heatmapService.getLiveHeatmap();

        assertNotNull(result);
        assertEquals(0, result.size());
    }
}
