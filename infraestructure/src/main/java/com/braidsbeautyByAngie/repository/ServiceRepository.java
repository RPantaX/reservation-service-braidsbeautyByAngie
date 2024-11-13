package com.braidsbeautyByAngie.repository;

import com.braidsbeautyByAngie.entity.ServiceEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface ServiceRepository extends JpaRepository<ServiceEntity, Long> {

    boolean existsByServiceName(String serviceName);

    @Query(value = "SELECT CASE WHEN COUNT(s) > 0 THEN true ELSE false END FROM ServiceEntity s WHERE s.serviceId = :serviceId AND s.state = true")
    boolean existsByServiceIdAndStateTrue(Long serviceId);

    @Query("SELECT s FROM ServiceEntity s WHERE s.serviceId = :serviceId AND s.state = true")
    Optional<ServiceEntity> findServiceByIdWithStateTrue(Long serviceId);

    @Query("SELECT s FROM ServiceEntity s WHERE s.state = true")
    Page<ServiceEntity> findAllByStateTrueAndPageable(Pageable pageable);
}
