package com.gameservermanagers.JavaGSM.util;

import java.io.IOException;

public class RuntimeUtil {

    public static Process runProcess(String command) {
        try {
            return Runtime.getRuntime().exec(command);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Process runProcessWaitFor(String command) {
        try {
            Process p = Runtime.getRuntime().exec(command);
            p.waitFor();
            return p;
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

}
