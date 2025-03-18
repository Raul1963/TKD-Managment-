package org.example.Model;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Represents a belt exam in the TKD management system
 */
public class BeltExam extends Event{

    public Map< Integer,Integer> listOfResults = new HashMap<>();// 1: passed, 0: failed, -1: absent
    public BeltLevel beltColor;

    /**
     * Constructs a new belt exam with the specified ID, start date, end date, price, country, city, address and belt color.
     * @param id            The unique identifier of the event.
     * @param startDate     The start date of the event.
     * @param endDate       The end date of the event.
     * @param price         The price of the event.
     * @param country       The country where the event takes place.
     * @param city          The city where the event takes place.
     * @param address       The address where the event takes place.
     * @param beltColor     The belt color for which the exam is taken.
     */
    public BeltExam(int id,String startDate, String endDate, double price, String country, String city, String address, BeltLevel beltColor) {
        super(id,startDate, endDate, price, country, city, address);
        this.beltColor = beltColor;
    }

    public BeltExam(){}
    /**
     * Gets the list of results from the exam.
     * @return The list of results from the exam as a map of student id and int.
     * The int can be -1( did not participate/exam hasn't taken place yet), 0( failed ) and 1( passed ).
     */
    public Map<Integer, Integer> getListOfResults() {
        return listOfResults;
    }

    /**
     * Sets the list of results from the exam.
     * @param listOfResults  The list of results from the exam to set as a map of student id and int.
     * The int can be -1( did not participate/exam hasn't taken place yet), 0( failed ) and 1( passed ).
     */
    public void setListOfResults(Map<Integer, Integer> listOfResults) {
        this.listOfResults = listOfResults;
    }

    /**
     * Gets the belt color from the exam.
     * @return The belt color from the exam.
     */
    public BeltLevel getBeltColor() {
        return beltColor;
    }

    /**
     * Sets the belt color from the exam.
     * @param beltColor  The belt color from the exam to set.
     */
    public void setBeltColor(BeltLevel beltColor) {
        this.beltColor = beltColor;
    }

    @Override
    public String toString() {
        return "BeltExam{" +
                "listOfResults=" + listOfResults +
                ", BeltColor='" + beltColor + '\'' +
                ", startDate='" + startDate + '\'' +
                ", endDate='" + endDate + '\'' +
                ", price=" + price +
                ", country='" + country + '\'' +
                ", city='" + city + '\'' +
                ", address='" + address + '\'' +
                '}';
    }

    /**
     * prints out a belt exam
     */

    public String toString2() {
        // Coduri ANSI pentru culori
        final String ANSI_RED = "\u001B[31m";
        final String ANSI_BLUE = "\u001B[34m";
        final String ANSI_GREEN = "\u001B[32m";
        final String ANSI_RESET = "\u001B[0m";

        return ANSI_BLUE + "🥋 Belt Exam Details:" + ANSI_RESET + "\n" +
                ANSI_GREEN + "  Start Date: " + ANSI_RESET + ANSI_RED + startDate + ANSI_RESET + "\n" +
                ANSI_GREEN + "  Country: " + ANSI_RESET + country + "\n" +
                ANSI_GREEN + "  City: " + ANSI_RESET + city;
    }


    @Override
    public String[] getHeader() {
        return new String[]{"id", "startDate", "endDate", "price", "country", "city", "address", "beltColor","listOfResults"};
    }

    @Override
    public String toCSV() {
        String listOfResultsToCSV = this.getListOfResults().entrySet().stream()
                .map(entry -> entry.getKey() + ":" + entry.getValue())
                .collect(Collectors.joining(";"));
        return String.join(",",String.valueOf(this.getId()),this.getStartDate(),this.getEndDate(),String.valueOf(this.getPrice()),
                this.getCountry(),this.getCity(),this.getAddress(), String.valueOf(this.getBeltColor()),listOfResultsToCSV);
    }

    public static BeltExam fromCSV(String csv) {
        String[] values = csv.split(",",-1);
        BeltExam beltExam = new BeltExam(Integer.parseInt(values[0]),values[1],values[2],Double.parseDouble(values[3]),values[4],values[5],values[6],BeltLevel.valueOf(values[7]));
        Map<Integer, Integer> listOfResults = new HashMap<>();
        if (!values[8].isEmpty()) {
            String[] entries = values[8].split(";");
            for (String entry : entries) {
                String[] keyValue = entry.split(":");
                Integer key = Integer.parseInt(keyValue[0]);
                Integer value = Integer.parseInt(keyValue[1]);
                listOfResults.put(key, value);
            }
        }
        beltExam.setListOfResults(listOfResults);
        return beltExam;
    }
}
