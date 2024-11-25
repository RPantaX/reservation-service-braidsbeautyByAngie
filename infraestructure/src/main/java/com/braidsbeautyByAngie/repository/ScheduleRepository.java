package com.braidsbeautyByAngie.repository;

import com.braidsbeautyByAngie.entity.ScheduleEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ScheduleRepository extends JpaRepository<ScheduleEntity, Long> {

    @Query(value = "SELECT CASE WHEN COUNT(s) > 0 THEN true ELSE false END FROM ScheduleEntity s WHERE s.scheduleId = :scheduleId AND s.state = true")
    boolean existsByScheduleIdAndStateTrue(Long scheduleId);
    @Query("SELECT s FROM ScheduleEntity s WHERE s.scheduleId = :scheduleId AND s.state = true AND s.scheduleState = 'FREE'")
    Optional<ScheduleEntity> findScheduleByIdWithStateTrueAndScheduleStateLIBRE(Long scheduleId);

    @Query("SELECT s FROM ScheduleEntity s WHERE s.scheduleId = :scheduleId AND s.state = true")
    Optional<ScheduleEntity> findScheduleByIdWithStateTrue(Long scheduleId);

    @Query("SELECT s FROM ScheduleEntity s WHERE s.state = true")
    Page<ScheduleEntity> findAllByStateTrueAndPageable(Pageable pageable);

}
