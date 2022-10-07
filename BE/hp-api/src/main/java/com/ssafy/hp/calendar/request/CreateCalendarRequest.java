package com.ssafy.hp.calendar.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateCalendarRequest {
    private Integer exerciseId;

    private Integer pillId;

    private String calendarContent;

    private Integer calendarDate;

    private LocalTime calendarTime;
}
