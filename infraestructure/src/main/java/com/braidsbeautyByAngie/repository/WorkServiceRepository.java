package com.braidsbeautyByAngie.repository;

import com.braidsbeautyByAngie.entity.WorkServiceEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public interface WorkServiceRepository extends JpaRepository<WorkServiceEntity, Long> {
    @Query(value = "SELECT CASE WHEN COUNT(w) > 0 THEN true ELSE false END FROM WorkServiceEntity w WHERE w.workServiceId = :promotionId AND w.state = true")
    boolean existsByWorkServiceIdAndStateTrue(Long promotionId);

    @Query("SELECT w FROM WorkServiceEntity w WHERE w.workServiceId = :workServiceId AND w.state = true")
    Optional<WorkServiceEntity> findWorkServiceByIdWithStateTrue(Long workServiceId);
    @Query("SELECT w FROM WorkServiceEntity w WHERE w.state = true")
    Page<WorkServiceEntity> findAllByStateTrueAndPageable(Pageable pageable);
}
