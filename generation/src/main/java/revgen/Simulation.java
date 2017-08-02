package revgen;

import revgen.ParameterSet;
import revgen.CaseGenerator;
import revgen.Case;
import java.util.ArrayList;
import java.util.List;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.ResultSet;

public class Simulation {
    ParameterSet params;
    int nCases;
    List<Case> cases;

    public Simulation(ParameterSet p, int n) {
        params = p;
        nCases = n;

        CaseGenerator gen = new CaseGenerator(params);
        cases = gen.generate(nCases);
    }


    public void insert_db(Connection con, long run_id) {
        String query = "insert into simulations values (default, ?, ?)";
        try {
            PreparedStatement stmt = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);

            stmt.setLong(1, run_id);
            stmt.setLong(2, params.id);

            stmt.executeUpdate();
            con.commit();

            long simulation_id = 0;
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    simulation_id = generatedKeys.getLong(1);
                }
            } catch (Exception f) {
                System.out.println(f);
            }

            PreparedStatement case_stmt = con.prepareStatement("insert into cases values (default, ?, ?, ?, ?, ?)");
            for (Case iCase : cases) {
                iCase.add_to_batch(case_stmt, simulation_id);
            }
            case_stmt.executeBatch();
            con.commit();

        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
