package com.projectpulse.dto.request;

import java.time.LocalDate;
import java.util.List;

public class SetupActiveWeeksRequest {
    private List<LocalDate> inactiveWeekStarts;

    public List<LocalDate> getInactiveWeekStarts() { return inactiveWeekStarts; }
    public void setInactiveWeekStarts(List<LocalDate> inactiveWeekStarts) { this.inactiveWeekStarts = inactiveWeekStarts; }
}
