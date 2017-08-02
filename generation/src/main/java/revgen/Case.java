package revgen;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;

public class Case {
    int sex;
    int implant;
    double ttr;
    double ttc;

    public Case(int csex, int cimplant, double cttr, double cttc) {
        sex = csex;
        implant = cimplant;
        ttr = cttr;
        ttc = cttc;
    }

    public void add_to_batch(PreparedStatement stmt, long simulation_id) {
        String query = "insert into cases values (default, ?, ?, ?, ?, ?)";
        try{
            stmt.setLong(1, simulation_id);
            stmt.setInt(2, sex);
            stmt.setInt(3, implant);
            stmt.setDouble(4, ttr);
            stmt.setDouble(5, ttc);
            stmt.addBatch();
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
