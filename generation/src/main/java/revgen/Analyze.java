package revgen;

import java.sql.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.Collections;

import revgen.SearchRunner;

public class Analyze {
    public static void main(String[] args) {
        try {
            // Import the JDBC driver for the database
            Class.forName("org.mariadb.jdbc.Driver");

            // Connect to the database
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost/revgen?user=root&password=password");


            long run_id = 11;

            String sql = "SELECT id FROM simulations WHERE run_id = ?";
            PreparedStatement stmt = con.prepareStatement(sql);

            stmt.setLong(1, run_id);
            ResultSet rs = stmt.executeQuery();

            ExecutorService executor = Executors.newFixedThreadPool(3);

            AtomicInteger simulations_remaining = new AtomicInteger(0);
            int totalSimulations = 0;

            while (rs.next()) {
                long sim_id = rs.getLong("id");
                simulations_remaining.incrementAndGet();
                totalSimulations++;
                SearchRunner runner = new SearchRunner(con, sim_id, simulations_remaining);
                executor.execute(runner);
            }

            executor.shutdown();
            System.out.println("Waiting for threads to finish");

            long startTime = System.currentTimeMillis();
            while(!executor.isTerminated()) { 
                Thread.sleep(1000);
                printProgress(startTime, totalSimulations, totalSimulations - simulations_remaining.get());

            }

            System.out.println("All threads finished");


        } catch (Exception e) {
            System.out.println(e);
        }
    }

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
