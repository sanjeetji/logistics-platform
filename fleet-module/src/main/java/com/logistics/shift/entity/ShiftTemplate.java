package com.logistics.shift.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Set;

@Entity
@Table(name = "shift_templates")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShiftTemplate {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name; // e.g., "Morning Shift", "Evening Shift"
    
    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;
    
    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;
    
    @ElementCollection(targetClass = DayOfWeek.class)
    @CollectionTable(name = "shift_template_days", joinColumns = @JoinColumn(name = "template_id"))
    @Column(name = "day_of_week")
    @Enumerated(EnumType.STRING)
    private Set<DayOfWeek> daysOfWeek; // Days this shift applies to
    
    @Column(name = "max_drivers")
    private Integer maxDrivers; // Maximum drivers for this shift
    
    @Column(name = "min_drivers")
    private Integer minDrivers; // Minimum drivers required
    
    @Column
    private String description;
    
    @Column
    private Boolean active = true;
}
