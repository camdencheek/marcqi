package revgen.utils;

import java.util.Collections;
import java.util.concurrent.*;

public class Utils {

    public static void printProgress(long startTime, long total, long current) {
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
            .append(String.format("%d%%", percent))
            .append(String.format(" %d/%d, ETA: %s", current, total, etaHms));

        System.out.print(string);
    }
}
