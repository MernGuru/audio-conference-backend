package com.brh.entities.cores;

import java.time.LocalDate;

public class DurationData {
    private String from_date;

    public String getFrom_date() {
        return from_date;
    }

    public void setFrom_date(String from_date) {
        this.from_date = from_date;
    }

    public String getEnd_date() {
        return end_date;
    }

    public void setEnd_date(String end_date) {
        this.end_date = end_date;
    }

    private String end_date;
}
