package com.logistics.analytics.repository;

import com.logistics.analytics.dto.HeatmapDataPoint;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Repository for geospatial analytics queries using PostGIS
 */
@Repository
public class LocationAnalyticsRepository {

    private final JdbcTemplate jdbcTemplate;

    public LocationAnalyticsRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * Generate demand heatmap using grid-based aggregation
     */
    public List<HeatmapDataPoint> getDemandHeatmap(
            double minLat, double maxLat,
            double minLon, double maxLon,
            double gridSize,
            LocalDateTime startTime,
            LocalDateTime endTime,
            int minThreshold) {

        String sql = """
                SELECT
                    ST_X(grid_point) as longitude,
                    ST_Y(grid_point) as latitude,
                    COUNT(*) as order_count,
                    AVG(CAST(total_price AS DECIMAL)) as avg_price
                FROM (
                    SELECT
                        ST_SnapToGrid(
                            ST_SetSRID(ST_MakePoint(pickup_longitude, pickup_latitude), 4326),
                            ?
                        ) as grid_point,
                        total_price
                    FROM orders
                    WHERE created_at BETWEEN ? AND ?
                      AND pickup_latitude BETWEEN ? AND ?
                      AND pickup_longitude BETWEEN ? AND ?
                ) grouped
                GROUP BY grid_point
                HAVING COUNT(*) >= ?
                ORDER BY order_count DESC
                """;

        return jdbcTemplate.query(sql, new HeatmapDataPointMapper(),
                gridSize, startTime, endTime, minLat, maxLat, minLon, maxLon, minThreshold);
    }

    /**
     * Generate driver availability heatmap
     */
    public List<HeatmapDataPoint> getDriverAvailabilityHeatmap(
            double minLat, double maxLat,
            double minLon, double maxLon,
            double gridSize) {

        String sql = """
                SELECT
                    ST_X(grid_point) as longitude,
                    ST_Y(grid_point) as latitude,
                    COUNT(*) as driver_count,
                    SUM(CASE WHEN status = 'AVAILABLE' THEN 1 ELSE 0 END) as available_count
                FROM (
                    SELECT
                        ST_SnapToGrid(
                            ST_SetSRID(ST_MakePoint(longitude, latitude), 4326),
                            ?
                        ) as grid_point,
                        status
                    FROM drivers
                    WHERE latitude BETWEEN ? AND ?
                      AND longitude BETWEEN ? AND ?
                      AND status IN ('AVAILABLE', 'BUSY')
                ) grouped
                GROUP BY grid_point
                ORDER BY driver_count DESC
                """;

        return jdbcTemplate.query(sql, new DriverHeatmapMapper(),
                gridSize, minLat, maxLat, minLon, maxLon);
    }

    /**
     * Identify high-demand hotspots using spatial clustering
     */
    public List<HeatmapDataPoint> identifyHotspots(
            LocalDateTime startTime,
            LocalDateTime endTime,
            int minOrders) {

        String sql = """
                WITH clustered AS (
                    SELECT
                        pickup_latitude,
                        pickup_longitude,
                        total_price,
                        ST_ClusterDBSCAN(
                            ST_SetSRID(ST_MakePoint(pickup_longitude, pickup_latitude), 4326),
                            0.05,
                            ?
                        ) OVER() as cluster_id
                    FROM orders
                    WHERE created_at BETWEEN ? AND ?
                )
                SELECT
                    AVG(pickup_latitude) as latitude,
                    AVG(pickup_longitude) as longitude,
                    COUNT(*) as order_count,
                    AVG(CAST(total_price AS DECIMAL)) as avg_price
                FROM clustered
                WHERE cluster_id IS NOT NULL
                GROUP BY cluster_id
                HAVING COUNT(*) >= ?
                ORDER BY order_count DESC
                LIMIT 20
                """;

        return jdbcTemplate.query(sql, new HeatmapDataPointMapper(),
                minOrders, startTime, endTime, minOrders);
    }

    /**
     * Row mapper for heatmap data points
     */
    private static class HeatmapDataPointMapper implements RowMapper<HeatmapDataPoint> {
        @Override
        public HeatmapDataPoint mapRow(ResultSet rs, int rowNum) throws SQLException {
            Map<String, Object> metadata = new HashMap<>();

            // Add avg_price if available
            try {
                double avgPrice = rs.getDouble("avg_price");
                if (!rs.wasNull()) {
                    metadata.put("avgPrice", avgPrice);
                }
            } catch (SQLException ignored) {
            }

            int count = rs.getInt("order_count");

            return HeatmapDataPoint.builder()
                    .latitude(rs.getDouble("latitude"))
                    .longitude(rs.getDouble("longitude"))
                    .count(count)
                    .intensity(0.0) // Will be normalized later
                    .metadata(metadata)
                    .build();
        }
    }

    /**
     * Row mapper for driver heatmap
     */
    private static class DriverHeatmapMapper implements RowMapper<HeatmapDataPoint> {
        @Override
        public HeatmapDataPoint mapRow(ResultSet rs, int rowNum) throws SQLException {
            Map<String, Object> metadata = new HashMap<>();

            int driverCount = rs.getInt("driver_count");
            int availableCount = rs.getInt("available_count");

            metadata.put("totalDrivers", driverCount);
            metadata.put("availableDrivers", availableCount);
            metadata.put("availabilityRate", driverCount > 0 ? (double) availableCount / driverCount : 0.0);

            return HeatmapDataPoint.builder()
                    .latitude(rs.getDouble("latitude"))
                    .longitude(rs.getDouble("longitude"))
                    .count(driverCount)
                    .intensity(0.0) // Will be normalized later
                    .metadata(metadata)
                    .build();
        }
    }
}
