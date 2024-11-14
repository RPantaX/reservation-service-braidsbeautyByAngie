package com.braidsbeautyByAngie.repository;

import com.braidsbeautyByAngie.entity.ReservationEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReservationRepository extends JpaRepository<ReservationEntity, Long> {
    @Query(value = "SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END FROM ReservationEntity r WHERE r.reservationId = :reservationId AND r.state = true")
    boolean existsReservationIdAndStateTrue(Long reservationId);

    @Query(value = "SELECT r FROM ReservationEntity r WHERE r.reservationId = :reservationId AND r.state = true")
    Optional<ReservationEntity> findReservationByReservationIdAndStateTrue(Long reservationId);

    @Query(value = "SELECT r FROM ReservationEntity r WHERE r.state = true")
    Page<ReservationEntity> findAllByStateTrueAndPageable (Pageable pageable);
}
