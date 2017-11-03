package revgen;

import revgen.ParameterSet;
import java.sql.Connection;
import java.util.concurrent.atomic.AtomicInteger;
import edu.cmu.tetrad.graph.*;


/**
 * A executable class that wraps the code for running a simulation 
 * so it can be passed around to a thread pool.
 */
public class SimulationRunner implements Runnable {
    ParameterSet params;
    long run_id;
    Connection con;

    // Constructor
    public SimulationRunner(ParameterSet p, long r, Connection c) {
        params = p;
        run_id = r;
        con = c;
    }

    // The method to run to generate the simulation
    public void run() {
            Simulation sim = new Simulation(params);
            sim.insertSim(con, run_id);
            Graph resultGraph = sim.analyze();
            sim.insertGraph(con, resultGraph);
    }
}
