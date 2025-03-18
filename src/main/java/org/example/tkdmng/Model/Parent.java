package org.example.Model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Represents a parent in the TKD management system
 */
public class Parent extends Person{
    List<Integer> children = new ArrayList<>();

    /**
     * Constructs a new Parent with the specified ID, name, last name, email, address, date of birth and number.
     * @param id            The unique identifier of the person.
     * @param name          The name of the person
     * @param lastName      The last name of the person.
     * @param email         The email of the person.
     * @param address       The address of the person.
     * @param number        The telephone number of the person.
     */
    public Parent(Integer id,String name, String lastName, String email, String address,String number) {
        super(id,name, lastName, email, address, number);
    }
    public Parent(){}
    /**
     * Gets the list of children of the parent.
     * @return The list of children of the parent.
     */
    public List<Integer> getChildren() {
        return children;
    }

    /**
     * Sets the list of children of the parent.
     * @param children  The list of children of the parent to set.
     */
    public void setChildren(List<Integer> children) {
        this.children = children;
    }

    @Override
    public String toString() {
        return "Parent{" +
                "children=" + children +
                ", name='" + name + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", address='" + address + '\'' +
                ", dateOfBirth=" + dateOfBirth +
                ", number='" + number + '\'' +
                ", beltLevel='" + beltLevel + '\'' +
                '}';
    }

    public String toString2() {
        // Coduri ANSI pentru culori
        final String ANSI_CYAN = "\u001B[36m";
        final String ANSI_YELLOW = "\u001B[33m";
        final String ANSI_GREEN = "\u001B[32m";
        final String ANSI_RED = "\u001B[31m";
        final String ANSI_RESET = "\u001B[0m";

        return ANSI_CYAN + "👪 Parent Details:" + ANSI_RESET + "\n" +
                ANSI_YELLOW + "  Name: " + ANSI_RESET + name + " " + lastName + "\n" +
                ANSI_GREEN + "  Email: " + ANSI_RESET + email + "\n" +
                ANSI_GREEN + "  Phone: " + ANSI_RESET + number + "\n" +
                ANSI_GREEN + "  Address: " + ANSI_RESET + address + "\n";
    }

    @Override
    public String[] getHeader() {
        return new String[]{"id", "name", "lastName", "email", "address", "number","children"};
    }

    @Override
    public String toCSV() {
        String childrenToCSV = this.getChildren().stream().map(String::valueOf).collect(Collectors.joining(";"));
        return String.join(",",String.valueOf(this.getId()),this.getName(),this.getLastName(),this.getEmail(),this.getAddress(),this.getNumber(),childrenToCSV);
    }

    public static Parent fromCSV(String csv) {
        String[] parts = csv.split(",",-1);
        Parent p = new Parent(Integer.parseInt(parts[0]),parts[1],parts[2],parts[3],parts[4],parts[5]);
        List<Integer> childrenList =  parts[6].isEmpty() ? new ArrayList<>() : Arrays.stream(parts[6].split(";")).map(Integer::parseInt).collect(Collectors.toList());
        p.setChildren(childrenList);
        return p;
    }
}
