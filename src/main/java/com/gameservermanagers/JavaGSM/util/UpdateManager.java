package com.gameservermanagers.JavaGSM.util;

import com.gameservermanagers.JavaGSM.JavaGSM;
import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

public class UpdateManager {

    public static void checkForUpdates() {
        System.out.println("Checking for updates...");

        String latest = ""; try { latest = Jsoup.connect("https://raw.githubusercontent.com/GameServerManagers/JavaGSM/master/latest").get().html().split("  ")[1].split("\\n")[0].replace(" ", ""); } catch (IOException e) { System.out.println("An unknown error occurred while getting the latest version"); e.printStackTrace(); }

        System.out.println("Current: " + JavaGSM.version);
        System.out.println("Latest: " + latest);

        List<Integer> currentNumbers = new LinkedList<>();
        for (String s : JavaGSM.version.split("\\.")) currentNumbers.add(Integer.valueOf(s));
        List<Integer> latestNumbers = new LinkedList<>();
        for (String s : latest.split("\\.")) latestNumbers.add(Integer.valueOf(s));

        boolean updateAvailable = false;
        for (int i = 0; i < 3; i++) if (currentNumbers.get(i) < latestNumbers.get(i)) updateAvailable = true;

        if (!updateAvailable) {
            System.out.println("No update available");
            System.out.println();
            return;
        }

        System.out.println("Update available. Downloading the latest jar...");

        String latestUrl = "";
        try {
            FileUtils.copyURLToFile(new URL(latestUrl), new File(JavaGSM.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath()));
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
    }

}
