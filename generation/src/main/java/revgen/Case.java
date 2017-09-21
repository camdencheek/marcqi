package revgen;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;

/**
 * A single case
 */
public class Case {
    int sex;
    int implant;
    float ttr;

    // Constructor
    public Case(int csex, int cimplant, float cttr) {
        sex = csex;
        implant = cimplant;
        ttr = cttr;
    }

    // Add this case to a created batch statement...
    // Not a super elegant way to do this, but it's the best I could figure out
    // See `Simultion.java` for implementation
    public void add_to_batch(PreparedStatement stmt, long simulation_id) {
        try{
            stmt.setLong(1, simulation_id);
            stmt.setInt(2, sex);
            stmt.setInt(3, implant);
            stmt.setFloat(4, ttr);
            stmt.addBatch();
        } catch (Exception e) {
            System.out.println("2: " + e);
        }
    }
}
