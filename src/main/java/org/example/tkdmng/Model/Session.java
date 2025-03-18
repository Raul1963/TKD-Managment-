package org.example.Model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Represents a session in the TKD management system
 */
public class Session implements HasID{
    public Integer id;
    public DifficultyLevel difficultyLevel;
    public int maximumParticipants;
    public int trainer;
    public double pricePerSession;

    private List<Integer> sessionStudents = new ArrayList<>();

    public List<Integer> getSessionStudents() {
        return sessionStudents;
    }

    public void setSessionStudents(List<Integer> sessionStudents) {
        this.sessionStudents = sessionStudents;
    }

    /**
     * Constructs a new Session with the specified ID, difficulty level, maximum participants, trainer, and price per session.
     * @param id                    The unique identifier of the session.
     * @param difficultyLevel       The difficulty level of the session( beginner, intermediary, advanced).
     * @param maximumParticipants   The max number of students that can be part of a session.
     * @param trainer               The trainer who manages the session and it's students.
     * @param pricePerSession       The price of one session.
     */
    public Session(Integer id , DifficultyLevel difficultyLevel, int maximumParticipants, int trainer, double pricePerSession) {
        this.id=id;
        this.difficultyLevel = difficultyLevel;
        this.maximumParticipants = maximumParticipants;
        this.trainer = trainer;
        this.pricePerSession = pricePerSession;
    }

    public Session(){}
    /**
     * Gets the id of the session.
     * @return The id of the session.
     */
    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public String[] getHeader() {
        return new String[]{"id", "difficultyLevel", "maximumParticipants", "trainer", "pricePerSession", "sessionStudents"};
    }

    @Override
    public String toCSV() {
        String sessionStudentsToCSV = this.getSessionStudents().stream().map(String::valueOf).collect(Collectors.joining(";"));
        return String.join(",", String.valueOf(this.getId()),String.valueOf(this.getDifficultyLevel()),String.valueOf(this.getMaximumParticipants()),
                String.valueOf(this.getTrainer()),String.valueOf(this.getPricePerSession()),sessionStudentsToCSV);
    }

    public static Session fromCSV(String csv) {
        String[] parts = csv.split(",",-1);
        Session s = new Session(Integer.parseInt(parts[0]),DifficultyLevel.valueOf(parts[1]),Integer.parseInt(parts[2]),Integer.parseInt(parts[3]),Double.parseDouble(parts[4]));
        List<Integer> sessionStudentsList =  parts[5].isEmpty() ? new ArrayList<>() : Arrays.stream(parts[5].split(";")).map(Integer::parseInt).collect(Collectors.toList());
        s.setSessionStudents(sessionStudentsList);
        return s;
    }

    /**
     * Sets the id of the session.
     * @param id  The id of the session to set.
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Gets the difficulty level of the session.
     * @return The difficulty level of the session.
     */
    public DifficultyLevel getDifficultyLevel() {
        return difficultyLevel;
    }

    /**
     * Sets the difficulty level of the session.
     * @param difficultyLevel  The difficulty level of the session to set.
     */
    public void setDifficultyLevel(DifficultyLevel difficultyLevel) {
        this.difficultyLevel = difficultyLevel;
    }

    /**
     * Gets the maximum number of participants of the session.
     * @return The maximum number of participants of the session.
     */
    public int getMaximumParticipants() {
        return maximumParticipants;
    }

    /**
     * Sets the maximum number of participants of the session.
     * @param maximumParticipants  The maximum number of participants of the session to set.
     */
    public void setMaximumParticipants(int maximumParticipants) {
        this.maximumParticipants = maximumParticipants;
    }

    /**
     * Gets the trainer of the session.
     * @return The trainer of the session.
     */
    public int getTrainer() {
        return trainer;
    }

    /**
     * Sets the trainer of the session.
     * @param trainer  The trainer of the session to set.
     */
    public void setTrainer(int trainer) {
        this.trainer = trainer;
    }

    /**
     * Gets the price per session.
     * @return The price per session.
     */
    public double getPricePerSession() {
        return pricePerSession;
    }

    /**
     * Sets the price per session.
     * @param pricePerSession  The price per session to set.
     */
    public void setPricePerSession(double pricePerSession) {
        this.pricePerSession = pricePerSession;
    }


    @Override
    public String toString() {
        return "Session{" +
                "id=" + id +
                ", difficultyLevel=" + difficultyLevel +
                ", maximumParticipants=" + maximumParticipants +
                ", trainer=" + trainer +
                ", pricePerSession=" + pricePerSession +
                ", sessionStudents=" + sessionStudents +
                '}';
    }

    public String toString2() {
        // Coduri ANSI pentru culori
        final String ANSI_CYAN = "\u001B[36m";
        final String ANSI_YELLOW = "\u001B[33m";
        final String ANSI_GREEN = "\u001B[32m";
        final String ANSI_RED = "\u001B[31m";
        final String ANSI_RESET = "\u001B[0m";

        // Folosim un StringBuilder pentru a construi stringul rezultat
        StringBuilder sb = new StringBuilder();

        sb.append(ANSI_CYAN).append("📅 Session Details:").append(ANSI_RESET).append("\n")
                .append(ANSI_YELLOW).append("  ID: ").append(ANSI_RESET).append(id).append("\n")
                .append(ANSI_GREEN).append("  Difficulty Level: ").append(ANSI_RESET).append(difficultyLevel).append("\n")
                .append(ANSI_GREEN).append("  Max Participants: ").append(ANSI_RESET).append(maximumParticipants).append("\n")
                .append(ANSI_GREEN).append("  Trainer: ").append(ANSI_RESET).append(trainer).append("\n")
                .append(ANSI_GREEN).append("  Price per Session: ").append(ANSI_RESET).append(pricePerSession).append(" lei/h\n")
                .append(ANSI_RED).append("  Students in Session: ").append(ANSI_RESET).append(sessionStudents.size());

        // Apelează toString2 pentru fiecare student din sessionStudents
//        for (Student student : sessionStudents) {
//            sb.append("\n").append(student.toString2()); // Presupunem că există o funcție toString2() în Student
//        }

        return sb.toString();
    }


}
