package org.example.Repository;

import org.example.Exceptions.DatabaseException;
import org.example.Model.BeltLevel;
import org.example.Model.SessionDate;
import org.example.Model.Student;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * A repository implementation that interacts with the database to manage Student entities.
 */

public class DatabaseStudent extends DatabaseRepo<org.example.Model.Student> {
    /**
     * Constructs a new DatabaseStudent with the specified database URL.
     * @param dbUrl
     */
    public DatabaseStudent(String dbUrl) throws DatabaseException {
        super(dbUrl);
    }

    /**
     * Adds a new student object in the database.
     *
     * @param student The belt exam object to be created.
     * @throws DatabaseException If there is an error executing the SQL query.
     */

    @Override
    public void add(Student student) throws DatabaseException {
        String addToStudent = "INSERT INTO Students (id, name,lastName,email,address,dateOfBirth,number, beltLevel, session) VALUES (?, ?, ?, ?, ?, ?, ?, ?,?)";
        try (PreparedStatement stmt = connection.prepareStatement(addToStudent)) {
            stmt.setInt(1, student.getId());
            stmt.setString(2, student.getName());
            stmt.setString(3, student.getLastName());
            stmt.setString(4, student.getEmail());
            stmt.setString(5, student.getAddress());
            stmt.setInt(6, student.getDateOfBirth());
            stmt.setString(7, student.getNumber());
            stmt.setString(8, String.valueOf(student.getBeltLevel()));
            stmt.setInt(9, student.getSession());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("DataBase Exception Error");
        }
        String addToSessionStudents = "INSERT INTO SessionStudents (studentId, sessionId) VALUES (?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(addToSessionStudents)) {
            stmt.setInt(1, student.getId());
            stmt.setInt(2, student.getSession());

            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("DataBase Exception Error");
        }
    }


    /**
     * Removes a student object from the database by its ID.
     * @param RemoveId The ID of the belt exam to remove.
     * @throws DatabaseException If there is an error executing the SQL query.
     **/

    @Override
    public void remove(Integer RemoveId) throws DatabaseException {

        String removeChildrenFromParents = "DELETE FROM ParentsStudents WHERE idStudent = ?";
        try(PreparedStatement stmt0 = connection.prepareStatement(removeChildrenFromParents)){
            stmt0.setInt(1, RemoveId);
            stmt0.executeUpdate();
        }catch(SQLException e){
            throw new DatabaseException("DataBase Exception Error");
        }

        String removeFromSessionStudents = "DELETE FROM SessionStudents WHERE studentId=?";

        try(PreparedStatement statement = connection.prepareStatement(removeFromSessionStudents)){
            statement.setInt(1,RemoveId);
            statement.execute();
        } catch (SQLException e) {
            throw new DatabaseException("DataBase Exception Error");
        }
        String removeFromSessionDates = "DELETE FROM SessionDates WHERE studentId=?";

        try(PreparedStatement statement = connection.prepareStatement(removeFromSessionDates)){
            statement.setInt(1,RemoveId);
            statement.execute();
        } catch (SQLException e) {
            throw new DatabaseException("DataBase Exception Error");
        }
        String removeFromStudentsTrainingCamp = "DELETE FROM StudentsTrainingCamp WHERE idStud=?";

        try(PreparedStatement statement = connection.prepareStatement(removeFromStudentsTrainingCamp)){
            statement.setInt(1,RemoveId);
            statement.execute();
        } catch (SQLException e) {
            throw new DatabaseException("DataBase Exception Error");
        }
        String removeFromStudentsContests = "DELETE FROM StudentsContests WHERE idStud=?";

        try(PreparedStatement statement = connection.prepareStatement(removeFromStudentsContests)){
            statement.setInt(1,RemoveId);
            statement.execute();
        } catch (SQLException e) {
            throw new DatabaseException("DataBase Exception Error");
        }
        String removeFromResultsBeltExams = "DELETE FROM ResultsBeltExams WHERE idStud=?";

        try(PreparedStatement statement = connection.prepareStatement(removeFromResultsBeltExams)){
            statement.setInt(1,RemoveId);
            statement.execute();
        } catch (SQLException e) {
            throw new DatabaseException("DataBase Exception Error");
        }
        String removeFromStudent = "DELETE FROM Students WHERE id=?";

        try(PreparedStatement statement = connection.prepareStatement(removeFromStudent)){
            statement.setInt(1,RemoveId);
            statement.execute();
        } catch (SQLException e) {
            throw new DatabaseException("DataBase Exception Error");
        }

    }


    /**
     * Updates an existing student exam object in the database.
     *
     * @param obj The student object to update.
     * @throws DatabaseException If there is an error executing the SQL query.
     */

    @Override
    public void update(Student obj) throws DatabaseException{
        String updateStudent = "UPDATE Students SET name=?, lastName=?,email=?,address=?,dateOfBirth=?,number=?, beltLevel=?, session=? WHERE id=?";

        try(PreparedStatement statement = connection.prepareStatement(updateStudent)){
            statement.setString(1,obj.getName());
            statement.setString(2,obj.getLastName());
            statement.setString(3,obj.getEmail());
            statement.setString(4,obj.getAddress());
            statement.setInt(5,obj.getDateOfBirth());
            statement.setString(6,obj.getNumber());
            statement.setString(7,String.valueOf(obj.getBeltLevel()));
            statement.setInt(8,obj.getSession());
            statement.setInt(9,obj.getId());

            statement.execute();
        } catch (SQLException e) {
            throw new DatabaseException("DataBase Exception Error");
        }
        String deleteSessionDates = "DELETE From SessionDates WHERE studentId=?";

        try(PreparedStatement statement = connection.prepareStatement(deleteSessionDates)){
            statement.setInt(1,obj.getId());
            statement.execute();
        } catch (SQLException e) {
            throw new DatabaseException("DataBase Exception Error");
        }
        String addSessionDates = "INSERT INTO SessionDates (studentId, sessionId,weekday,date,attended) VALUES (?,?,?,?,?)";

        for(SessionDate sd: obj.getSessionDateList()) {
            try (PreparedStatement statement = connection.prepareStatement(addSessionDates)) {
                statement.setInt(1, obj.getId());
                statement.setInt(2, obj.getSession());
                statement.setString(3, sd.getWeekDay());
                statement.setDate(4, Date.valueOf(sd.getDate()));
                statement.setBoolean(5, sd.isAttended());
                statement.execute();
            } catch (SQLException e) {
                //throw new DatabaseException("An error is by update occurred");
                throw new RuntimeException();
            }
        }
    }


    /**
     * Retrieves a student object from the database by its ID.
     *
     * @param getId The ID of the student to retrieve.
     * @return The student object, or null if not found.
     * @throws DatabaseException If there is an error executing the SQL query.
     **/

    @Override
    public Student get(Integer getId) throws DatabaseException{
        String sql = "SELECT * FROM Students WHERE id=?";

        try(PreparedStatement statement = connection.prepareStatement(sql)){
            statement.setInt(1,getId);

            ResultSet resultSet = statement.executeQuery();

            if(resultSet.next()){
                List<SessionDate> sessionDateList = getSessionDateStudent(getId);
                List<Integer> trainingCampIdList = getTrainingCampsStudent(getId);
                List<Integer> contestList = getContestsStudent(getId);
                int parentId = getIdParentfromStudent(resultSet.getInt("id"));
                return extractFromResultSet(resultSet, sessionDateList,trainingCampIdList,contestList,parentId);
            } else {
                return null;
            }
        } catch (SQLException e) {
            throw new DatabaseException("DataBase Exception Error");
        }
    }


    /**
     * Retrieves all students objects from the database.
     *
     * @return A list of all Stduent objects.
     * @throws DatabaseException If there is an error executing the SQL query.
     */

    @Override
    public List<Student> getAll() throws DatabaseException{
        String sql = "SELECT * FROM Students";

        try(PreparedStatement statement = connection.prepareStatement(sql)){
            ResultSet resultSet = statement.executeQuery();

            List<Student> students = new ArrayList<>();

            while(resultSet.next()){
                List<SessionDate> sessionDateList = getSessionDateStudent(resultSet.getInt("id"));
                List<Integer> trainingCampList = getTrainingCampsStudent(resultSet.getInt("id"));
                List<Integer> contestList = getContestsStudent(resultSet.getInt("id"));
                int parentId = getIdParentfromStudent(resultSet.getInt("id"));
                students.add(extractFromResultSet(resultSet,sessionDateList,trainingCampList,contestList,parentId));
            }

            return students;
        } catch (SQLException e) {
            throw new DatabaseException("DataBase Exception Error");
        }
    }

    /**
     * Extracts a student object from the given ResultSet.
     *
     * @param resultSet The ResultSet containing the student data.
     * @return The extracted student object.
     * @throws DatabaseException If there is an error retrieving related entities.
     */

    public static Student extractFromResultSet(ResultSet resultSet,List<SessionDate> sessionDateList, List<Integer> trainingCamps,List<Integer> contests, int parentId) throws SQLException {
        Student student = new Student(resultSet.getInt("id"), resultSet.getString("name"), resultSet.getString("lastName"),resultSet.getString("email"),
                resultSet.getString("address"),resultSet.getInt("dateOfBirth"),resultSet.getString("number"), BeltLevel.valueOf(resultSet.getString("beltLevel")),
                resultSet.getInt("session"));
        student.setSessionDateList(sessionDateList);
        student.setTrainingCampList(trainingCamps);
        student.setContestList(contests);
        student.setParent(parentId);
        return student;
    }




    public static SessionDate extractFromSessionDate(ResultSet resultSet) throws DatabaseException {
        try {
            return new SessionDate(resultSet.getString("weekday"),String.valueOf(resultSet.getDate("date")),resultSet.getInt("sessionId"),resultSet.getBoolean("attended"));
        } catch (SQLException e) {
            throw new DatabaseException("DataBase Exception Error");
        }
    }

    /**
     * Gets the list of students from a student by its id.
     * @param studentId            The id of the student to get.
     * @return                      The list of students of the belt exam
     * @throws DatabaseException    If there is an error retrieving related entities.
     */
    public List<SessionDate> getSessionDateStudent(int studentId) throws DatabaseException{
        String sql = "SELECT * FROM SessionDates WHERE studentId=?";
        List<SessionDate> sessionDateList = new ArrayList<>();
        try(PreparedStatement statement = connection.prepareStatement(sql)){
            statement.setInt(1,studentId);
            ResultSet resultSet = statement.executeQuery();

            while(resultSet.next()){
                sessionDateList.add(extractFromSessionDate(resultSet));
            }
        }
        catch (SQLException e) {
            throw new DatabaseException("DataBase Exception Error");
        }
        return sessionDateList;
    }

    /**
     * return a list with Students ID's that participate at that TrainingCamp
     * @param studentId the id of the student
     * @return a list with Students ID's that participate at that TrainingCamp
     * @throws DatabaseException If there is an error retrieving related entities.
     */
    public List<Integer> getTrainingCampsStudent(int studentId) throws DatabaseException{
        String sql = "SELECT * FROM StudentsTrainingCamp WHERE idStud=?";
        List<Integer> trainingCampList = new ArrayList<>();
        try(PreparedStatement statement = connection.prepareStatement(sql)){
            statement.setInt(1,studentId);
            ResultSet resultSet = statement.executeQuery();
            while(resultSet.next()){
                trainingCampList.add(resultSet.getInt("idTrainingCamp"));
            }
        } catch (SQLException e) {
            throw new DatabaseException("DataBase Exception Error");
        }
        return trainingCampList;
    }

    /**
     * Retrieves a list of contest IDs associated with a specific student.
     *
     * @param studentId the ID of the student whose contests are to be retrieved
     * @return a list of contest IDs in which the student is registered
     * @throws DatabaseException if an error occurs while accessing the database
     */
    public List<Integer> getContestsStudent(int studentId) throws DatabaseException{
        String sql = "SELECT * FROM StudentsContests WHERE idStud=?";
        List<Integer> contestList = new ArrayList<>();
        try(PreparedStatement statement = connection.prepareStatement(sql)){
            statement.setInt(1,studentId);
            ResultSet resultSet = statement.executeQuery();
            while(resultSet.next()){
                contestList.add(resultSet.getInt("idContest"));
            }
        } catch (SQLException e) {
            throw new DatabaseException("DataBase Exception Error");
        }
        return contestList;
    }

    /**
     * Retrieves the parent ID associated with a specific student.
     *
     * @param studentId the ID of the student whose parent ID is to be retrieved
     * @return the parent ID associated with the student, or 0 if no parent is found
     * @throws DatabaseException if an error occurs while accessing the database
     */
    public int getIdParentfromStudent(int studentId) throws DatabaseException{
        String sql = "SELECT * FROM ParentsStudents WHERE idStudent = ?";
        try(PreparedStatement stmt = connection.prepareStatement(sql)){
            stmt.setInt(1,studentId);
            ResultSet resultSet = stmt.executeQuery();
            if(resultSet.next()){
                return resultSet.getInt("idParent");
            }
            else return 0;

        }catch (SQLException e) {
            throw new DatabaseException("DataBase Exception Error");
        }
    }

}
