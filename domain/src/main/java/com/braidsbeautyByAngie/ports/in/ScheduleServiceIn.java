package com.braidsbeautyByAngie.ports.in;

import com.braidsbeautyByAngie.aggregates.dto.ScheduleDTO;
import com.braidsbeautyByAngie.aggregates.request.RequestSchedule;
import com.braidsbeautyByAngie.aggregates.response.schedules.ResponseListPageableSchedule;
import com.braidsbeautyByAngie.aggregates.response.schedules.ResponseSchedule;
import com.braidsbeautyByAngie.aggregates.response.schedules.ResponseScheduleWithEmployee;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ScheduleServiceIn {
    ScheduleDTO createScheduleIn(RequestSchedule requestSchedule);
    Optional<ResponseSchedule> findScheduleByIdIn(Long scheduleId);
    ScheduleDTO updateScheduleIn(Long scheduleId, RequestSchedule requestSchedule);
    ScheduleDTO deleteScheduleIn(Long scheduleId);
    ResponseListPageableSchedule listScheduleByPageIn(int pageNumber, int pageSize, String orderBy, String sortDir);
    List<ResponseScheduleWithEmployee> listScheduleIn();

    // NUEVOS MÉTODOS
    List<ResponseScheduleWithEmployee> listScheduleFromDateIn(LocalDate fromDate);
    List<ResponseScheduleWithEmployee> listScheduleBetweenDatesIn(LocalDate fromDate, LocalDate toDate);
    List<ResponseScheduleWithEmployee> listScheduleBySpecificDateIn(LocalDate scheduleDate);
}
