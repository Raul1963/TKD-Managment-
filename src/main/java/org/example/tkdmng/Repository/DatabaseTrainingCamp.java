package org.example.Repository;

import org.example.Exceptions.DatabaseException;
import org.example.Model.BeltExam;
import org.example.Model.SessionDate;
import org.example.Model.TrainingCamp;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * A repository implementation for managing `TrainingCamp` entities in the database.
 */
public class DatabaseTrainingCamp extends DatabaseRepo<TrainingCamp> {
    /**
     * Constructs a new `DatabaseTrainingCamp` with the specified database URL.
     *
     * @param dbUrl The URL of the database to connect to.
     * @throws DatabaseException If there is an error connecting to the database.
     */
    public DatabaseTrainingCamp(String dbUrl) throws DatabaseException {
        super(dbUrl);
    }

    /**
     * Adds a new `TrainingCamp` object to the database.
     *
     * @param trainingCamp The `TrainingCamp` object to be added.
     * @throws DatabaseException If there is an error executing the SQL query.
     */
    @Override
    public void add(TrainingCamp trainingCamp) throws DatabaseException {
        String sql = "INSERT INTO TrainingCamps (id, startDate,endDate,price,country,city,address,numberOfParticipants) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, trainingCamp.getId());
            stmt.setDate(2, Date.valueOf(trainingCamp.getStartDate()));
            stmt.setDate(3, Date.valueOf(trainingCamp.getEndDate()));
            stmt.setDouble(4, trainingCamp.getPrice());
            stmt.setString(5, trainingCamp.getCountry());
            stmt.setString(6, trainingCamp.getCity());
            stmt.setString(7, trainingCamp.getAddress());
            stmt.setInt(8, trainingCamp.getNumberOfParticipants());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("DataBase Exception Error");
        }
    }
    /**
     * Removes a `TrainingCamp` object from the database by its ID.
     *
     * @param removeId The ID of the training camp to be removed.
     * @throws DatabaseException If there is an error executing the SQL query.
     */
    @Override
    public void remove(Integer removeId) throws DatabaseException{
        String removeFromStudentsTrainingCamp = "DELETE FROM StudentsTrainingCamp WHERE idTrainingCamp=?";

        try(PreparedStatement statement = connection.prepareStatement(removeFromStudentsTrainingCamp)){
            statement.setInt(1,removeId);
            statement.execute();
        } catch (SQLException e) {
            throw new DatabaseException("DataBase Exception Error");
        }
        String sql = "DELETE FROM TrainingCamps WHERE id=?";

        try(PreparedStatement statement = connection.prepareStatement(sql)){
            statement.setInt(1,removeId);
            statement.execute();
        } catch (SQLException e) {
            throw new DatabaseException("DataBase Exception Error");
        }
    }

    /**
     * Updates an existing `TrainingCamp` object in the database.
     *
     * @param trainingCamp The `TrainingCamp` object to update.
     * @throws DatabaseException If there is an error executing the SQL query.
     */
    @Override
    public void update(TrainingCamp trainingCamp) throws DatabaseException {
        String sql = "UPDATE TrainingCamps SET startDate=?, endDate=?, price=?,country=?,city=?,address=?,numberOfParticipants=?  WHERE ID=?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setDate(1, Date.valueOf(trainingCamp.getStartDate()));
            stmt.setDate(2, Date.valueOf(trainingCamp.getEndDate()));
            stmt.setDouble(3, trainingCamp.getPrice());
            stmt.setString(4, trainingCamp.getCountry());
            stmt.setString(5, trainingCamp.getCity());
            stmt.setString(6, trainingCamp.getAddress());
            stmt.setInt(7, trainingCamp.getNumberOfParticipants());
            stmt.setInt(8, trainingCamp.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("DataBase Exception Error");
        }
        String deleteStudentsTrainingCamps = "DELETE From StudentsTrainingCamp WHERE idStud=?";

        try(PreparedStatement statement = connection.prepareStatement(deleteStudentsTrainingCamps)){
            statement.setInt(1,trainingCamp.getId());
            statement.execute();
        } catch (SQLException e) {
            throw new DatabaseException("DataBase Exception Error");
        }
        String addStudentsTrainingCamps = "INSERT INTO StudentsTrainingCamp (idTrainingCamp,idStud) VALUES (?,?)";

        for(int studentId: trainingCamp.getStudents()) {
            try (PreparedStatement statement = connection.prepareStatement(addStudentsTrainingCamps)) {
                statement.setInt(1, trainingCamp.getId());
                statement.setInt(2, studentId);
                statement.execute();
            } catch (SQLException e) {
                throw new DatabaseException("DataBase Exception Error");
            }
        }
    }

    /**
     * Retrieves a `TrainingCamp` object from the database by its ID.
     *
     * @param getId The ID of the training camp to retrieve.
     * @return The `TrainingCamp` object, or null if not found.
     * @throws DatabaseException If there is an error executing the SQL query.
     */
    @Override
    public TrainingCamp get(Integer getId) throws DatabaseException{
        String sql = "SELECT * FROM TrainingCamps WHERE ID=?";

        try(PreparedStatement statement = connection.prepareStatement(sql)){
            statement.setInt(1,getId);

            ResultSet resultSet = statement.executeQuery();

            if(resultSet.next()){
                List<Integer> students = getStudentsFromTrainingCamp(getId);
                return extractFromResultSet(resultSet,students);
            } else {
                return null;
            }
        } catch (SQLException e) {
            throw new DatabaseException("DataBase Exception Error");
        }
    }

    /**
     * Retrieves all `TrainingCamp` objects from the database.
     *
     * @return A list of all `TrainingCamp` objects.
     * @throws DatabaseException If there is an error executing the SQL query.
     */
    @Override
    public List<TrainingCamp> getAll() throws DatabaseException{
        String sql = "SELECT * FROM TrainingCamps";

        try(PreparedStatement statement = connection.prepareStatement(sql)){
            ResultSet resultSet = statement.executeQuery();

            List<TrainingCamp> trainingCamps = new ArrayList<>();

            while(resultSet.next()){
                List<Integer> students = getStudentsFromTrainingCamp(resultSet.getInt("id"));
                trainingCamps.add(extractFromResultSet(resultSet,students));
            }

            return trainingCamps;
        } catch (SQLException e) {
            throw new DatabaseException("DataBase Exception Error");
        }
    }

    /**
     * Extracts a `TrainingCamp` object from the given ResultSet.
     *
     * @param resultSet The ResultSet containing the training camp data.
     * @param students  The list of student IDs associated with the training camp.
     * @return The extracted `TrainingCamp` object.
     * @throws DatabaseException If there is an error processing the ResultSet.
     */
    public static TrainingCamp extractFromResultSet(ResultSet resultSet,List<Integer> students) throws DatabaseException {
        TrainingCamp trainingCamp = null;
        try {
            trainingCamp = new TrainingCamp(resultSet.getInt("id"),resultSet.getDate("startDate").toString(), resultSet.getDate("endDate").toString(),resultSet.getDouble("price"),
                    resultSet.getString("country"),resultSet.getString("city"),resultSet.getString("address"),resultSet.getInt("numberOfParticipants"));
        } catch (SQLException e) {
            throw new DatabaseException("DataBase Exception Error");
        }
        trainingCamp.setStudents(students);
        return trainingCamp;
    }


    /**
     * Retrieves the list of students associated with a specific training camp by its ID.
     *
     * @param trainingCampId The ID of the training camp.
     * @return A list of student IDs associated with the training camp.
     * @throws DatabaseException If there is an error executing the SQL query.
     */
    public List<Integer> getStudentsFromTrainingCamp(int trainingCampId) throws DatabaseException{
        String sql = "SELECT * FROM StudentsTrainingCamp WHERE idTrainingCamp=?";
        List<Integer> students = new ArrayList<>();
        try(PreparedStatement statement = connection.prepareStatement(sql)){
            statement.setInt(1,trainingCampId);
            ResultSet resultSet = statement.executeQuery();

            while(resultSet.next()){
                students.add(resultSet.getInt("idStud"));
            }
        }
        catch (SQLException e) {
            throw new DatabaseException("DataBase Exception Error");
        }
        return students;
    }
}
