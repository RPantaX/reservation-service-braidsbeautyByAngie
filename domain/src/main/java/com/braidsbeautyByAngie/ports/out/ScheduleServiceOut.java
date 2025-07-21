package com.braidsbeautyByAngie.ports.out;

import com.braidsbeautyByAngie.aggregates.dto.ScheduleDTO;
import com.braidsbeautyByAngie.aggregates.request.RequestSchedule;
import com.braidsbeautyByAngie.aggregates.response.schedules.ResponseListPageableSchedule;
import com.braidsbeautyByAngie.aggregates.response.schedules.ResponseSchedule;
import com.braidsbeautyByAngie.aggregates.response.schedules.ResponseScheduleWithEmployee;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ScheduleServiceOut {
    ScheduleDTO createScheduleOut(RequestSchedule requestSchedule);
    List<ScheduleDTO> createSchedulesBulkOut(List<RequestSchedule> requestSchedules);
    Optional<ResponseSchedule> findScheduleByIdOut(Long scheduleId);
    ScheduleDTO updateScheduleOut(Long scheduleId, RequestSchedule requestSchedule);
    ScheduleDTO deleteScheduleOut(Long scheduleId);
    ResponseListPageableSchedule listScheduleByPageOut(int pageNumber, int pageSize, String orderBy, String sortDir);
    List<ResponseScheduleWithEmployee> listScheduleOut();
    // NUEVOS MÉTODOS
    List<ResponseScheduleWithEmployee> listScheduleFromDateOut(LocalDate fromDate);
    List<ResponseScheduleWithEmployee> listScheduleBetweenDatesOut(LocalDate fromDate, LocalDate toDate);
    List<ResponseScheduleWithEmployee> listScheduleBySpecificDateOut(LocalDate scheduleDate);
}
