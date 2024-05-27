package controller;
import model.Session;
import model.SessionScheduler;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;
public class SessionController {
    private SessionScheduler scheduler;

    /**
     * Constructor for the SessionController
     * @param scheduler
     */
    public SessionController(SessionScheduler scheduler){
        this.scheduler=scheduler;
    }

    /**
     * Handles the identifier of a message received
     * @param identifier String identifier to determine correct action to take
     * @param message String message with the relative information that each action needs
     * @return Appropriate response from the server
     * @throws IncorrectActionException If message received is not in the correct format
     */
    public synchronized String handleIdentifier(String identifier, String message) throws IncorrectActionException {
        String response="";
        switch (identifier) {
            case "ADD":
                response = handleAdd(message);
                break;
            case "REMOVE":
                response = handleRemove(message);
                break;
            case "DISPLAY":
                response = handleDisplay(message);
                break;
            case "STOP":
                response = handleStop();
                break;
            case "EARLY_LECTURES":
                response = handleEarlyLectures(message);
                break;
        }
        return response;
    }

    /**
     * Handles the 'ADD' case for adding a Session
     * @param remainder String format of a Session to be added
     * @return Response depending on if the addition was successful or not
     * @throws IncorrectActionException
     */
    private synchronized String handleAdd(String remainder) throws IncorrectActionException {
        Session session = parseSession(remainder);
        boolean added = scheduler.add(session);
        if (added)
            return "SUCCESS Class scheduled successfully";
        else
            return "ERROR There is already a class booked for this time slot";

    }

    /**
     * Handles the 'REMOVE' case for removing a Session
     * @param remainder String format of a Session to be removed
     * @return Response depending on if the removal was successful or not
     * @throws IncorrectActionException
     */
    private synchronized String handleRemove(String remainder) throws IncorrectActionException {
            Session session = parseSession(remainder + " a a");//" a a" included for parsing purposes, has no effect on remove method
            boolean removed = scheduler.remove(session);
           if(removed)
               return "SUCCESS The freed time slot is " + session.formatTimeSlot();
           else
               return "ERROR There is no class booked " + session.formatTimeSlot();

    }

    /**
     * Handles the 'DISPLAY' case
     * @param message String either 'ALL' or a specific class name
     * @return String containing Sessions with display prefix or ERROR message if there are no scheduled sessions
     * @throws IncorrectActionException
     */
    private synchronized String handleDisplay(String message) throws IncorrectActionException {
        if (message.equals("ALL")) {
            if(scheduler.getSchedule().isEmpty())
                return "ERROR There are no scheduled Sessions";
            else {
                System.out.println(scheduler.getSchedule());
                String schedule = scheduler.getSchedule();
                return "DISPLAY ALL;" + schedule;
            }
        } else {
            boolean contains = false;
            for (Session s : scheduler.getListSchedule()) {
                if (s.getClassName().equals(message)) {
                    contains = true;
                    break;
                }
            }
            if (!contains)
                return "ERROR There are no scheduled Sessions";

            System.out.println(scheduler.getSchedule(message));
            String schedule = scheduler.getSchedule(message);
            return "DISPLAY " + message + ";" + schedule;
        }
    }

    /**
     * Handles the 'STOP' case
     * @return String message 'TERMINATE'
     */
    private String handleStop() {
        return "TERMINATE";
    }

    /**
     * Handles the 'EARLY LECTURES' case for either 'ALL' or a specific class
     * @param message String 'ALL' or a specific class name
     * @return String response depending on success or not
     */
    private synchronized String handleEarlyLectures(String message){
        ForkJoinPool pool = new ForkJoinPool();
        for (DayOfWeek day : DayOfWeek.values()) {
            pool.invoke(new EarlyLecturesTask(day, scheduler,message));
        }
        scheduler.sortSchedule();
        return "SUCCESS Lectures shifted to earliest available times";
    }

    /**
     * Parse a String format of Session into type Session
     * @param remainder String format of a Session
     * @return Session
     * @throws IncorrectActionException
     */
    private Session parseSession(String remainder) throws IncorrectActionException {
        try {
            String[] remainderSplit = remainder.split(" ");
            if (remainderSplit.length < 6) {
                throw new IncorrectActionException();
            }
            DayOfWeek day = DayOfWeek.valueOf(remainderSplit[0].toUpperCase());
            LocalTime startTime = LocalTime.parse(remainderSplit[1]);
            LocalTime endTime = LocalTime.parse(remainderSplit[2]);
            String room = remainderSplit[3].toUpperCase();
            String className = remainderSplit[4].toUpperCase();
            String description = remainderSplit[5].toUpperCase();
            if (room.isEmpty() || className.isEmpty()) {
                throw new IncorrectActionException();
            }
            if(startTime.isBefore(LocalTime.of(9,0)) || endTime.isAfter(LocalTime.of(18,0))){
                throw new IncorrectActionException();
            }

            return new Session(remainder);
        } catch (IllegalArgumentException | DateTimeParseException | ArrayIndexOutOfBoundsException e) {
            throw new IncorrectActionException();
        }
    }


    private static class EarlyLecturesTask extends RecursiveAction {
        private DayOfWeek day;
        private SessionScheduler scheduler;
        private String message;

        /**
         * Constructor for the EarlyLecturesTask
         * @param day DayOfWeek day
         * @param scheduler SessionScheduler scheduler
         * @param message String 'ALL' or class name
         */
        public EarlyLecturesTask(DayOfWeek day, SessionScheduler scheduler,String message) {
            this.day = day;
            this.scheduler = scheduler;
            this.message=message;
        }

        @Override
        protected void compute() {
            ArrayList<Session> sessions = scheduler.groupSessionsByDay().get(day);

            if (sessions == null)
                return;
            if(message.equals("ALL")){
                for (Session session : sessions) {
                    shiftToEarlyMorning(session, sessions);
                }
            }else{
                for (Session session : sessions) {
                    if(session.getClassName().toUpperCase().equals(message))
                        shiftToEarlyMorning(session, sessions);
                }
            }

        }

        /**
         * Shifts all or a specific class' lectures to the earliest possible times checking for conflicts
         * @param session Session session
         * @param sessions ArrayList<Session> sessions
         */
        private void shiftToEarlyMorning(Session session, ArrayList<Session> sessions) {
            int earliestHour = 9;  // Earliest possible start time
            LocalTime earliestStart = LocalTime.of(earliestHour, 0);
            int durationHours = session.getSessionLength().toHoursPart();  // Session duration in hours

            LocalTime potentialStartTime = earliestStart;
            boolean shifted = false;

            while (potentialStartTime.isBefore(session.getStartTime()) && !shifted) {
                LocalTime potentialEndTime = potentialStartTime.plusHours(durationHours);

                boolean hasConflict = false;
                Session potentialSession = new Session(day, potentialStartTime, potentialEndTime,
                        session.getRoom(), session.getClassName(), session.getDescription());
                for (Session existingSession : sessions) {
                    if (!existingSession.equals(session) && scheduler.conflicts(existingSession, potentialSession)) {
                        hasConflict = true;
                        break;
                    }
                }

                if (!hasConflict) {
                    session.setStartTime(potentialStartTime);
                    session.setEndTime(potentialEndTime);
                    shifted = true;
                } else {

                    potentialStartTime = potentialStartTime.plusHours(1);
                }
            }
        }
    }
}
