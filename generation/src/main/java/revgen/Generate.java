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
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.Executors;

/** 
 * A class containing the main method for generating the simulated patients
 */
public class Generate {
    public static void main(String[] args) {

        try {

            // Import the database driver
            Class.forName("org.mariadb.jdbc.Driver");

            // Create the connection to the database
            // host='localhost', database='revgen', user='root', password='password'
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost/revgen?user=root&password=password");

            // Do not automatically commit executed SQL statements
            // Allows for batched commits, speeding up network bottleneck
            con.setAutoCommit(false);

            // Create a new run with the given description
            Run run = new Run("Test without inserting cases");

            // Insert the run into the database and get the run id
            long runID = run.insert_db(con);

            // Create a thread pool for parallel execution. 
            // Usually optimal to have the same number of threads as CPU cores
            ExecutorService executor = Executors.newFixedThreadPool(4);

            // Counter for number of simluations
            int totalSimulations = 0;

            for (double theta_i0 = 0; theta_i0 <= 0.3; theta_i0 += 0.01) {
                for (double theta_i1 = 0; theta_i1 <= 0.3; theta_i1 += 0.01) {
                    ParameterSet params = new ParameterSet(
                            runID, // database ID of the current run
                            0.45, // Proportion of cases male
                            theta_i0, theta_i1, // θ_I_F and θ_I_M
                            0.71, 0.71, 0.71, 0.71, // alpha 00, 01, 10, 11
                            182, 91, 171, 85.5, // beta 00, 01, 10, 11
                            5,  // Length of study
                            //20863); // Number of cases per simulation
                            799); // Number of cases per simulation

                    params.insert_db(con);
                    con.commit();

                    CaseGenerator gen = new CaseGenerator(params);

                    int nSims = 10000;
                    for (int i = 0; i < nSims; i++) {
                        totalSimulations++;
                        SimulationRunner runner = new SimulationRunner(params, runID, con);
                        executor.execute(runner);
                    }
                }
            }

            // Tell the thread pool to stop accepting jobs
            executor.shutdown();
            System.out.println("Waiting for threads to finish");

            // Get the start time of the generation
            long startTime = System.currentTimeMillis();

            // Update the printed generation progress every second
            while(!executor.isTerminated()) {
                Thread.sleep(1000);
                long simsRemaining = ((ThreadPoolExecutor) executor).getQueue().size();
                Utils.printProgress(startTime, totalSimulations, totalSimulations - simsRemaining);
            }

            System.out.println("All threads finished");


        } catch (Exception e) {
            System.out.println("Generate: " + e);
        }
    }
}
