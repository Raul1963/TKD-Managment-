package org.example.Repository;

import org.example.Exceptions.DatabaseException;
import org.example.Model.BeltLevel;
import org.example.Model.Contest;
import org.example.Model.Trainer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * A repository implementation for managing `Trainer` entities in the database.
 */
public class DatabaseTrainer extends DatabaseRepo<org.example.Model.Trainer>{

    /**
     * Constructs a new `DatabaseTrainer` with the specified database URL.
     *
     * @param dbUrl The URL of the database to connect to.
     * @throws DatabaseException If there is an error connecting to the database.
     */
    public DatabaseTrainer(String dbUrl) throws DatabaseException {
        super(dbUrl);
    }

    /**
     * Adds a new `Trainer` object to the database.
     *
     * @param obj The `Trainer` object to be added.
     * @throws DatabaseException If there is an error executing the SQL query.
     */
    @Override
    public void add(Trainer obj) throws DatabaseException {
        String sql =  "INSERT INTO dbo.Trainer (id,name,lastName, email, address, dateOfBirth, number, beltLevel) Values (?,?,?,?,?,?,?,?)";
        try(PreparedStatement stmt = connection.prepareStatement(sql)){
            stmt.setInt(1, obj.getId());
            stmt.setString(2, obj.getName());
            stmt.setString(3, obj.getLastName());
            stmt.setString(4, obj.getEmail());
            stmt.setString(5, obj.getAddress());
            stmt.setInt(6,obj.getDateOfBirth());
            stmt.setString(7, obj.getNumber());
            stmt.setString(8, String.valueOf(obj.getBeltLevel()));
            stmt.executeUpdate();
        }catch(Exception e){
            throw new DatabaseException("DataBase Exception Error");
        }
    }

    /**
     * Removes a `Trainer` object from the database by its ID.
     *
     * @param removeId The ID of the trainer to be removed.
     * @throws DatabaseException If there is an error executing the SQL query.
     */
    @Override
    public void remove(Integer removeId) throws DatabaseException {
        String sql =  "DELETE FROM dbo.Trainer WHERE id = ?";
        try(PreparedStatement stmt = connection.prepareStatement(sql)){
            stmt.setInt(1, removeId);
            stmt.executeUpdate();
        }
        catch(Exception e){
            throw new DatabaseException("DataBase Exception Error");
        }
    }

    /**
     * Updates an existing `Trainer` object in the database.
     *
     * @param obj The `Trainer` object to update.
     * @throws DatabaseException If there is an error executing the SQL query.
     */
    @Override
    public void update(Trainer obj) throws DatabaseException{
        String sql = "UPDATE dbo.Trainer SET name = ?, lastName = ?, email = ?, address = ?, " +
                "dateOfBirth = ?, number = ?, beltLevel = ? WHERE id = ?";
        try(PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, obj.getName());
            stmt.setString(2, obj.getLastName());
            stmt.setString(3, obj.getEmail());
            stmt.setString(4, obj.getAddress());
            stmt.setInt(5, obj.getDateOfBirth());
            stmt.setString(6, obj.getNumber());
            stmt.setString(7, String.valueOf(obj.getBeltLevel()));
            stmt.setInt(8, obj.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("DataBase Exception Error");
        }
    }

    /**
     * Retrieves a `Trainer` object from the database by its ID.
     *
     * @param getId The ID of the trainer to retrieve.
     * @return The `Trainer` object, or null if not found.
     * @throws DatabaseException If there is an error executing the SQL query.
     */
    @Override
    public Trainer get(Integer getId) throws DatabaseException {
        String sql =  "SELECT * FROM dbo.Trainer WHERE id = ?";
        try(PreparedStatement stmt = connection.prepareStatement(sql)){
            stmt.setInt(1, getId);
            try(ResultSet rs = stmt.executeQuery()){
                if(rs.next()){
                    return new Trainer(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getString("lastName"),
                            rs.getString("email"),
                            rs.getString("address"),
                            rs.getInt("dateOfBirth"),
                            rs.getString("number"),
                            BeltLevel.valueOf(rs.getString("beltLevel"))
                    );
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
     * Retrieves all `Trainer` objects from the database.
     *
     * @return A list of all `Trainer` objects.
     * @throws DatabaseException If there is an error executing the SQL query.
     */
    @Override
    public List<Trainer> getAll() throws DatabaseException {
        String sql =  "SELECT * FROM dbo.Trainer";
        List<Trainer> trainers = new ArrayList<>();
        try(PreparedStatement stmt = connection.prepareStatement(sql)){
            try(ResultSet rs = stmt.executeQuery()){
                while(rs.next()){
                    trainers.add(new Trainer(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getString("lastName"),
                            rs.getString("email"),
                            rs.getString("address"),
                            rs.getInt("dateOfBirth"),
                            rs.getString("number"),
                            BeltLevel.valueOf(rs.getString("beltLevel"))
                    ));
                }
            }catch (SQLException e){
                throw new DatabaseException("DataBase Exception Error");
            }

        }
        catch(Exception e){
            throw new DatabaseException("DataBase Exception Error");
        }
        return trainers;
    }


}
