package com.gameservermanagers.JavaGSM.servers;

import com.gameservermanagers.JavaGSM.ServerInstaller;
import com.gameservermanagers.JavaGSM.util.DownloadUtil;
import org.jsoup.Jsoup;

import java.io.File;
import java.io.IOException;

public class Terraria implements ServerInstaller {

    public static void install(File destination) {
        System.out.print("Obtaining latest download url...");
        String latestDownloadUrl = null;

        try { latestDownloadUrl = "https://github.com" + Jsoup.connect("https://github.com/NyxStudios/TShock/releases/latest").ignoreContentType(true).get().html().split("<ul class=\"release-downloads\">")[1].split("<a href=\"")[1].split("\"")[0]; } catch (IOException e) { e.printStackTrace(); }
        if (latestDownloadUrl != null) {
            System.out.println(" " + latestDownloadUrl);
        } else {
            System.out.println("An error occurred during checking the latest version, aborting installation");
            return;
        }

        DownloadUtil.download(latestDownloadUrl, new File(destination, "tshock.zip"));
        DownloadUtil.unzip(new File(destination, "tshock.zip"));
        new File(destination, "tshock.zip").delete();

        System.out.println("Finished installing TShock server.");
    }

}
