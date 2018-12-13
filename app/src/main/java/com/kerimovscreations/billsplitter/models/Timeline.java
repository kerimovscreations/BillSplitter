package com.kerimovscreations.billsplitter.models;

import com.kerimovscreations.billsplitter.activities.enums.StatisticsPeriod;

import java.util.Date;

public class Timeline {

    private Date startDate, endDate;
    private String name;

    private StatisticsPeriod period;

    public Timeline(Date startDate, Date endDate, String name, StatisticsPeriod period) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.name = name;
        this.period = period;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public StatisticsPeriod getPeriod() {
        return period;
    }
}
