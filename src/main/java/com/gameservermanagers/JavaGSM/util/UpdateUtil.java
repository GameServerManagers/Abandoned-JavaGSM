package com.gameservermanagers.JavaGSM.util;

import com.gameservermanagers.JavaGSM.JavaGSM;

import java.io.File;
import java.net.URISyntaxException;
import java.util.LinkedList;
import java.util.List;

public class UpdateUtil {

    public static void checkForUpdates() {
        JavaGSM.config.put("lastUpdateCheck", System.currentTimeMillis());
        JavaGSM.saveConfig();

        System.out.print("Checking for updates...");
        String latest = DownloadUtil.getUrlAsString("https://raw.githubusercontent.com/" + JavaGSM.config.get("repo") + "/master/latest").trim();
        System.out.print(" latest: " + latest);

        List<Integer> currentNumbers = new LinkedList<>();
        for (String s : JavaGSM.version.split("\\.")) currentNumbers.add(Integer.valueOf(s));
        List<Integer> latestNumbers = new LinkedList<>();
        for (String s : latest.split("\\.")) latestNumbers.add(Integer.valueOf(s));

        List<Boolean> results = new LinkedList<>();
        for (int i = 0; i < 3; i++) results.add(currentNumbers.get(i) < latestNumbers.get(i));
        boolean updateAvailable =
                results.get(0) ||
                (!results.get(0) && results.get(1)) ||
                (!results.get(0) && !results.get(1) && results.get(2))
        ;

        if (!updateAvailable) {
            System.out.println(" | no update available");
            return;
        }

        System.out.println(" | update available");

        try {
            String latestUrl = "http://scarsz.tech:8080/job/JavaGSM/lastSuccessfulBuild/artifact/target/JavaGSM.jar";
            File destination = new File(JavaGSM.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath());

            // this loads needed classes to update to the JVM because they won't be available after the jar is overwritten
            try {
                Class.forName("com.gameservermanagers.JavaGSM.util.RuntimeUtil");
                Class.forName("com.gameservermanagers.JavaGSM.util.StreamGobbler");
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

            DownloadUtil.download(latestUrl, destination);

            // TODO: spawn child process with same arguments as this process
            RuntimeUtil.runProcess("java -jar " + destination.getAbsolutePath());

            System.exit(0);
        } catch (URISyntaxException e) {
            e.printStackTrace();
            System.out.println("\"This will literally never happen\" they said. \"Don't even write a message for it,\" they said.");
        }
    }

}
