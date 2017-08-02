package revgen;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.ResultSet;

public class Run {
    String description;

    Run(String desc) {
        description = desc;
    }

    public long insert_db(Connection con) {
        String query = "insert into runs values (default, default, ?)";
        long id = 0;
        try {
            PreparedStatement stmt = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);

            stmt.setString(1, description);

            stmt.executeUpdate();

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
        return id;
    }
}
