package com.braidsbeautyByAngie.impl;

import com.braidsbeautyByAngie.aggregates.dto.ScheduleDTO;
import com.braidsbeautyByAngie.aggregates.request.RequestSchedule;
import com.braidsbeautyByAngie.aggregates.response.schedules.ResponseListPageableSchedule;
import com.braidsbeautyByAngie.aggregates.response.schedules.ResponseSchedule;
import com.braidsbeautyByAngie.aggregates.response.schedules.ResponseScheduleWithEmployee;
import com.braidsbeautyByAngie.ports.in.ScheduleServiceIn;
import com.braidsbeautyByAngie.ports.out.ScheduleServiceOut;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ScheduleServiceImpl implements ScheduleServiceIn {

    private final ScheduleServiceOut scheduleServiceOut;

    @Override
    public ScheduleDTO createScheduleIn(RequestSchedule requestSchedule) {
        return scheduleServiceOut.createScheduleOut(requestSchedule);
    }

    @Override
    public Optional<ResponseSchedule> findScheduleByIdIn(Long scheduleId) {
        return scheduleServiceOut.findScheduleByIdOut(scheduleId);
    }

    @Override
    public ScheduleDTO updateScheduleIn(Long scheduleId, RequestSchedule requestSchedule) {
        return scheduleServiceOut.updateScheduleOut(scheduleId, requestSchedule);
    }

    @Override
    public ScheduleDTO deleteScheduleIn(Long scheduleId) {
        return scheduleServiceOut.deleteScheduleOut(scheduleId);
    }

    @Override
    public ResponseListPageableSchedule listScheduleByPageIn(int pageNumber, int pageSize, String orderBy, String sortDir) {
        return scheduleServiceOut.listScheduleByPageOut(pageNumber, pageSize, orderBy, sortDir);
    }

    @Override
    public List<ResponseScheduleWithEmployee> listScheduleIn() {
        return scheduleServiceOut.listScheduleOut();
    }

    @Override
    public List<ResponseScheduleWithEmployee> listScheduleFromDateIn(LocalDate fromDate) {
        return scheduleServiceOut.listScheduleFromDateOut(fromDate);
    }

    @Override
    public List<ResponseScheduleWithEmployee> listScheduleBetweenDatesIn(LocalDate fromDate, LocalDate toDate) {
        return scheduleServiceOut.listScheduleBetweenDatesOut(fromDate, toDate);
    }

    @Override
    public List<ResponseScheduleWithEmployee> listScheduleBySpecificDateIn(LocalDate scheduleDate) {
        return scheduleServiceOut.listScheduleBySpecificDateOut(scheduleDate);
    }
}
