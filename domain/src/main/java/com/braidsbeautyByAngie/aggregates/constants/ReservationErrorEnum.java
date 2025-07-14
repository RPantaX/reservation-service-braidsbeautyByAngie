package com.braidsbeautyByAngie.aggregates.constants;

import com.braidsbeautybyangie.sagapatternspringboot.aggregates.AppExceptions.TypeException;
import com.braidsbeautybyangie.sagapatternspringboot.aggregates.aggregates.util.GenericError;

public enum ReservationErrorEnum implements GenericError {
    // General Errors
    //RESERVATION
    RESERVATION_NOT_FOUND_ERR00001("ERR00001", "Reservation Not Found", "The requested reservation does not exist.", TypeException.E),
    RESERVATION_ALREADY_EXISTS_ERR00002("ERR00002", "Reservation Already Exists", "A reservation with the same details already exists.", TypeException.E),
    RESERVATION_CREATION_FAILED_ERR00003("ERR00003", "Reservation Creation Failed", "Failed to create the reservation due to an internal error.", TypeException.E),
    RESERVATION_UPDATE_FAILED_ERR00004("ERR00004", "Reservation Update Failed", "Failed to update the reservation due to an internal error.", TypeException.E),
    RESERVATION_DELETION_FAILED_ERR00005("ERR00005", "Reservation Deletion Failed", "Failed to delete the reservation due to an internal error.", TypeException.E),
    RESERVATION_LISTING_FAILED_ERR00006("ERR00006", "Reservation Listing Failed", "Failed to retrieve the list of reservations due to an internal error.", TypeException.E),
    RESERVATION_INVALID_DATA_ERR00007("ERR00007", "Invalid Reservation Data", "The provided reservation data is invalid or incomplete.", TypeException.E),
    //SCHEDULE
    SCHEDULE_NOT_FOUND_ERS00008("ERS00008", "Schedule Not Found", "The requested schedule does not exist.", TypeException.E),
    SCHEDULE_ALREADY_EXISTS_ERS00009("ERS00009", "Schedule Already Exists", "A schedule with the same details already exists.", TypeException.E),
    SCHEDULE_CREATION_FAILED_ERS00010("ERS00010", "Schedule Creation Failed", "Failed to create the schedule due to an internal error.", TypeException.E),
    SCHEDULE_UPDATE_FAILED_ERS00011("ERS00011", "Schedule Update Failed", "Failed to update the schedule due to an internal error.", TypeException.E),
    SCHEDULE_DELETION_FAILED_ERS00012("ERS00012", "Schedule Deletion Failed", "Failed to delete the schedule due to an internal error.", TypeException.E),
    SCHEDULE_LISTING_FAILED_ERS00013("ERS00013", "Schedule Listing Failed", "Failed to retrieve the list of schedules due to an internal error.", TypeException.E),
    SCHEDULE_INVALID_DATA_ERS00014("ERS00014", "Invalid Schedule Data", "The provided schedule data is invalid or incomplete.", TypeException.E),
    //SERVICES
    SERVICE_NOT_FOUND_ERS00015("ERS00015", "Service Not Found", "The requested service does not exist.", TypeException.E),
    SERVICE_ALREADY_EXISTS_ERS00016("ERS00016", "Service Already Exists", "A service with the same details already exists.", TypeException.E),
    SERVICE_CREATION_FAILED_ERS00017("ERS00017", "Service Creation Failed", "Failed to create the service due to an internal error.", TypeException.E),
    SERVICE_UPDATE_FAILED_ERS00018("ERS00018", "Service Update Failed", "Failed to update the service due to an internal error.", TypeException.E),
    SERVICE_DELETION_FAILED_ERS00019("ERS00019", "Service Deletion Failed", "Failed to delete the service due to an internal error.", TypeException.E),
    SERVICE_LISTING_FAILED_ERS00020("ERS00020", "Service Listing Failed", "Failed to retrieve the list of services due to an internal error.", TypeException.E),
    SERVICE_INVALID_DATA_ERS00021("ERS00021", "Invalid Service Data", "The provided service data is invalid or incomplete.", TypeException.E),

    //WORK
    WORK_NOT_FOUND_ERS00022("ERS00022", "Work Not Found", "The requested work does not exist.", TypeException.E),
    WORK_ALREADY_EXISTS_ERS00023("ERS00023", "Work Already Exists", "A work with the same details already exists.", TypeException.E),
    WORK_CREATION_FAILED_ERS00024("ERS00024", "Work Creation Failed", "Failed to create the work due to an internal error.", TypeException.E),
    WORK_UPDATE_FAILED_ERS00025("ERS00025", "Work Update Failed", "Failed to update the work due to an internal error.", TypeException.E),
    WORK_DELETION_FAILED_ERS00026("ERS00026", "Work Deletion Failed", "Failed to delete the work due to an internal error.", TypeException.E),
    WORK_LISTING_FAILED_ERS00027("ERS00027", "Work Listing Failed", "Failed to retrieve the list of works due to an internal error.", TypeException.E),
    WORK_INVALID_DATA_ERS00028("ERS00028", "Invalid Work Data", "The provided work data is invalid or incomplete.", TypeException.E),
    //WARNING Errors
    //SCHEDULE
    SCHEDULE_DATE_REQUIRED_WRS00001("WRS00001", "Schedule Date Required", "The schedule date is required and cannot be empty.", TypeException.W),
    SCHEDULE_HOUR_START_REQUIRED_WRS00002("WRS00002", "Schedule Hour Start Required", "The schedule hour start is required and cannot be empty.", TypeException.W),

    ;
    private ReservationErrorEnum(String code, String title, String message, TypeException type) {
        this.code = code;
        this.title = title;
        this.message = message;
        this.type = type;
    }
    private final String code;
    private final String title;
    private final String message;
    private final TypeException type;


    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public TypeException getType() {
        return type;
    }
}
