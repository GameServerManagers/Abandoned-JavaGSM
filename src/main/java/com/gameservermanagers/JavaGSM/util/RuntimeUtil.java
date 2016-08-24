package com.gameservermanagers.JavaGSM.util;

import java.io.File;
import java.io.IOException;

@SuppressWarnings("WeakerAccess")
public class RuntimeUtil {

    public static Process runProcess(String command) {
        return runProcess(command, new File("."));
    }
    public static Process runProcess(String command, File directory) {
        try {
            System.out.println("Running command \"" + command + "\"");
            return Runtime.getRuntime().exec(command, null, directory);
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
