package com.logistics.parcel.model;

import com.logistics.platform.utils.model.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "partners")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Partner extends BaseEntity {

    private String name;
    private String baseUrl;
    private String apiKey;
    private String status; // ACTIVE, INACTIVE
    private Integer priority;
}
