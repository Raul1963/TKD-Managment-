package org.example.Model;

/**
 * An abstract class that represents a person in the TKD management system
 */
public abstract class Person implements HasID{
    private int id;
    public String name;
    public String lastName;
    public String email;
    public String address;
    public int dateOfBirth;
    public String number;
    public BeltLevel beltLevel;

    /**
     * Constructs a new Person with the specified ID, name, last name, email, address, date of birth, number and belt level.
     * @param id            The unique identifier of the person.
     * @param name          The name of the person
     * @param lastName      The last name of the person.
     * @param email         The email of the person.
     * @param address       The address of the person.
     * @param dateOfBirth   The date of birth of the person.
     * @param number        The telephone number of the person.
     * @param beltLevel     The belt level of the person.
     */
    public Person(Integer id,String name, String lastName, String email, String address, int dateOfBirth, String number, BeltLevel beltLevel) {
        this.id=id;
        this.name = name;
        this.lastName = lastName;
        this.email = email;
        this.address = address;
        this.dateOfBirth = dateOfBirth;
        this.number = number;
        this.beltLevel = beltLevel;
    }


    /**
     * Constructs a new Person with the specified ID, name, last name, email, address, date of birth and number.
     * @param id            The unique identifier of the person.
     * @param name          The name of the person
     * @param lastName      The last name of the person.
     * @param email         The email of the person.
     * @param address       The address of the person.
     * @param number        The telephone number of the person.
     */
    public Person(Integer id,String name, String lastName, String email, String address,String number) {
        this.id=id;
        this.name = name;
        this.lastName = lastName;
        this.email = email;
        this.address = address;
        this.number = number;
    }

    public Person(){}

    /**
     * Sets the id of the person.
     * @param id The id of the person to set.
     */
    public void setId(int id) {this.id = id;}

    /**
     * Gets the name of the person.
     * @return The name of the person.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the person.
     * @param name  The name of the person to set.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the lastname of the person.
     * @return The lastname of the person.
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Sets the lastname of the person.
     * @param lastName  The lastname of the person to set.
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * Gets the email of the person.
     * @return The email of the person.
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the email of the person.
     * @param email  The email of the person to set.
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Gets the address of the person.
     * @return The address of the person.
     */
    public String getAddress() {
        return address;
    }

    /**
     * Sets the address of the person.
     * @param address The address of the person to set.
     */
    public void setAddress(String address) {
        this.address = address;
    }

    /**
     * Gets the date of birth of the person.
     * @return The date of birth of the person.
     */
    public int getDateOfBirth() {
        return dateOfBirth;
    }

    /**
     * Sets the date of birth of the person.
     * @param dateOfBirth  The date of birth of the person to set.
     */
    public void setDateOfBirth(int dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    /**
     * Gets the number of the person.
     * @return The number of the person.
     */
    public String getNumber() {
        return number;
    }

    /**
     * Sets the number of the person.
     * @param number  The number of the person to set.
     */
    public void setNumber(String number) {
        this.number = number;
    }

    /**
     * Gets the belt level of the person.
     * @return The belt level of the person.
     */
    public BeltLevel getBeltLevel() {
        return beltLevel;
    }

    /**
     * Sets the belt level of the person.
     * @param beltLevel  The belt level of the person.
     */
    public void setBeltLevel(BeltLevel beltLevel) {
        this.beltLevel = beltLevel;
    }

    /**
     * Gets the id of the person.
     * @return The id of the person.
     */
    @Override
    public Integer getId() {return id;}
}
