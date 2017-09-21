package revgen;

import java.util.*;
import edu.cmu.tetrad.graph.*;
import edu.cmu.tetrad.data.*;
import edu.cmu.tetrad.search.*;
import java.lang.Runnable;
import java.sql.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A runnable class that allows the job to be passed around
 * in a thread pool.
 */
public class SearchRunner implements Runnable {
    long simulation_id;
    Connection con;
    Graph result_graph;
    AtomicInteger simulations_remaining;
    

    // Constructor
    public SearchRunner(Connection c, long s, AtomicInteger r) {
        simulation_id = s;
        con = c;
        simulations_remaining = r;
    }

    // Method to run when the job is executed
    public void run() {
        BoxDataSet dataset = getData();
        result_graph = runSearch(dataset);
        insertGraph(result_graph);
        simulations_remaining.decrementAndGet();

    }


    // Get generated simulation data from the database
    // Put it in a BoxDataSet to be used by TETRAD
    private BoxDataSet getData() {
        
        // Create BoxDataSet
        BoxDataSet returnBox = BoxDataSet.serializableInstance();
        try {
            
            // SQL query to get generated parameters of interest
            String sql = "SELECT sex, implant, ttr, (ttr > 10) as ttr_discretized " +
                "FROM cases WHERE simulation_id = ?";
            PreparedStatement stmt = con.prepareStatement(sql);
            stmt.setLong(1, simulation_id);
            ResultSet rs = stmt.executeQuery();

            // Count the number of results
            int returned_rows = 0;
            while (rs.next()) {
                returned_rows++;
            }

            // Create a list of nodes corresponding to the variables in the network
            List<Node> node_list_tmp = new ArrayList<>();
            List<Node> node_list = Collections.synchronizedList(node_list_tmp);
            DiscreteVariable sex = new DiscreteVariable("sex", Arrays.asList("0","1"));
            DiscreteVariable implant = new DiscreteVariable("implant", Arrays.asList("0","1"));
            DiscreteVariable ttr = new DiscreteVariable("ttr", Arrays.asList("0","1"));
            node_list.add(sex);
            node_list.add(implant);
            node_list.add(ttr);
            
            // Create a databox
            VerticalIntDataBox dataBox = new VerticalIntDataBox(returned_rows, 3);

            // Set insert the values into the databox
            rs.beforeFirst();
            int i = 0;
            while (rs.next()) {
                dataBox.set(i, 0, rs.getInt("sex"));
                dataBox.set(i, 1, rs.getInt("implant"));
                dataBox.set(i, 2, rs.getInt("ttr_discretized"));
                i++;
            }

            // Return the databox
            returnBox = new BoxDataSet(dataBox, node_list);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return returnBox;
    }

    // Run a search on the graph
    private Graph runSearch(BoxDataSet data) {

        // Do independence tests with Chi Square
        IndTestChiSquare indTest = new IndTestChiSquare(data, 0.05);

        // Create a PC search object
        Pc pc = new Pc(indTest);

        // Run the search
        Graph tetradGraph = pc.search();

        // Return the resulting graph
        return tetradGraph;
    }


    // Insert the resulting graph into the database
    private void insertGraph(Graph graph) {
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
            stmt.setLong(1, simulation_id);
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
