package com.braidsbeautyByAngie.entity;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "service")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ServiceEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "service_Id", nullable = false)
    private Long serviceId;
    @Column(name = "service_name", nullable = false)
    private String serviceName;
    @Column(name = "service_description", nullable = false)
    private String serviceDescription;
    @Column(name = "service_image", nullable = false)
    private String serviceImage;
    @Column(name = "duration_time_aprox", nullable = false)
    private LocalTime durationTimeAprox;

    @ManyToOne(optional = true)
    @JoinColumn(name = "service_category_Id")
    private ServiceCategoryEntity serviceCategoryEntity;

    @OneToMany(mappedBy = "serviceEntity", cascade = CascadeType.ALL)
    private List<WorkServiceEntity> workServices = new ArrayList<>();


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
