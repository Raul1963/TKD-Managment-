package org.example.Model;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

/**
 * Represents the date that a session took place in the TKD management system
 */
public class SessionDate {
    public String weekDay;
    public String date;
    public int session;

    public boolean attended;


    /**
     * Constructs a new Session date with the specified weekday, date and session.
     * @param weekDay   The day of the week the session took place.
     * @param date      The exact date the session took place.
     * @param session   The session itself that was on a day.
     * @param attended  The attendance as boolean.
     */
    public SessionDate(String weekDay, String date, int session,boolean attended) {
        this.weekDay = weekDay;
        this.date = date;
        this.session = session;
        this.attended = attended;
    }
    public SessionDate(){}
    /**
     * Gets the weekday of the session.
     * @return The  weekday of the session.
     */
    public String getWeekDay() {
        return weekDay;
    }

    /**
     * Sets the weekday of the session.
     * @param weekDay  The weekday of the session to set.
     */
    public void setWeekDay(String weekDay) {
        this.weekDay = weekDay;
    }

    /**
     * Gets the date of the session.
     * @return The date of the session.
     */
    public String getDate() {
        return date;
    }

    /**
     * Sets the date of the session.
     * @param date  The date of the session to set.
     */
    public void setDate(String date) {
        this.date = date;
    }

    /**
     * Gets the session from the date.
     * @return The session from the date.
     */
    public int getSession() {
        return session;
    }

    /**
     * Sets the session from the date.
     * @param session  The session from the date to set.
     */
    public void setSession(int session) {
        this.session = session;
    }

    /**
     * Gets the attended bool from the session date.
     * @return The attended bool from the session date.
     */
    public boolean isAttended() {
        return attended;
    }

    /**
     * Sets the attended bool to the session date.
     * @param attended The attended boll to set in session date.
     */
    public void setAttended(boolean attended) {
        this.attended = attended;
    }

    /**
     * default string for print out a Session date.
     * @return String representation of object.
     */
    @Override
    public String toString() {
        return weekDay + ":" + date + ":" + session + ":" + attended;
    }

    /**
     * custom string for print out a Session date.
     * @return String representation of object.
     */
    public String toString2() {
        // Coduri ANSI pentru culori
        final String ANSI_GREEN = "\u001B[32m";
        final String ANSI_YELLOW = "\u001B[33m";
        final String ANSI_CYAN = "\u001B[36m";
        final String ANSI_RESET = "\u001B[0m";

        return ANSI_CYAN + "📅 Session Date Details:" + ANSI_RESET + "\n" +
                ANSI_GREEN + "  Weekday: " + ANSI_RESET + weekDay + "\n" +
                ANSI_YELLOW + "  Date: " + ANSI_RESET + date + "\n" +
                ANSI_CYAN + "  Session: " + ANSI_RESET + session;
    }
    public static SessionDate fromString(String data) {
        String[] parts = data.split(":");
        String weekDay = parts[0];
        String date = parts[1];
        int session = Integer.parseInt(parts[2]);
        boolean attended = Boolean.parseBoolean(parts[3]);
        return new SessionDate(weekDay, date, session, attended);
    }
}
