package com.logistics.analytics.service;

import com.logistics.analytics.model.RevenueReport;
import com.logistics.analytics.repository.RevenueReportRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Service for revenue analytics
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RevenueAnalyticsService {

    private final RevenueReportRepository reportRepository;

    /**
     * Generate daily revenue report (runs at midnight)
     */
    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void generateDailyReport() {
        log.info("Generating daily revenue report");

        LocalDate yesterday = LocalDate.now().minusDays(1);

        // Mock data - in real implementation, query from order/payment services
        BigDecimal totalRevenue = BigDecimal.valueOf(50000.00);
        BigDecimal b2cRevenue = BigDecimal.valueOf(30000.00);
        BigDecimal b2bRevenue = BigDecimal.valueOf(20000.00);
        int orderCount = 150;
        int b2cOrderCount = 100;
        int b2bOrderCount = 50;

        BigDecimal avgOrderValue = totalRevenue.divide(
                BigDecimal.valueOf(orderCount), 2, RoundingMode.HALF_UP);

        RevenueReport report = RevenueReport.builder()
                .reportId("REV-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                .period(yesterday)
                .totalRevenue(totalRevenue)
                .b2cRevenue(b2cRevenue)
                .b2bRevenue(b2bRevenue)
                .orderCount(orderCount)
                .b2cOrderCount(b2cOrderCount)
                .b2bOrderCount(b2bOrderCount)
                .averageOrderValue(avgOrderValue)
                .build();

        reportRepository.save(report);
        log.info("Daily revenue report generated for {}", yesterday);
    }

    /**
     * Get revenue report for period
     */
    public RevenueReport getReportForPeriod(LocalDate period) {
        return reportRepository.findByPeriod(period)
                .orElseThrow(() -> new RuntimeException("Report not found for period: " + period));
    }

    /**
     * Get recent reports
     */
    public List<RevenueReport> getRecentReports() {
        return reportRepository.findTop30ByOrderByPeriodDesc();
    }
}
