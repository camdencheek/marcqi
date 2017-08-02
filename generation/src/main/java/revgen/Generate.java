package revgen;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.*;
import java.lang.Runtime;
import java.lang.Thread;
import java.io.*;
import revgen.CaseGenerator;
import revgen.ParameterSet;
import revgen.Run;
import revgen.utils.Utils;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class Generate {
    public static void main(String[] args) {
        try {
            Class.forName("org.mariadb.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost/revgen?user=root&password=password");
            con.setAutoCommit(false);

            Run run = new Run("Test Run");
            long run_id = run.insert_db(con);

            ExecutorService executor = Executors.newFixedThreadPool(3);

            AtomicInteger simulationsRemaining = new AtomicInteger(0);
            int totalSimulations = 0;

            double[] i0 = new double[] {0.0,0.05,0.1,0.15,0.2,0.25,0.3};
            double[] i1 = new double[] {0.0,0.05,0.1,0.15,0.2,0.25,0.3};
            for (double theta_i0 : i0) {
                for (double theta_i1 : i1) {
                    ParameterSet params = new ParameterSet(
                            run_id,
                            0.5,
                            theta_i0, theta_i1,
                            1.0, 1.5, 1.5, 2.0,
                            80, 100, 100, 120, 
                            10);

                    params.insert_db(con);
                    con.commit();

                    CaseGenerator gen = new CaseGenerator(params);

                    int nSims = 1000;
                    int nCases = 1000;
                    for (int i = 0; i < nSims; i++) {
                        simulationsRemaining.incrementAndGet(); 
                        totalSimulations++;
                        SimulationRunner runner = new SimulationRunner(params, nCases, run_id, con, simulationsRemaining);
                        executor.execute(runner);
                    }
                }
            }

            executor.shutdown();
            System.out.println("Waiting for threads to finish");

            long startTime = System.currentTimeMillis();
            while(!executor.isTerminated()) {
                Thread.sleep(1000);
                Utils.printProgress(startTime, totalSimulations, totalSimulations - simulationsRemaining.get());
            }

            System.out.println("All threads finished");


        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
