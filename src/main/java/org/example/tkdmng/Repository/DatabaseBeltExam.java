package org.example.Repository;

import org.example.Exceptions.DatabaseException;
import org.example.Model.BeltExam;
import org.example.Model.BeltLevel;
import org.example.Model.Student;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A repository implementation that interacts with the database to manage belt exams entities.
 */
public class DatabaseBeltExam extends DatabaseRepo<BeltExam> {

    /**
     * Constructs a new DatabaseBeltExam with the specified database URL.
     *
     * @param dbUrl The URL of the database to connect to.
     * @throws DatabaseException If there is an error connecting to the database.
     */
    public DatabaseBeltExam(String dbUrl) throws DatabaseException {
        super(dbUrl);
    }

    /**
     * Adds a new belt exam object in the database.
     *
     * @param beltExam The belt exam object to be created.
     * @throws DatabaseException If there is an error executing the SQL query.
     */
    @Override
    public void add(BeltExam beltExam) throws DatabaseException {
        String sql = "INSERT INTO BeltExams (id, startDate,endDate,price,country,city,address,beltColor) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, beltExam.getId());
            stmt.setDate(2, Date.valueOf(beltExam.getStartDate()));
            stmt.setDate(3, Date.valueOf(beltExam.getEndDate()));
            stmt.setDouble(4, beltExam.getPrice());
            stmt.setString(5, beltExam.getCountry());
            stmt.setString(6, beltExam.getCity());
            stmt.setString(7, beltExam.getAddress());
            stmt.setString(8, String.valueOf(beltExam.getBeltColor()));
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("DataBase Exception Error");
        }
    }

/**
 * Removes a belt exam object from the database by its ID.
 *
 * @param RemoveId The ID of the belt exam to remove.
 * @throws DatabaseException If there is an error executing the SQL query.
 **/
    @Override
    public void remove(Integer RemoveId) throws DatabaseException {
        String removeFromResultsBeltExams = "DELETE FROM ResultsBeltExams WHERE idBeltExam=?";

        try(PreparedStatement statement = connection.prepareStatement(removeFromResultsBeltExams)){
            statement.setInt(1,RemoveId);
            statement.execute();
        } catch (SQLException e) {
            throw new DatabaseException("DataBase Exception Error");
        }
        String sql = "DELETE FROM BeltExams WHERE ID=?";

        try(PreparedStatement statement = connection.prepareStatement(sql)){
            statement.setInt(1,RemoveId);
            statement.execute();
        } catch (SQLException e) {
            throw new DatabaseException("DataBase Exception Error");
        }

    }

    /**
     * Updates an existing belt exam object in the database.
     *
     * @param beltExam The belt exam object to update.
     * @throws DatabaseException If there is an error executing the SQL query.
     */
    @Override
    public void update(BeltExam beltExam) throws DatabaseException {
        String sql = "UPDATE BeltExams SET startDate=?, endDate=?, price=?,country=?,city=?,address=?,beltColor=?  WHERE ID=?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setDate(1, Date.valueOf(beltExam.getStartDate()));
            stmt.setDate(2, Date.valueOf(beltExam.getEndDate()));
            stmt.setDouble(3, beltExam.getPrice());
            stmt.setString(4, beltExam.getCountry());
            stmt.setString(5, beltExam.getCity());
            stmt.setString(6, beltExam.getAddress());
            stmt.setString(7, String.valueOf(beltExam.getBeltColor()));
            stmt.setInt(8, beltExam.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("DataBase Exception Error");
        }
        String deleteStudentsBeltExams = "DELETE From ResultsBeltExams WHERE idStud=?";

        try(PreparedStatement statement = connection.prepareStatement(deleteStudentsBeltExams)){
            statement.setInt(1,beltExam.getId());
            statement.execute();
        } catch (SQLException e) {
            throw new DatabaseException("DataBase Exception Error");
        }
        String addStudentsTrainingCamps = "INSERT INTO ResultsBeltExams (idBeltExam,idStud,result) VALUES (?,?,?)";

        for(int studentId: beltExam.getListOfResults().keySet()) {
            try (PreparedStatement statement = connection.prepareStatement(addStudentsTrainingCamps)) {
                statement.setInt(1, beltExam.getId());
                statement.setInt(2, studentId);
                statement.setInt(3, beltExam.getListOfResults().get(studentId));
                statement.execute();
            } catch (SQLException e) {
                throw new DatabaseException("DataBase Exception Error");
            }
        }
    }

/**
 * Retrieves a belt exam object from the database by its ID.
 *
 * @param getId The ID of the belt exam to retrieve.
 * @return The belt exam object, or null if not found.
 * @throws DatabaseException If there is an error executing the SQL query.
 **/
    @Override
    public BeltExam get(Integer getId) throws DatabaseException{
        String sql = "SELECT * FROM BeltExams WHERE id=?";

        try(PreparedStatement statement = connection.prepareStatement(sql)){
            statement.setInt(1,getId);

            ResultSet resultSet = statement.executeQuery();

            if(resultSet.next()){
                Map<Integer,Integer> results = getResultListFromBeltExam(getId);
                return extractFromResultSet(resultSet,results);
            } else {
                return null;
            }
        } catch (SQLException e) {
            throw new DatabaseException("DataBase Exception Error");
        }
    }

    /**
     * Retrieves all belt exams objects from the database.
     *
     * @return A list of all belt exams objects.
     * @throws DatabaseException If there is an error executing the SQL query.
     */
    @Override
    public List<BeltExam> getAll() throws DatabaseException {
        String sql = "SELECT * FROM BeltExams";

        try(PreparedStatement statement = connection.prepareStatement(sql)){
            ResultSet resultSet = statement.executeQuery();

            List<BeltExam> beltExams = new ArrayList<>();

            while(resultSet.next()){
                Map<Integer,Integer> results = getResultListFromBeltExam(resultSet.getInt("id"));
                beltExams.add(extractFromResultSet(resultSet,results));
            }

            return beltExams;
        } catch (SQLException e) {
            throw new DatabaseException("DataBase Exception Error");
        }
    }

    /**
     * Extracts a belt exam object from the given ResultSet.
     *
     * @param resultSet The ResultSet containing the belt exam data.
     * @return The extracted belt exam object.
     * @throws DatabaseException If there is an error retrieving related entities.
     */
    public static BeltExam extractFromResultSet(ResultSet resultSet,Map<Integer,Integer> results) throws DatabaseException {
        BeltExam beltExam = null;
        try {
            beltExam = new BeltExam(resultSet.getInt("id"),resultSet.getDate("startDate").toString(), resultSet.getDate("endDate").toString(),resultSet.getDouble("price"),
                    resultSet.getString("country"),resultSet.getString("city"),resultSet.getString("address"), BeltLevel.valueOf(resultSet.getString("beltColor")));
        } catch (SQLException e) {
            throw new DatabaseException("DataBase Exception Error");
        }
        beltExam.setListOfResults(results);
        return beltExam;
    }

    /**
     * Gets the list of students from a belt exam by its id.
     * @param beltExamId            The id of the belt exam to get.
     * @return                      The list of students of the belt exam
     * @throws DatabaseException    If there is an error retrieving related entities.
     */
    public Map<Integer,Integer> getResultListFromBeltExam(int beltExamId)  throws DatabaseException {
        String sql = "SELECT * FROM ResultsBeltExams WHERE idBeltExam=?";
        Map<Integer,Integer> results= new HashMap<>();
        try(PreparedStatement statement = connection.prepareStatement(sql)){
            statement.setInt(1,beltExamId);
            ResultSet resultSet = statement.executeQuery();

            while(resultSet.next()){
                results.put(resultSet.getInt("idStud"),resultSet.getInt("result"));
            }
        }
        catch (SQLException e) {
            throw new DatabaseException("DataBase Exception Error");
        }
        return results;
    }
}
