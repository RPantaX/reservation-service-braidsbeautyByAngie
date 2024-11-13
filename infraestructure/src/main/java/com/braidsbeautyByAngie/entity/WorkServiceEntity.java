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

    @OneToOne
    @JoinColumn(name = "service_Id", referencedColumnName = "service_Id", nullable = true)
    private ServiceEntity serviceEntity;

    @OneToOne
    @JoinColumn(name = "schedule_Id", referencedColumnName = "schedule_Id", nullable = true)
    private ScheduleEntity scheduleEntity;

    @ManyToOne(optional = true)
    @JoinColumn(name = "reservation_Id")
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
