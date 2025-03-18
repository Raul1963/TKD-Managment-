package org.example.Model;

/**
 * A functional interface that represents an object with a unique identifier.
 */

public interface HasID {
    /**
     * Gets the id of the object.
     * @return The id of the object.
     */
    Integer getId();

    /**
     * Gets the header of the CSV file.
     * @return The header of the CSV file.
     */
    String[] getHeader();

    /**
     * Transforms the object in CSV format
     * @return  The object in CSV format.
     */
    String toCSV();
}
