package revgen;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.ResultSet;

/**
 * A class describing a Run, which is a collection of 
 * simulations for a set of varied parameter sets. 
 *
 * A run is the topmost organizational unit in this package.
 */
public class Run {

    // A simple description of of the current run
    String description;

    // Constructor for a Run
    Run(String desc) {
        description = desc;
    }

    // Insert the run into the database. 
    // Returns the id of the inserted run
    public long insert_db(Connection con) {

        // The query to insert it
        // The only non-automatic field is the description
        String query = "insert into runs values (default, default, ?)";
        long id = 0;
        try {
            // Create a sql statement from the string
            PreparedStatement stmt = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);

            // Insert the description into the query
            stmt.setString(1, description);

            // Execute the sql query
            stmt.executeUpdate();

            // Get the id from the result
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    id =  generatedKeys.getLong(1);
                }
            } catch (Exception e) {
                System.out.println(e);
            }
        } catch (Exception f) {
            System.out.println(f); 
        }

        // Return the id of the run
        return id;
    }
}
