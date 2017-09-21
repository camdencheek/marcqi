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
            Run run = new Run("1D variable effect size, NJR sample size, 0.02 thetas");

            // Insert the run into the database and get the run id
            long runID = run.insert_db(con);

            // Create a thread pool for parallel execution. 
            // Usually optimal to have the same number of threads as CPU cores
            ExecutorService executor = Executors.newFixedThreadPool(4);

            // Counter for number of simluations
            AtomicInteger simulationsRemaining = new AtomicInteger(0);
            int totalSimulations = 0;

            // For an effect size from 1 to 4
            for (double effect_size = 1.0; effect_size <= 4.0; effect_size += 0.25) {

                // Create a parameter set with the variable effect size and other 
                // parameters fixed
                ParameterSet params = new ParameterSet(
                        runID, // database ID of the current run
                        0.45, // Proportion of cases male
                        0.02, 0.02, // θ_I_F and θ_I_M
                        0.71, 0.71, 0.71, 0.71, // alpha 00, 01, 10, 11
                        182, 182.0 / effect_size, 171, 171.0 / effect_size, // beta 00, 01, 10, 11
                        5,  // Length of study
                        20863); // Number of cases per simulation NJR size
                        //799); // Number of cases per simulation MARCQI size

                // Insert the parameter set into the DB
                params.insert_db(con);
                con.commit();

                // Create a new CaseGenerator with the parameter set
                CaseGenerator gen = new CaseGenerator(params);

                // Replicate each simulation 1000 times
                int nSims = 1000;
                for (int i = 0; i < nSims; i++) {
                    
                    // Increment number of simulations
                    simulationsRemaining.incrementAndGet(); 
                    totalSimulations++;

                    // Create an executable SimulationRunner
                    SimulationRunner runner = new SimulationRunner(params, runID, con, simulationsRemaining);

                    // Add the SimulationRunner to the job queue
                    executor.execute(runner);
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
                Utils.printProgress(startTime, totalSimulations, totalSimulations - simulationsRemaining.get());
            }

            System.out.println("All threads finished");


        } catch (Exception e) {
            System.out.println("Generate: " + e);
        }
    }
}
