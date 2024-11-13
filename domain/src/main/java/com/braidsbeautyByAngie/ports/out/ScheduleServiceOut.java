package com.braidsbeautyByAngie.ports.out;

import com.braidsbeautyByAngie.aggregates.dto.ScheduleDTO;
import com.braidsbeautyByAngie.aggregates.request.RequestSchedule;
import com.braidsbeautyByAngie.aggregates.response.schedules.ResponseListPageableSchedule;
import com.braidsbeautyByAngie.aggregates.response.schedules.ResponseSchedule;

import java.util.Optional;

public interface ScheduleServiceOut {
    ScheduleDTO createScheduleOut(RequestSchedule requestSchedule);
    Optional<ResponseSchedule> findScheduleByIdOut(Long scheduleId);
    ScheduleDTO updateScheduleOut(Long scheduleId, RequestSchedule requestSchedule);
    ScheduleDTO deleteScheduleOut(Long scheduleId);
    ResponseListPageableSchedule listScheduleByPageOut(int pageNumber, int pageSize, String orderBy, String sortDir);
}
