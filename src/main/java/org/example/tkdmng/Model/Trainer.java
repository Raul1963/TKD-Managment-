package org.example.Model;
//import com.fasterxml.jackson.annotation.JsonCreator;
//import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents a parent in the TKD management system
 */
public class Trainer extends Person{

    /**
     *  Constructs a new Trainer with the specified ID, name, last name, email, address, date of birth, number and belt level.
     * @param id            The unique identifier of the trainer.
     * @param name          The name of the trainer
     * @param lastName      The last name of the trainer.
     * @param email         The email of the trainer.
     * @param address       The address of the trainer.
     * @param dateOfBirth   The date of birth of the trainer.
     * @param number        The telephone number of the trainer.
     * @param beltLevel     The belt level of the trainer.
     */
    public Trainer(Integer id,String name, String lastName, String email, String address, int dateOfBirth, String number, BeltLevel beltLevel) {
        super(id,name, lastName, email, address, dateOfBirth, number, beltLevel);
    }
    public Trainer(){}

    @Override
    public String toString() {
        return "Trainer{" +
                "name='" + name + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", address='" + address + '\'' +
                ", dateOfBirth=" + dateOfBirth +
                ", number='" + number + '\'' +
                ", beltLevel='" + beltLevel + '\'' +
                '}';
    }

    /**
     * prints out a trainer
     * @return
     */

    public String toString2() {
        // Coduri ANSI pentru culori
        final String ANSI_CYAN = "\u001B[36m";
        final String ANSI_BLUE = "\u001B[34m";
        final String ANSI_GREEN = "\u001B[32m";
        final String ANSI_YELLOW = "\u001B[33m";
        final String ANSI_RESET = "\u001B[0m";

        return ANSI_CYAN + "💪 Trainer Details:" + ANSI_RESET + "\n" +
                ANSI_BLUE + "  Name: " + ANSI_RESET + name + " " + lastName + "\n" +
                ANSI_GREEN + "  Email: " + ANSI_RESET + email + "\n" +
                ANSI_GREEN + "  Address: " + ANSI_RESET + address + "\n" +
                ANSI_GREEN + "  Date of Birth: " + ANSI_RESET + dateOfBirth + "\n" +
                ANSI_GREEN + "  Phone Number: " + ANSI_RESET + number + "\n" +
                ANSI_YELLOW + "  Belt Level: " + ANSI_RESET + beltLevel;
    }

    @Override
    public String[] getHeader() {
        return new String[]{"id","name","lastName","email","address","dateOfBirth","number","beltLevel"};
    }

    @Override
    public String toCSV() {
        return String.join(",",String.valueOf(this.getId()),this.getName(),this.getLastName(),this.getEmail(),this.getAddress(),
                String.valueOf(this.getDateOfBirth()),this.getNumber(),String.valueOf(this.getBeltLevel()));
    }

    public static Trainer fromCSV(String csv) {
        String[] values = csv.split(",");
        return new Trainer(Integer.parseInt(values[0]),values[1],values[2],values[3],values[4],Integer.parseInt(values[5]),values[6],BeltLevel.valueOf(values[7]));
    }
}
