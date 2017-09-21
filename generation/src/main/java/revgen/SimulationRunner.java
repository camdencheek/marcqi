package revgen;

import revgen.ParameterSet;
import java.sql.Connection;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A executable class that wraps the code for running a simulation 
 * so it can be passed around to a thread pool.
 */
public class SimulationRunner implements Runnable {
    ParameterSet params;
    long run_id;
    Connection con;
    AtomicInteger simulations_remaining;

    // Constructor
    public SimulationRunner(ParameterSet p, long r, Connection c, AtomicInteger s) {
        params = p;
        run_id = r;
        con = c;
        simulations_remaining = s;
    }

    // The method to run to generate the simulation
    public void run() {
            Simulation sim = new Simulation(params);
            sim.insert_db(con, run_id);
            simulations_remaining.decrementAndGet();

    }
}
