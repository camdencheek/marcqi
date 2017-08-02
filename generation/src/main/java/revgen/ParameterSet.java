package revgen;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.ResultSet;

public class ParameterSet {
    public long id = 0;
    public long run_id;
    public double theta_s;
    public double[] theta_i;
    public double[][] alpha;
    public double[][] beta;
    public double study_length;

    public void insert_db(Connection con) {
        String query = "insert into parameter_sets values (default,default,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        try {
            PreparedStatement stmt = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);

            stmt.setLong(1, run_id);
            stmt.setDouble(2, theta_s);
            stmt.setDouble(3, theta_i[0]);
            stmt.setDouble(4, theta_i[1]);
            stmt.setDouble(5, alpha[0][0]);
            stmt.setDouble(6, alpha[0][1]);
            stmt.setDouble(7, alpha[1][0]);
            stmt.setDouble(8, alpha[1][1]);
            stmt.setDouble(9, beta[0][0]);
            stmt.setDouble(10, beta[0][1]);
            stmt.setDouble(11, beta[1][0]);
            stmt.setDouble(12, beta[1][1]);
            stmt.setDouble(13, study_length);

            stmt.executeUpdate();

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    id = generatedKeys.getLong(1);
                }
            } catch (Exception e) {
                System.out.println(e);
            }

        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public ParameterSet(long crun_id, double ctheta_s, double ctheta_i0, double ctheta_i1, 
            double calpha00, double calpha01, double calpha10, double calpha11,
            double cbeta00, double cbeta01, double cbeta10, double cbeta11, double cstudy_length) {

        run_id = crun_id;
        theta_s = ctheta_s;
        theta_i = new double[] {ctheta_i0, ctheta_i1};
        alpha = new double[][] {{calpha00,calpha01},{calpha10,calpha11}};
        beta = new double[][] {{cbeta00,cbeta01},{cbeta10,cbeta11}};
        study_length = cstudy_length;

    }

}
