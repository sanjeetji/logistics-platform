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
public class Partner extends BaseEntity implements com.logistics.platform.common.dto.TenantAware {

    private String name;
    private String baseUrl;
    private String apiKey;
    private String status; // ACTIVE, INACTIVE
    private Integer priority;

    @jakarta.persistence.Column(name = "tenant_id")
    private String tenantId;

    @Override
    public String getTenantId() {
        return tenantId;
    }

    @Override
    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }
}
