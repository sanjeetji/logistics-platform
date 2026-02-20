package com.logistics.dispatch.model;

import com.logistics.platform.utils.model.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDateTime;

@Entity
@Table(name = "dispatch_jobs")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@SQLDelete(sql = "UPDATE dispatch_jobs SET deleted = true WHERE id=?")
@SQLRestriction("deleted=false")
public class DispatchJob extends BaseEntity {

    private String orderId;

    private String matchedDriverId;

    @Enumerated(EnumType.STRING)
    private DispatchStatus status;

    private Integer attempts;

    private String lastErrorMessage;

    private LocalDateTime nextRetryAt;

    @Builder.Default
    private boolean deleted = false;
}
