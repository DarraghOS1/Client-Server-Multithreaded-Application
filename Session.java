package model;

import java.time.Duration;
import java.time.LocalTime;
import java.time.DayOfWeek;

public class Session {
    private DayOfWeek day;
    private LocalTime startTime;
    private LocalTime endTime;
    private String room;
    private String className;
    private String description;

    /**
     * Constructor for Session class
     * @param s A String in the format "MONDAY 10:00 11:00 S115 LM121 LAB"
     */
    public Session(String s){
        String[] parts = s.split(" ");
        int stringLength = parts.length;
        if(stringLength != 6){
            throw new IllegalArgumentException("Wrong string format passed");
        }
        day = DayOfWeek.valueOf(parts[0].toUpperCase());
        startTime = LocalTime.parse(parts[1]);
        endTime = LocalTime.parse(parts[2]);
        room = parts[3].toUpperCase();
        className = parts[4].toUpperCase();
        description = parts[5].toUpperCase();
    }

    /**
     * Constructor for Session class
     * @param day DayOfWeek day of Session
     * @param startTime LocalTime start time of Session
     * @param endTime LocalTime end time of Session
     * @param room String room Session takes place in
     * @param className String class name for Session
     * @param description String Session type; Lecture, Lab or Tutorial
     */
    public Session(DayOfWeek day, LocalTime startTime, LocalTime endTime, String room, String className, String description){
        this.day = day;
        this.startTime = startTime;
        this.endTime = endTime;
        this.room = room;
        this.className = className;
        this.description=description;
    }

    /**
     * Checks to see if two model.Session are equal
     * @param other An object which will be parsed to a Session
     * @return True or false depending on if they are equal
     */
    public boolean equals(Object other) {
        if (other == null || !(other instanceof Session)) {
            return false;
        }
        Session b = (Session) other;
        return day.equals(b.day) && startTime.equals(b.startTime) && endTime.equals(b.endTime)
                && room.equals(b.room) && className.equals(b.className);
    }

    /**
     * Checks to see if two sessions are equal, besides their class name and description
     * @param other An object which will be parsed to a Session
     * @return True or false depending on if they are equal, besides their class name and description
     */
    public boolean equalExcludeClassNameAndDescription(Object other) {
        if (other == null || !(other instanceof Session)) {
            return false;
        }
        Session b = (Session) other;
        return day.equals(b.day) && startTime.equals(b.startTime) && endTime.equals(b.endTime)
                && room.equals(b.room);
    }

    /**
     * Formats the Session into a String
     * @return String of Session
     */
    public String format(){
        return day.toString() + " " + startTime.toString() + " "
                + endTime.toString() + " " + room + " " + className + " " + description;
    }

    /**
     * Formats the Session into a String, excluding class name
     * @return String of model.Session, excluding class name
     */
    public String formatTimeSlot(){
        return day.toString() + " " + startTime.toString() + " "
                + endTime.toString() + " " + room;
    }
    public DayOfWeek getDay(){
        return day;
    }
    public LocalTime getStartTime(){
        return  startTime;
    }
    public LocalTime getEndTime(){
        return endTime;
    }
    public String getRoom(){
        return room;
    }
    public String getClassName(){
        return className;
    }
    public String getDescription(){return description;}

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }
    public void setEndTime(LocalTime endTime){
        this.endTime =endTime;
    }

    public Duration getSessionLength(){
        return Duration.between(startTime,endTime);
    }

}
