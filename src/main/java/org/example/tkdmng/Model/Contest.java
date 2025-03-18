package org.example.Model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Represents a contest in the TKD management system
 */
public class Contest extends Event{

    public String name;
    List<Integer> students = new ArrayList<>();

    /**
     * Constructs a new Contest with the specified ID, start date, end date, price, country, city, name and address.
     * @param id            The unique identifier of the event.
     * @param startDate     The start date of the event.
     * @param endDate       The end date of the event.
     * @param price         The price of the event.
     * @param country       The country where the event takes place.
     * @param city          The city where the event takes place.
     * @param name          The name of the contest.
     * @param address       The address where the event takes place.
     */
    public Contest(int id,String startDate, String endDate, double price, String country, String city, String name, String address) {
        super(id,startDate, endDate, price, country, city, address);
        this.name = name;
    }

    public Contest(){}
    /**
     * Gets the name of the contest.
     * @return The name of the contest.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the contest.
     * @param name  The name of the contest to set.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the list of student ids registered to the contest.
     * @return The list of student ids registered to the contest.
     */
    public List<Integer> getStudents() {
        return students;
    }

    /**
     * Sets the list of student ids registered to the contest.
     * @param students  The list of student ids registered to the contest to set.
     */
    public void setStudents(List<Integer> students) {
        this.students = students;
    }

    /**
     * default string for print out a Contest.
     * @return String representation of object.
     */
    @Override
    public String toString() {
        return "Contest{" +
                "name='" + name + '\'' +
                ", students=" + students +
                ", startDate='" + startDate + '\'' +
                ", endDate='" + endDate + '\'' +
                ", price=" + price +
                ", country='" + country + '\'' +
                ", city='" + city + '\'' +
                ", address='" + address + '\'' +
                '}';
    }



    /**
     * custom string for print out a Contest.
     * @return String representation of object.
     */
    public String toString2() {
        // Coduri ANSI pentru culori
        final String ANSI_YELLOW = "\u001B[33m";
        final String ANSI_CYAN = "\u001B[36m";
        final String ANSI_GREEN = "\u001B[32m";
        final String ANSI_RESET = "\u001B[0m";

        return ANSI_CYAN + " Contest Details: " + ANSI_RESET + "\n" +
                ANSI_YELLOW + "  Name: " + ANSI_RESET + name + "\n" +
                ANSI_GREEN + "  Price: " + ANSI_RESET + price + " lei" + "\n"+
                ANSI_GREEN + "  Starting date: " + ANSI_RESET +  startDate;
    }

    @Override
    public String[] getHeader() {
        return new String[]{"id", "startDate", "endDate", "price", "country", "city","name", "address","students"};
    }

    @Override
    public String toCSV() {
        String studentsToCSV = this.students.stream().map(String::valueOf).collect(Collectors.joining(";"));
        return String.join(",", String.valueOf(this.getId()),this.getStartDate(),this.getEndDate(),String.valueOf(this.getPrice()),this.getCountry(),this.getCity(),this.getName(),this.getAddress(),studentsToCSV);
    }

    public static Contest fromCSV(String csv) {
        String[] values = csv.split(",",-1);
        Contest contest = new Contest(Integer.valueOf(values[0]),values[1],values[2],Double.parseDouble(values[3]),values[4],values[5],values[6],values[7]);
        List<Integer> students = values[8].isEmpty() ? new ArrayList<>() : Arrays.stream(values[8].split(";")).map(Integer::parseInt).collect(Collectors.toList());
        contest.setStudents(students);
        return contest;
    }
}
