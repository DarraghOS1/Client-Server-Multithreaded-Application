package model;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.*;
public class SessionScheduler {
    private ArrayList<Session> schedule;

    /**
     * Constructor for the SessionScheduler
     */
    public SessionScheduler() {
        schedule = new ArrayList<>();
    }

    /**
     * Adds a session to the class if there is no clashes
     * @param s Session to be added
     * @return Returns true or false depending on if the Session was added successfully
     */
    public boolean add(Session s) {
        // Check for conflicts before adding
        for (Session session : schedule) {
            if (conflicts(session, s)) {
                // Handle conflict
                return false;
            }
        }

        schedule.add(s);

        sortSchedule();
        return true;
    }

    /**
     * Removes the Session from the server
     * @param s Session to be removed
     */
    public boolean remove(Session s) {
        boolean removed = false;
        for (Session session : schedule) {
            if (session.equalExcludeClassNameAndDescription(s)) {
                schedule.remove(session);
                removed = true;
                break;
            }
        }
        return removed;
    }

    /**
     * Checks to see if there is a schedule clash
     * @param existingSession The Session already stored in the server
     * @param newSession The Session to be added
     * @return True or false depending on if there is a clash or not
     */
    public boolean conflicts(Session existingSession, Session newSession) {
        if (!existingSession.getDay().equals(newSession.getDay())) {
            return false;
        }

        LocalTime existingSessionStartTime = existingSession.getStartTime();
        LocalTime existingSessionEndTime = existingSession.getEndTime();
        LocalTime newSessionStartTime = newSession.getStartTime();
        LocalTime newSessionEndTime = newSession.getEndTime();

        boolean timeOverlap = existingSessionStartTime.isBefore(newSessionEndTime) &&
                newSessionStartTime.isBefore(existingSessionEndTime);

        if (existingSession.getRoom().equals(newSession.getRoom()) && timeOverlap) {
            return true;
        }

        boolean sameClassName = existingSession.getClassName().equalsIgnoreCase(newSession.getClassName());
        boolean differentRooms = !existingSession.getRoom().equalsIgnoreCase(newSession.getRoom());

        if (timeOverlap && sameClassName && differentRooms) {
            return true;
        }

        return false;
    }

    /**
     * Sorts the schedule based on the day and start times of the Sessions
     */
    public void sortSchedule() {
        Collections.sort(schedule, new Comparator<Session>() {
            @Override
            public int compare(Session s1, Session s2) {
                DayOfWeek day1 = s1.getDay();
                DayOfWeek day2 = s2.getDay();
                if (day1 != day2) {
                    return day1.compareTo(day2);
                }
                return s1.getStartTime().compareTo(s2.getStartTime());
            }
        });
    }

    /**
     * Returns the ArrayList of the Sessions
     * @return ArrayList of Sessions
     */
    public ArrayList<Session> getListSchedule(){
        return schedule;
    }

    /**
     * Returns a String containing all the schedule's Sessions
     * @return String containing all the schedule's Sessions
     */
    public String getSchedule() {
        StringBuilder builder = new StringBuilder();
        int size = schedule.size();
        for (int i = 0; i < size; i++) {
            builder.append(schedule.get(i).format());
            if (i < size - 1) {
                builder.append(", ");
            }
        }
        return builder.toString();
    }

    /**
     * Returns a String containing all the Sessions stored under a specific class name
     * @param className String of the class name
     * @return String containing all the Sessions stored under a specific class name
     */
    public String getSchedule(String className){
        ArrayList<Session> classSession = new ArrayList<>();
        StringBuilder builder = new StringBuilder();
        int size = schedule.size();
        for (int i = 0; i < size; i++) {
            if(schedule.get(i).getClassName().compareTo(className) == 0) {
                builder.append(schedule.get(i).format());
                if (i < size - 1) {
                    builder.append(", ");
                }
            }
        }
        return builder.toString();
    }

    /**
     * Separates the Sessions into separate lists of Sessions based on days
     * @return Map of DayOfWeek and Sessions
     */
    public  Map<DayOfWeek, ArrayList<Session>> groupSessionsByDay() {
        Map<DayOfWeek, ArrayList<Session>> sessionsByDay = new HashMap<>();

        for (DayOfWeek day : DayOfWeek.values()) {
            sessionsByDay.put(day, new ArrayList<>());
        }

        for (Session session : schedule) {
            sessionsByDay.get(session.getDay()).add(session);
        }

        return sessionsByDay;
    }
}
