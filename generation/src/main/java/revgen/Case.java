package revgen;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;

public class Case {
    int sex;
    int implant;
    float ttr;

    public Case(int csex, int cimplant, float cttr) {
        sex = csex;
        implant = cimplant;
        ttr = cttr;
    }

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
