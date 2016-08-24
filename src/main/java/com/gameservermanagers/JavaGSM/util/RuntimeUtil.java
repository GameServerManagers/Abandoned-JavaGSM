package com.gameservermanagers.JavaGSM.util;

import java.io.IOException;

public class RuntimeUtil {

    public static Process runProcess(String command) {
        try {
            System.out.println("Running command \"" + command + "\"");
            return Runtime.getRuntime().exec(command);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Process runProcessWaitFor(String command) {
        try {
            Process p = runProcess(command);
            if (p != null) p.waitFor();
            return p;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

}
