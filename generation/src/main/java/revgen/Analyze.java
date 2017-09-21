package revgen;

import java.sql.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.Collections;

import revgen.SearchRunner;

/**
 * A class containing the main method for the "analyze" portion of the program.
 */
public class Analyze {
    public static void main(String[] args) {
        try {
            // Import the JDBC driver for the database
            Class.forName("org.mariadb.jdbc.Driver");

            // Connect to the database
            // Uses host='localhost'
            //      database='revgen'
            //      user='root'
            //      password='password'
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost/revgen?user=root&password=password");


            // Get the most recently generated run
            String latestRunSql = "SELECT id FROM runs ORDER BY id DESC LIMIT 1";
            Statement runStmt = con.createStatement();
            ResultSet runRs = runStmt.executeQuery(latestRunSql);
            runRs.next();

            // Set the run_id to be analyzed to the most recently generated run.
            // If you would like to analyze a specific run, hard code this to th
            // run's id
            long run_id = runRs.getLong("id");
            
            // Get the id of all simulations in the selected run
            String sql = "SELECT id FROM simulations WHERE run_id = ?";
            PreparedStatement stmt = con.prepareStatement(sql);
            stmt.setLong(1, run_id);
            ResultSet rs = stmt.executeQuery();

            // Create a thread pool with 4 threads. It is usually optimal to create
            // the same number of threads as cores on the current CPU.
            ExecutorService executor = Executors.newFixedThreadPool(4);

            // Count the number of simulations created/remaining
            // Atomic Integer is used to be threadsafe
            AtomicInteger simulations_remaining = new AtomicInteger(0);
            int totalSimulations = 0;

            // For each simulation in the run
            while (rs.next()) {

                // Get the simulation id
                long sim_id = rs.getLong("id");

                // Increment the number of simulations queued
                simulations_remaining.incrementAndGet();
                totalSimulations++;

                // Create a new executable SearchRunner job
                SearchRunner runner = new SearchRunner(con, sim_id, simulations_remaining);

                // Queue the job with the thread pool to be run in parallel 
                // with the other simulations
                executor.execute(runner);
            }

            // Tell the thread pool to not take any additional jobs and continue
            // running the analysis jobs
            executor.shutdown();
            System.out.println("Waiting for threads to finish");

            // Get the time that the analysis started to give an estimate of time remaining
            long startTime = System.currentTimeMillis();

            // Until all jobs are completed
            while(!executor.isTerminated()) { 

                // Wait for 1 second
                Thread.sleep(1000);

                // Print current progress
                printProgress(startTime, totalSimulations, totalSimulations - simulations_remaining.get());

            }

            System.out.println("All threads finished");


        } catch (Exception e) {
            System.out.println(e);
        }
    }

    /**
     * Prints the current progress of a long-running job
     */
    private static void printProgress(long startTime, long total, long current) {
        long eta = current == 0 ? 0 : 
            (total - current) * (System.currentTimeMillis() - startTime) / current;

        String etaHms = current == 0 ? "N/A" : 
            String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(eta),
                    TimeUnit.MILLISECONDS.toMinutes(eta) % TimeUnit.HOURS.toMinutes(1),
                    TimeUnit.MILLISECONDS.toSeconds(eta) % TimeUnit.MINUTES.toSeconds(1));

        StringBuilder string = new StringBuilder(140);   
        int percent = (int) (current * 100 / total);
        string
            .append('\r')
            .append(String.join("", Collections.nCopies(percent == 0 ? 2 : 2 - (int) (Math.log10(percent)), " ")))
            .append(String.format(" %d%% [", percent))
            .append(String.join("", Collections.nCopies(percent / 2, "=")))
            .append('>')
            .append(String.join("", Collections.nCopies((100 - percent + 1) / 2, " ")))
            .append(']')
            .append(String.join("", Collections.nCopies((int) (Math.log10(total)) - (int) (Math.log10(current)), " ")))
            .append(String.format(" %d/%d, ETA: %s", current, total, etaHms));

        System.out.print(string);
    }
}
