package org.example.Repository;
import org.example.Exceptions.DatabaseException;
import org.example.Model.Parent;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * A repository implementation that interacts with the database to manage parent entities.
 */
public class DatabaseParent extends DatabaseRepo<org.example.Model.Parent>{

    /**
     * Constructs a new DatabaseParent with the specified database URL.
     *
     * @param dbUrl The URL of the database to connect to.
     */

    public DatabaseParent(String dbUrl) throws DatabaseException {
        super(dbUrl);
    }

    /**
     * Adds a new parent object to the database.
     *
     * @param obj The parent object to be added.
     * @throws DatabaseException If there is an error executing the SQL query.
     */
    @Override
    public void add(Parent obj) throws DatabaseException {
        String sql = "INSERT INTO PARENT (id,name,lastName, email, address, number) VALUES (?,?,?,?,?,?)";
        try( PreparedStatement stmt= connection.prepareStatement(sql)) {
            stmt.setInt(1, obj.getId());
            stmt.setString(2, obj.getName());
            stmt.setString(3, obj.getLastName());
            stmt.setString(4, obj.getEmail());
            stmt.setString(5, obj.getAddress());
            stmt.setString(6, obj.getNumber());
            stmt.executeUpdate();
        }catch(Exception e) {
            throw new DatabaseException("DataBase Exception Error");
        }
    }

    /**
     * Removes a parent object from the database by its ID.
     *
     * @param RemoveId The ID of the parent to remove.
     * @throws DatabaseException If there is an error executing the SQL query.
     */
    @Override
    public void remove(Integer RemoveId) throws DatabaseException {
        String removeChildrenFromParents = "DELETE FROM ParentsStudents WHERE idParent = ?";
        try(PreparedStatement stmt0 = connection.prepareStatement(removeChildrenFromParents)){
            stmt0.setInt(1, RemoveId);
            stmt0.executeUpdate();
        }catch(SQLException e){
            throw new DatabaseException("DataBase Exception Error");
        }
        String sql = "DELETE FROM PARENT WHERE id = ?";
        try( PreparedStatement stmt= connection.prepareStatement(sql)) {
            stmt.setInt(1, RemoveId);
            stmt.executeUpdate();
        }catch(Exception e) {
            throw new DatabaseException("DataBase Exception Error");
        }
    }

    /**
     * Updates an existing parent object in the database.
     *
     * @param obj The parent object to update.
     * @throws DatabaseException If there is an error executing the SQL query.
     */
    @Override
    public void update(Parent obj) throws DatabaseException {
        String sql = "UPDATE dbo.Parent SET name = ?, lastName = ?, email = ?, address = ?, number=? WHERE id = ?";
        try( PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, obj.getName());
            stmt.setString(2, obj.getLastName());
            stmt.setString(3, obj.getEmail());
            stmt.setString(4, obj.getAddress());
            stmt.setString(5, obj.getNumber());
            stmt.setInt(6, obj.getId());
            stmt.executeUpdate();
        }catch (Exception e) {
            throw new DatabaseException("DataBase Exception Error");
        }

        String deleteChildrenFromParent = "DELETE From ParentsStudents WHERE idParent = ?";
        try(PreparedStatement stmt2 = connection.prepareStatement(deleteChildrenFromParent)){
            stmt2.setInt(1, obj.getId());
            stmt2.executeUpdate();
        }catch(SQLException e) {
            throw new DatabaseException("DataBase Exception Error");
        }


        String insertChildrenParents = "INSERT INTO ParentsStudents(idParent, idStudent) VALUES(?,?)";
        for(int idStudent: obj.getChildren()){
            try(PreparedStatement stmt = connection.prepareStatement(insertChildrenParents)){
                stmt.setInt(1, obj.getId());
                stmt.setInt(2, idStudent);
                stmt.executeUpdate();
            }catch(SQLException e) {
                throw new DatabaseException("DataBase Exception Error");
            }
        }


    }

    /**
     * Retrieves a parent object from the database by its ID.
     *
     * @param getId The ID of the parent to retrieve.
     * @return The parent object, or null if not found.
     * @throws DatabaseException If there is an error executing the SQL query.
     */
    @Override
    public Parent get(Integer getId) throws DatabaseException {
        String sql =  "SELECT * FROM dbo.Parent WHERE id = ?";
        try( PreparedStatement stmt = connection.prepareStatement(sql)){
            stmt.setInt(1, getId);
            try(ResultSet rs = stmt.executeQuery()){
                if(rs.next()){
                    List<Integer> children = getParentChildren(getId);
                    return extractParent(rs,children);
                }
            }catch (SQLException e){
                throw new DatabaseException("DataBase Exception Error");
            }

        }
        catch(Exception e){
            throw new DatabaseException("DataBase Exception Error");
        }
        return null;
    }


    /**
     * Retrieves all parent objects from the database.
     *
     * @return A list of all parent objects.
     * @throws DatabaseException If there is an error executing the SQL query.
     */
    @Override
    public List<Parent> getAll() throws DatabaseException {
        String sql =  "SELECT * FROM dbo.Parent";
        List<Parent> parents = new ArrayList<>();
        try( PreparedStatement stmt = connection.prepareStatement(sql)){
            try(ResultSet rs = stmt.executeQuery()){
                while(rs.next()){
                    List<Integer> children = getParentChildren(rs.getInt("id"));
                    parents.add(extractParent(rs,children));
                }
            }catch (SQLException e){
                throw new DatabaseException("DataBase Exception Error");
            }

        }
        catch(Exception e){
            throw new DatabaseException("DataBase Exception Error");
        }
        return parents;
    }

    /**
     * Retrieves the list of children associated with a specific parent by its ID.
     *
     * @param ParentId The ID of the parent.
     * @return A list of child IDs associated with the parent.
     * @throws DatabaseException If there is an error executing the SQL query.
     */
    public List<Integer> getParentChildren(int ParentId) throws DatabaseException {
        String sql = "SELECT * FROM ParentsStudents WHERE idParent = ?";
        List<Integer> children = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, ParentId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    children.add(rs.getInt("idStudent"));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("DataBase Exception Error");
        }
        return children;
    }

    /**
     * Extracts a parent object from the given ResultSet.
     *
     * @param rs       The ResultSet containing the parent data.
     * @param students The list of child IDs associated with the parent.
     * @return The extracted parent object.
     * @throws DatabaseException If there is an error processing the ResultSet.
     */
    public static Parent extractParent(ResultSet rs, List<Integer> students) throws DatabaseException{
        Parent parent = null;
        try {
            parent = new Parent(rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("lastName"),
                    rs.getString("email"),
                    rs.getString("address"),
                    rs.getString("number"));
        } catch (SQLException e) {
            throw new DatabaseException("DataBase Exception Error");
        }
        parent.setChildren(students);
        return parent;
    }

}
