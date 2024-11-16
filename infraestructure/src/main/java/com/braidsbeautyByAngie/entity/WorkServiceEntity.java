package com.braidsbeautyByAngie.entity;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;

@Entity
@Table(name = "work_service")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WorkServiceEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "work_service_Id", nullable = false)
    private Long workServiceId;
    @Column(name = "work_service_state", nullable = false)
    private String workServiceState;

    // Relaciones opcionales
    @ManyToOne(optional = true)
    @JoinColumn(name = "service_id", referencedColumnName = "service_id", nullable = true)
    private ServiceEntity serviceEntity;

    @ManyToOne(optional = true)
    @JoinColumn(name = "promotion_id", referencedColumnName = "promotion_id", nullable = true)
    private PromotionEntity promotionEntity;

    @ManyToOne(optional = true)
    @JoinColumn(name = "schedule_id", referencedColumnName = "schedule_id", nullable = true)
    private ScheduleEntity scheduleEntity;

    @ManyToOne(optional = false)
    @JoinColumn(name = "reservation_id", referencedColumnName = "reservation_id")
    private ReservationEntity reservationEntity;
    @Column(name = "state", nullable = false)
    private Boolean state;

    @Column(name = "modified_by_user", nullable = false, length = 15)
    private String modifiedByUser;

    @Column(name = "created_at", nullable = false)
    private Timestamp createdAt;

    @Column(name = "modified_at")
    private Timestamp modifiedAt;

    @Column(name = "deleted_at")
    private Timestamp deletedAt;

}
