package com.example.gcccyclingmobileapp;

public class EventTypeInfo {
    private String eventId; // Unique identifier for the event
    private String eventType; // Type of the event (e.g., "RoadRace", "TimeTrial")
    private String eventDescription; // Description of the event
    private int ageRequirement; // Minimum age requirement for participants
    private String pace; // Pace of the event (e.g., "Slow," "Medium," "Fast")
    private String level; // Skill level of the event (e.g., "Beginner," "Intermediate," "Advanced")


    public EventTypeInfo() {
    }

    public EventTypeInfo(String eventId, String eventType, String eventDescription, int ageRequirement, String pace, String level) {
        this.eventId = eventId;
        this.eventType = eventType;
        this.eventDescription = eventDescription;
        this.ageRequirement = ageRequirement;
        this.pace = pace;
        this.level = level;
    }

    public String getEventId() {
        return eventId;
    }

    public String getEventType() {
        return eventType;
    }

    public String getEventDescription() {
        return eventDescription;
    }

    public int getAgeRequirement() {
        return ageRequirement;
    }

    public String getPace() {
        return pace;
    }

    public String getLevel() {
        return level;
    }
}


