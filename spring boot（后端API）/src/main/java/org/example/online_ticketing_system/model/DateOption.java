package org.example.online_ticketing_system.model;

public class DateOption {
    private final String date;
    private final String label;
    private final boolean available;
    private final String morning;
    private final String morning_avai;
    private final String afternoon;
    private final String afternoon_avai;

    public DateOption(String date, String label, boolean available, String morning, String morningAvai, String afternoon, String afternoonAvai) {
        this.date = date;
        this.label = label;
        this.available = available;
        this.morning = morning;
        this.morning_avai = morningAvai;
        this.afternoon = afternoon;
        this.afternoon_avai = afternoonAvai;
    }

    public String getDate() {
        return date;
    }

    public String getLabel() {
        return label;
    }

    public boolean isAvailable() {
        return available;
    }

    public String getMorning() {
        return morning;
    }

    public String getAfternoon() {
        return afternoon;
    }

    public String getMorning_avai() {
        return morning_avai;
    }

    public String getAfternoon_avai() {
        return afternoon_avai;
    }
}
