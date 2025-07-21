package com.braidsbeautyByAngie.repository;

import com.braidsbeautyByAngie.entity.ScheduleEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
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
    // NUEVO: Filtrar por fecha desde una fecha específica
    @Query("SELECT s FROM ScheduleEntity s WHERE s.state = true AND s.scheduleDate >= :fromDate ORDER BY s.scheduleDate ASC, s.scheduleHourStart ASC")
    List<ScheduleEntity> findAllByStateTrueAndScheduleDateFromDate(@Param("fromDate") LocalDate fromDate);

    // NUEVO: Filtrar por rango de fechas (opcional)
    @Query("SELECT s FROM ScheduleEntity s WHERE s.state = true AND s.scheduleDate BETWEEN :fromDate AND :toDate ORDER BY s.scheduleDate ASC, s.scheduleHourStart ASC")
    List<ScheduleEntity> findAllByStateTrueAndScheduleDateBetween(@Param("fromDate") LocalDate fromDate, @Param("toDate") LocalDate toDate);

    // NUEVO: Filtrar por fecha específica
    @Query("SELECT s FROM ScheduleEntity s WHERE s.state = true AND s.scheduleDate = :scheduleDate ORDER BY s.scheduleHourStart ASC")
    List<ScheduleEntity> findAllByStateTrueAndScheduleDate(@Param("scheduleDate") LocalDate scheduleDate);
}
