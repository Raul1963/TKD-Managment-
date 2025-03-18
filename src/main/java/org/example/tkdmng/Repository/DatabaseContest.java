package org.example.Repository;

import org.example.Exceptions.DatabaseException;
import org.example.Model.Contest;
import org.example.Model.Student;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


/**
 * A repository implementation that interacts with the database to manage contest entities.
 */
public class DatabaseContest extends DatabaseRepo<org.example.Model.Contest>{
    /**
     * Constructs a new DatabaseContest with the specified database URL.
     *
     * @param dbUrl The URL of the database to connect to.
     * @throws DatabaseException If there is an error connecting to the database.
     */
    public DatabaseContest(String dbUrl) throws DatabaseException {
        super(dbUrl);
    }

    /**
     * Adds a new contest object to the database.
     *
     * @param obj The contest object to be added.
     * @throws DatabaseException If there is an error executing the SQL query.
     */
    @Override
    public void add(Contest obj) throws DatabaseException {
        String sql = "INSERT INTO dbo.Contest(id,startDate, endDate, price, country, city, name, address) values(?,?,?,?,?,?,?,?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)){
            stmt.setInt(1, obj.getId());
            stmt.setString(2, obj.getStartDate());
            stmt.setString(3, obj.getEndDate());
            stmt.setDouble(4, obj.getPrice());
            stmt.setString(5, obj.getCountry());
            stmt.setString(6, obj.getCity());
            stmt.setString(7, obj.getName());
            stmt.setString(8, obj.getAddress());
            stmt.executeUpdate();
        }catch (SQLException e) {
            throw new DatabaseException("DataBase Exception Error");
        }
    }

    /**
     * Removes a contest object from the database by its ID.
     *
     * @param RemoveId The ID of the contest to remove.
     * @throws DatabaseException If there is an error executing the SQL query.
     */
    @Override
    public void remove(Integer RemoveId) throws DatabaseException {

        String removeStudentContests = "DELETE FROM StudentsContests WHERE idContest = ?";

        try(PreparedStatement stmt = connection.prepareStatement(removeStudentContests)){
            stmt.setInt(1, RemoveId);
            stmt.executeUpdate();
        }catch (SQLException e) {
            throw new DatabaseException("DataBase Exception Error");
        }

        String sql = "DELETE FROM Contest WHERE Contest.id = ?";
        try(PreparedStatement stmt = connection.prepareStatement(sql)){
            stmt.setInt(1, RemoveId);
            stmt.executeUpdate();
        }catch (SQLException e) {
            throw new DatabaseException("DataBase Exception Error");
        }
    }

    /**
     * Updates an existing contest object in the database.
     *
     * @param obj The contest object to update.
     * @throws DatabaseException If there is an error executing the SQL query.
     */
    @Override
    public void update(Contest obj) throws DatabaseException {
        String sql = "UPDATE dbo.Contest SET startDate = ?, endDate = ?, price = ?, country = ?, city = ?, name = ?, address = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            // Setăm toți parametrii, indiferent dacă s-au schimbat sau nu
            stmt.setString(1, obj.getStartDate());
            stmt.setString(2, obj.getEndDate());
            stmt.setDouble(3, obj.getPrice());
            stmt.setString(4, obj.getCountry());
            stmt.setString(5, obj.getCity());
            stmt.setString(6, obj.getName());
            stmt.setString(7, obj.getAddress());
            stmt.setInt(8, obj.getId());

            // Executăm query-ul
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("DataBase Exception Error");
        }


        String deleteStudentsFromContest = "DELETE From StudentsContests WHERE idContest = ?";
        try(PreparedStatement stmt2 = connection.prepareStatement(deleteStudentsFromContest)){
            stmt2.setInt(1, obj.getId());
            stmt2.executeUpdate();
        }catch(SQLException e) {
            throw new DatabaseException("DataBase Exception Error");
        }

        String insertStudentsContests = "INSERT INTO StudentsContests(idContest, idStud) VALUES(?,?)";

        for(int idStudent: obj.getStudents()){
            try(PreparedStatement stmt = connection.prepareStatement(insertStudentsContests)){
                stmt.setInt(1, obj.getId());
                stmt.setInt(2, idStudent);
                stmt.executeUpdate();
            }catch(SQLException e) {
                throw new DatabaseException("DataBase Exception Error");
            }
        }

    }

    /**
     * Retrieves a contest object from the database by its ID.
     *
     * @param getId The ID of the contest to retrieve.
     * @return The contest object, or null if not found.
     * @throws DatabaseException If there is an error executing the SQL query.
     */
    @Override
    public Contest get(Integer getId) throws DatabaseException {
        String sql = "SELECT * FROM Contest WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, getId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    // Creăm un obiect Contest din valorile obținute
                    List<Integer> studentList = getContestsStudents(getId);
                    return extractContest(rs, studentList);
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("DataBase Exception Error");
        }
        return null; // Dacă nu există un concurs cu ID-ul dat
    }


    /**
     * Retrieves all contest objects from the database.
     *
     * @return A list of all contest objects.
     * @throws DatabaseException If there is an error executing the SQL query.
     */
    @Override
    public List<Contest> getAll() throws DatabaseException {
        String sql = "SELECT * FROM Contest";
        List<Contest> contests = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    List<Integer> studentList = getContestsStudents(rs.getInt("id"));
                    contests.add(extractContest(rs,studentList));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("DataBase Exception Error");
        }
        return contests;
    }

    /**
     * Retrieves the list of students associated with a specific contest by its ID.
     *
     * @param ContestId The ID of the contest.
     * @return A list of student IDs participating in the contest.
     * @throws DatabaseException If there is an error executing the SQL query.
     */
    public List<Integer> getContestsStudents(int ContestId) throws DatabaseException{
        String sql = "SELECT * FROM StudentsContests WHERE idContest = ?";
        List<Integer> students = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, ContestId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    students.add(rs.getInt("idStud"));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("DataBase Exception Error");
        }
        return students;
    }

    /**
     * Extracts a contest object from the given ResultSet.
     *
     * @param rs       The ResultSet containing the contest data.
     * @param students The list of student IDs associated with the contest.
     * @return The extracted contest object.
     * @throws DatabaseException If there is an error processing the ResultSet.
     */
    public static Contest extractContest(ResultSet rs, List<Integer> students) throws DatabaseException{
        Contest contest = null;
        try {
            contest = new Contest(rs.getInt("id"),
                    rs.getString("startDate"),
                    rs.getString("endDate"),
                    rs.getDouble("price"),
                    rs.getString("country"),
                    rs.getString("city"),
                    rs.getString("name"),
                    rs.getString("address"));
        } catch (SQLException e) {
            throw new DatabaseException("DataBase Exception Error");
        }
        contest.setStudents(students);
        return contest;
    }

}
