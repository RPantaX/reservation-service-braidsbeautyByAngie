package com.braidsbeautyByAngie.entity;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "schedule")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ScheduleEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "schedule_Id", nullable = false)
    private Long scheduleId;
    @Column(name = "schedule_date", nullable = false)
    private LocalDate scheduleDate;
    @Column(name = "schedule_hour_start", nullable = false)
    private LocalTime scheduleHourStart;
    @Column(name = "schedule_hour_end", nullable = false)
    private LocalTime scheduleHourEnd;
    @Column(name = "schedule_state", nullable = false)
    private String scheduleState;
    @Column(name = "employee_Id", nullable = false)
    private Long employeeId;
    @OneToOne(mappedBy = "scheduleEntity", cascade = CascadeType.ALL)
    private WorkServiceEntity workServiceEntity;
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