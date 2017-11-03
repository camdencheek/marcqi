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
import java.lang.Thread;
import java.util.*;
import edu.cmu.tetrad.graph.*;
import edu.cmu.tetrad.data.*;
import edu.cmu.tetrad.search.*;
import java.lang.Runnable;
import java.sql.*;
import java.util.concurrent.atomic.AtomicInteger;



public class Simulation {
    long id;
    ParameterSet params;
    List<Case> cases;

    public Simulation(ParameterSet p) {
        params = p;

        CaseGenerator gen = new CaseGenerator(params);
        cases = gen.generate();
        id = 0;
    }

    public void insertSim(Connection con, long run_id) {
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
                    this.id = simulation_id;
                }
            } catch (Exception f) {
                System.out.println(f);
            }

        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public Graph analyze() {
        BoxDataSet returnBox = BoxDataSet.serializableInstance();

        List<Node> node_list_tmp = new ArrayList<>();
        List<Node> node_list = Collections.synchronizedList(node_list_tmp);
        DiscreteVariable sex = new DiscreteVariable("sex", Arrays.asList("0","1"));
        DiscreteVariable implant = new DiscreteVariable("implant", Arrays.asList("0","1"));
        DiscreteVariable ttr = new DiscreteVariable("ttr", Arrays.asList("0","1"));
        node_list.add(sex);
        node_list.add(implant);
        node_list.add(ttr);

        VerticalIntDataBox dataBox = new VerticalIntDataBox(cases.size(), 3);


        int i = 0;
        for (Case icase: this.cases) {
            dataBox.set(i, 0, icase.sex);
            dataBox.set(i, 1, icase.implant);
            dataBox.set(i, 2, icase.ttr > 10 ? 1 : 0);
            i++;
        }
        BoxDataSet myBoxDataSet = new BoxDataSet(dataBox, node_list);

        IndTestChiSquare indTest = new IndTestChiSquare(myBoxDataSet, 0.05);

        Pc pc = new Pc(indTest);

        Graph tetradGraph = pc.search();

        return tetradGraph;
    }

    public void insertGraph(Connection con, Graph graph) {
        try {

            // SQL query to insert graph
            String sql = "INSERT INTO graph_results VALUES (?, ?, ?, ?)" +
                " ON DUPLICATE KEY UPDATE sex_implant = ?, sex_ttr = ?, implant_ttr = ?;";
            PreparedStatement stmt = con.prepareStatement(sql);

            // Get each of the variable nodes
            Node ttr = graph.getNode("ttr");
            Node sex = graph.getNode("sex");
            Node implant = graph.getNode("implant");

            int sex_implant = -1;
            int sex_ttr = -1;
            int implant_ttr = -1;

            // Encode the graph connections. 
            // 0 = no edge
            // 1 = undirected edge
            // 2 = correct direction
            // 3 = incorrect direction
            if (graph.isAdjacentTo(sex, implant)) {
                sex_implant = 1;
                if (graph.isParentOf(sex, implant)) {
                    sex_implant = 2;
                } else if (graph.isChildOf(sex, implant)) {
                    sex_implant = 3;
                }
            } else {
                sex_implant = 0;
            }
            if (graph.isAdjacentTo(sex, ttr)) {
                sex_ttr = 1;
                if (graph.isParentOf(sex, ttr)) {
                    sex_ttr = 2;
                } else if (graph.isChildOf(sex, ttr)) {
                    sex_ttr = 3;
                }
            } else {
                sex_ttr = 0;
            }       
            if (graph.isAdjacentTo(implant, ttr)) {
                implant_ttr = 1;
                if (graph.isParentOf(implant, ttr)) {
                    implant_ttr = 2;
                } else if (graph.isChildOf(implant, ttr)) {
                    implant_ttr = 3;
                }
            } else {
                implant_ttr = 0;
            }       


            // Insert the calculated values into the SQL statement
            stmt.setLong(1, this.id);
            stmt.setInt(2,sex_implant);
            stmt.setInt(3,sex_ttr);
            stmt.setInt(4,implant_ttr);
            stmt.setInt(5,sex_implant);
            stmt.setInt(6,sex_ttr);
            stmt.setInt(7,implant_ttr);

            stmt.executeQuery();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
