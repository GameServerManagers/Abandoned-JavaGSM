package com.gameservermanagers.JavaGSM.util;

import java.io.File;
import java.io.IOException;

@SuppressWarnings("WeakerAccess")
public class RuntimeUtil {

    public static Process runProcess(String command) {
        return runProcess(command, new File("."));
    }
    public static Process runProcess(String command, File directory) {
        return runProcess(command, directory, true);
    }
    public static Process runProcess(String command, File directory, boolean gobble) {
            try {
            System.out.println("Running command \"" + command + "\"");
            Process p = Runtime.getRuntime().exec(command, null, directory);

            if (p != null && gobble) {
                StreamGobbler outputGobbler = new StreamGobbler(p.getInputStream());
                outputGobbler.start();
                StreamGobbler errorGobbler = new StreamGobbler(p.getErrorStream());
                errorGobbler.start();
            }

            return p;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Process runProcessWaitFor(String command) {
        return runProcessWaitFor(command, new File("."));
    }
    public static Process runProcessWaitFor(String command, File directory) {
        try {
            Process p = runProcess(command, directory);
            if (p != null) p.waitFor();
            return p;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

}
