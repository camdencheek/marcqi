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

            Run run = new Run("MARCQI defaults, MARCQI sample size, 1/4 effect size");
            long runID = run.insert_db(con);

            ExecutorService executor = Executors.newFixedThreadPool(3);

            AtomicInteger simulationsRemaining = new AtomicInteger(0);
            int totalSimulations = 0;

            for (double theta_i0 = 0; theta_i0 <= 0.3; theta_i0 += 0.05) {
                for (double theta_i1 = 0; theta_i1 <= 0.3; theta_i1 += 0.05) {
                    ParameterSet params = new ParameterSet(
                            runID, // database ID of the current run
                            0.45, // Proportion of cases male
                            theta_i0, theta_i1, // θ_I_F and θ_I_M
                            0.71, 0.71, 0.71, 0.71, // alpha 00, 01, 10, 11
                            182, 45, 171, 43, // beta 00, 01, 10, 11
                            5,  // Length of study
                            //20863); // Number of cases per simulation
                            799); // Number of cases per simulation

                    params.insert_db(con);
                    con.commit();

                    CaseGenerator gen = new CaseGenerator(params);

                    int nSims = 1000;
                    for (int i = 0; i < nSims; i++) {
                        simulationsRemaining.incrementAndGet(); 
                        totalSimulations++;
                        SimulationRunner runner = new SimulationRunner(params, runID, con, simulationsRemaining);
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
            System.out.println("Generate: " + e);
        }
    }
}
