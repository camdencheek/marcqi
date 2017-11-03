package revgen;

import java.sql.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.Collections;


import revgen.SearchRunner;
import revgen.Simulation;
import revgen.Case;

/**
 * A class containing the main method for the "analyze" portion of the program.
 */
public class Analyze {
    public static void analyze(Simulation sim) {
        try {
            // Import the JDBC driver for the database
            Class.forName("org.mariadb.jdbc.Driver");

            // Connect to the database
            // Uses host='localhost'
            //      database='revgen'
            //      user='root'
            //      password='password'
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost/revgen?user=root&password=password");


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
