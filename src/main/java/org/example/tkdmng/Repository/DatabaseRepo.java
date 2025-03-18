package org.example.Repository;

import org.example.Exceptions.DatabaseException;
import org.example.Model.HasID;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

/**
 * A repository implementation that stores main.java.data in database.
 *
 * @param <T> The type of objects stored in the repository, which must implement HasId.
 */
public abstract class DatabaseRepo<T extends HasID> implements IRepo<T>,AutoCloseable {
    protected final Connection connection;

    /**
     * Constructs a new  DBRepository and establishes a connection to the database.
     *
     * @param dbUrl The URL of the database to connect to.
     * @throws DatabaseException If there is an error while attempting to connect to the database.
     */
    public DatabaseRepo(String dbUrl) throws DatabaseException {
        try {
            connection = DriverManager.getConnection(dbUrl);
        } catch (SQLException e) {
            throw new DatabaseException(e.getMessage());
        }
    }

    /**
     * Closes the database connection.
     *
     * @throws Exception If there is an error while closing the connection.
     */
    @Override
    public void close() throws Exception {
        connection.close();
    }
}
