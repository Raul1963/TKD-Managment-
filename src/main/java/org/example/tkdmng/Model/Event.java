package org.example.Model;

/**
 * An abstract class that represents an event in the TKD management system
 */
public abstract class Event implements HasID{
    private int id;
    public String startDate;
    public String endDate;
    public double price;
    public String country;
    public String city;
    public String address;

    /**
     * Constructs a new Event with the specified ID, start date, end date, price, country, city and address.
     * @param id            The unique identifier of the event.
     * @param startDate     The start date of the event.
     * @param endDate       The end date of the event.
     * @param price         The price of the event.
     * @param country       The country where the event takes place.
     * @param city          The city where the event takes place.
     * @param address       The address where the event takes place.
     */
    public Event(int id,String startDate, String endDate, double price, String country, String city, String address) {
        this.id = id;
        this.startDate = startDate;
        this.endDate = endDate;
        this.price = price;
        this.country = country;
        this.city = city;
        this.address = address;
    }
    public Event(){}
    /**
     * Gets the start date of the event.
     * @return The start date of the event.
     */
    public String getStartDate() {
        return startDate;
    }

    /**
     * Sets the start date of the event.
     * @param startDate  The start date of the event to set.
     */
    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    /**
     * Gets the end date of the event.
     * @return The end date of the event.
     */
    public String getEndDate() {
        return endDate;
    }

    /**
     * Sets the end date of the event.
     * @param endDate  The end date of the event to set.
     */
    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    /**
     * Gets the price of the event.
     * @return The price of the event.
     */
    public double getPrice() {
        return price;
    }

    /**
     * Sets the price of the event.
     * @param price  The price of the event to set.
     */
    public void setPrice(double price) {
        this.price = price;
    }

    /**
     * Gets the country of the event.
     * @return The country of the event.
     */
    public String getCountry() {
        return country;
    }

    /**
     * Sets the country of the event.
     * @param country  The country of the event to set.
     */
    public void setCountry(String country) {
        this.country = country;
    }

    /**
     * Gets the city of the event.
     * @return The city of the event.
     */
    public String getCity() {
        return city;
    }

    /**
     * Sets the city of the event.
     * @param city  The city of the event to set.
     */
    public void setCity(String city) {
        this.city = city;
    }

    /**
     * Gets the address of the event.
     * @return The address of the event.
     */
    public String getAddress() {
        return address;
    }

    /**
     * Sets the address of the event.
     * @param address  The address of the event to set.
     */
    public void setAddress(String address) {
        this.address = address;
    }

    /**
     * Gets the id of the event.
     * @return The id of the event.
     */
    @Override
    public Integer getId() {return id;}
}
