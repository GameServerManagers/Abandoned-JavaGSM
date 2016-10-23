package com.gameservermanagers.JavaGSM.servers;

import com.gameservermanagers.JavaGSM.JavaGSM;
import com.gameservermanagers.JavaGSM.ServerInstaller;
import com.gameservermanagers.JavaGSM.util.DownloadUtil;
import com.google.gson.internal.LinkedTreeMap;

import java.io.File;
import java.util.List;

public class Terraria extends ServerInstaller {

    public static void install(File destination) {
        System.out.print("Obtaining latest download url...");
        File tShockArchive = new File(destination, "tshock.zip");
        List<LinkedTreeMap<String, Object>> assetsFromApi = (List<LinkedTreeMap<String, Object>>) ((LinkedTreeMap<String, Object>) JavaGSM.gson.fromJson(DownloadUtil.getUrlAsString("https://api.github.com/repos/NyxStudios/TShock/releases/latest"), LinkedTreeMap.class)).get("assets");
        for (LinkedTreeMap<String, Object> asset : assetsFromApi) {
            DownloadUtil.download((String) asset.get("browser_download_url"), tShockArchive);
            DownloadUtil.unzip(tShockArchive);
            DownloadUtil.deleteFile(tShockArchive);
        }
        System.out.println("Finished installing TShock server. Start it with the -s flag.");
    }

}
