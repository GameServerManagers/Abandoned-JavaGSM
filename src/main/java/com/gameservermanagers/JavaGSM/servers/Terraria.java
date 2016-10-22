package com.gameservermanagers.JavaGSM.servers;

import com.gameservermanagers.JavaGSM.ServerInstaller;
import com.gameservermanagers.JavaGSM.util.DownloadUtil;

import java.io.File;

public class Terraria implements ServerInstaller {

    public static void install(File destination) {
        System.out.print("Obtaining latest download url...");
        //TODO: switch this to use github api at https://api.github.com/repos/NyxStudios/TShock/releases/latest
        String latestDownloadUrl = "https://github.com" + DownloadUtil.getUrlAsString("https://github.com/NyxStudios/TShock/releases/latest").split("<ul class=\"release-downloads\">")[1].split("<a href=\"")[1].split("\"")[0];
        System.out.println(" " + latestDownloadUrl);

        DownloadUtil.download(latestDownloadUrl, new File(destination, "tshock.zip"));
        DownloadUtil.unzip(new File(destination, "tshock.zip"));
        new File(destination, "tshock.zip").delete();

        System.out.println("Finished installing TShock server.");
    }

}
