package org.example.Repository;

import org.example.Exceptions.DatabaseException;
import org.example.Model.HasID;

import java.sql.SQLException;
import java.util.List;

/**
 * An interface that defines the basic CRUD operations for a repository.
 * @param <T> The type of objects stored in the repository, which must implement HasId.
 */
public interface IRepo <T extends HasID>{
    /**
     * Adds a new object in the repository.
     * @param obj The object to add.
     */
    void add(T obj) throws DatabaseException;

    /**
     * Deletes an object from the repository by its ID.
     * @param RemoveId The unique identifier of the object to delete.
     */
    void remove(Integer RemoveId) throws DatabaseException;

    /**
     * Updates an existing object in the repository.
     * @param obj The object to update.
     */
    void update(T obj) throws DatabaseException;

    /**
     * Retrieves an object from the repository by its ID.
     * @param getId The unique identifier of the object to retrieve.
     * @return The object with the specified ID, or null if not found.
     */
    T get(Integer getId) throws DatabaseException;

    /**
     * Retrieves all objects from the repository.
     * @return A list of all objects in the repository.
     */
    List<T> getAll() throws DatabaseException;
}
