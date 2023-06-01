package com.example.licentatakecare.Authentication;

import com.google.firebase.database.PropertyName;

public class LogEntry {
    @PropertyName("entry_timestamp")
    private String entryTimestamp;
    @PropertyName("exit_timestamp")
    private String exitTimestamp;
    @PropertyName("section")
    private String section;
    @PropertyName("hospital")
    private String hospital;
    private String timeDifference;

    public String getTimeDifference() {
        return timeDifference;
    }

    public void setTimeDifference(String time) {
        this.timeDifference = time;
    }

    public LogEntry(String entryTimestamp, String exitTimestamp, String section, String hospital, String time) {
        this.entryTimestamp = entryTimestamp;
        this.exitTimestamp = exitTimestamp;
        this.section = section;
        this.hospital = hospital;
        this.timeDifference=time;
    }

    public String getEntryTimestamp() {
        return entryTimestamp;
    }

    public void setEntryTimestamp(String entryTimestamp) {
        this.entryTimestamp = entryTimestamp;
    }

    public String getExitTimestamp() {
        return exitTimestamp;
    }

    public void setExitTimestamp(String exitTimestamp) {
        this.exitTimestamp = exitTimestamp;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public String getHospital() {
        return hospital;
    }

    public void setHospital(String hospital) {
        this.hospital = hospital;
    }
}

