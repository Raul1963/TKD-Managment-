package org.example.Repository;

import org.example.Exceptions.DatabaseException;
import org.example.Model.DifficultyLevel;
import org.example.Model.Session;
import org.example.Model.TrainingCamp;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * A repository implementation that interacts with the database to manage session entities.
 */
public class DatabaseSession extends DatabaseRepo<Session> {
    /**
     * Constructs a new DatabaseSession with the specified database URL.
     *
     * @param dbUrl The URL of the database to connect to.
     */
    public DatabaseSession(String dbUrl) throws DatabaseException {
        super(dbUrl);
    }

    /**
     * Adds a new session object to the database.
     *
     * @param session The session object to be added.
     * @throws DatabaseException If there is an error executing the SQL query.
     */
    @Override
    public void add(Session session) throws DatabaseException {
        String sql = "INSERT INTO Sessions (id, difficultyLevel,maximumParticipants,trainerId,pricePerSession) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, session.getId());
            stmt.setString(2, String.valueOf(session.getDifficultyLevel()));
            stmt.setInt(3, session.getMaximumParticipants());
            stmt.setInt(4, session.getTrainer());
            stmt.setFloat(5, (float) session.getPricePerSession());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("DataBase Exception Error");
        }
    }

    /**
     * Removes a session object from the database by its ID.
     *
     * @param RemoveId The ID of the session to remove.
     * @throws DatabaseException If there is an error executing the SQL query.
     */
    @Override
    public void remove(Integer RemoveId) throws DatabaseException {
        String removeFromSession = "DELETE FROM Sessions WHERE ID=?";

        try(PreparedStatement statement = connection.prepareStatement(removeFromSession)){
            statement.setInt(1,RemoveId);
            statement.execute();
        } catch (SQLException e) {
            throw new DatabaseException("DataBase Exception Error");
        }
        String removeFromSessionStudents = "DELETE FROM SessionStudents WHERE sessionId=?";

        try(PreparedStatement statement = connection.prepareStatement(removeFromSessionStudents)){
            statement.setInt(1,RemoveId);
            statement.execute();
        } catch (SQLException e) {
            throw new DatabaseException("DataBase Exception Error");
        }
    }

    /**
     * Updates an existing session object in the database.
     *
     * @param session The session object to update.
     * @throws DatabaseException If there is an error executing the SQL query.
     */
    @Override
    public void update(Session session)  throws DatabaseException {
        String sql = "UPDATE Sessions SET difficultyLevel=?, maximumParticipants=?, trainerId=?,pricePerSession=?  WHERE id=?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, session.getDifficultyLevel().name());
            stmt.setInt(2, session.getMaximumParticipants());
            stmt.setInt(3, session.getTrainer());
            stmt.setDouble(4, session.getPricePerSession());
            stmt.setInt(5, session.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("DataBase Exception Error");
        }
    }

    /**
     * Retrieves a session object from the database by its ID.
     *
     * @param getId The ID of the session to retrieve.
     * @return The session object, or null if not found.
     * @throws DatabaseException If there is an error executing the SQL query.
     */
    @Override
    public Session get(Integer getId) throws DatabaseException{
        String sql = "SELECT * FROM Sessions WHERE ID=?";

        try(PreparedStatement statement = connection.prepareStatement(sql)){
            statement.setInt(1,getId);

            ResultSet resultSet = statement.executeQuery();

            if(resultSet.next()){
                List<Integer> students = getSessionStudents(getId);
                return extractFromResultSet(resultSet,students);
            } else {
                return null;
            }
        } catch (SQLException e) {
            throw new DatabaseException("DataBase Exception Error");
        }
    }

    /**
     * Retrieves all session objects from the database.
     *
     * @return A list of all session objects.
     * @throws DatabaseException If there is an error executing the SQL query.
     */
    @Override
    public List<Session> getAll() throws DatabaseException {
        String sql = "SELECT * FROM Sessions";

        try(PreparedStatement statement = connection.prepareStatement(sql)){
            ResultSet resultSet = statement.executeQuery();

            List<Session> sessions = new ArrayList<>();

            while(resultSet.next()){
                List<Integer> students = getSessionStudents(resultSet.getInt("id"));
                sessions.add(extractFromResultSet(resultSet,students));
            }

            return sessions;
        } catch (SQLException e) {
            throw new DatabaseException("DataBase Exception Error");
        }
    }
    /**
     * Extracts a session object from the given ResultSet.
     *
     * @param resultSet       The ResultSet containing the session data.
     * @param students The list of student IDs associated with the session.
     * @return The extracted session object.
     * @throws DatabaseException If there is an error processing the ResultSet.
     */
    public static Session extractFromResultSet(ResultSet resultSet,List<Integer> students) throws DatabaseException {
        try {
            Session session = new Session(resultSet.getInt("id"),DifficultyLevel.valueOf(resultSet.getString("difficultyLevel")),resultSet.getInt("maximumParticipants"),
                    resultSet.getInt("trainerId"),resultSet.getDouble("pricePerSession"));
            session.setSessionStudents(students);
            return session;
        } catch (SQLException e) {
            throw new DatabaseException("DataBase Exception Error");
        }
    }

    /**
     * Retrieves the list of students associated with a specific session by its ID.
     *
     * @param sessionId The ID of the session.
     * @return A list of student IDs associated with the session.
     * @throws DatabaseException If there is an error executing the SQL query.
     */
    public List<Integer> getSessionStudents(int sessionId) throws DatabaseException {
        String sql = "SELECT * FROM SessionStudents WHERE sessionId=?";
        List<Integer> studentList = new ArrayList<>();
        try(PreparedStatement statement = connection.prepareStatement(sql)){
            statement.setInt(1,sessionId);
            ResultSet resultSet = statement.executeQuery();
            while(resultSet.next()){
                studentList.add(resultSet.getInt("studentId"));
            }
        } catch (SQLException e) {
            throw new DatabaseException("DataBase Exception Error");
        }
        return studentList;
    }
}
