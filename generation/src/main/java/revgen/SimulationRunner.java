package revgen;

import revgen.ParameterSet;
import java.sql.Connection;
import java.util.concurrent.atomic.AtomicInteger;

public class SimulationRunner implements Runnable {
    ParameterSet params;
    long run_id;
    Connection con;
    AtomicInteger simulations_remaining;

    public SimulationRunner(ParameterSet p, long r, Connection c, AtomicInteger s) {
        params = p;
        run_id = r;
        con = c;
        simulations_remaining = s;
    }

    public void run() {
            Simulation sim = new Simulation(params);
            sim.insert_db(con, run_id);
            simulations_remaining.decrementAndGet();

    }
}
