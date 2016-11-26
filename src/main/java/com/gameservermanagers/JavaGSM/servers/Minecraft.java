package com.gameservermanagers.JavaGSM.servers;

import com.gameservermanagers.JavaGSM.JavaGSM;
import com.gameservermanagers.JavaGSM.ServerInstaller;
import com.gameservermanagers.JavaGSM.util.ConfigUtil;
import com.gameservermanagers.JavaGSM.util.DownloadUtil;
import com.gameservermanagers.JavaGSM.util.RuntimeUtil;
import com.gameservermanagers.JavaGSM.util.UserInputUtil;
import com.google.gson.internal.LinkedTreeMap;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

@SuppressWarnings({"unused", "WeakerAccess"})
public class Minecraft extends ServerInstaller {

    private static String getSoftware(String prompt, String filter) {
        // populate possible server software
        List<String> availableServerSoftware = new LinkedList<>();
        for (Method method : Minecraft.class.getDeclaredMethods())
            if (method.getName().startsWith(filter)) availableServerSoftware.add(method.getName().substring(8));
        Collections.sort(availableServerSoftware);

        return availableServerSoftware.get(UserInputUtil.questionList(prompt, availableServerSoftware));
    }

    public static void install(File destination) {
        String requestedSoftware = getSoftware("Which server software do you want to install", "install_");
        System.out.println("Installing " + requestedSoftware + "...\n");

        // run installer for that specific software
        String jarFile = null;
        for (Method method : Minecraft.class.getDeclaredMethods())
            if (method.getName().equals("install_" + requestedSoftware)) try {
                jarFile = String.valueOf(method.invoke(null, destination));
                System.out.println();
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }

        if (jarFile == null) {
            System.out.println("Installation failed.");
            return;
        }

        // ask how much memory should be max for this server
        String memory = UserInputUtil.questionString("How much memory should this server use (ex. 1G, 512M)").toUpperCase();

        // write user's input to eula acceptance to file
        boolean userAgreesToEula = UserInputUtil.questionYesNo("Do you agree to follow the Minecraft EULA");
        try { FileUtils.writeStringToFile(new File(destination, "eula.txt"), "eula=" + userAgreesToEula, Charset.defaultCharset()); } catch (IOException e) { e.printStackTrace(); }

        File serverConfig = new File(destination, "gsm.json");
        ConfigUtil.writeDefaultConfigToFile(Minecraft.class, serverConfig);
        ConfigUtil.changeConfigOptionInFile(serverConfig, "commandline", ((String) ConfigUtil.getConfigOptionFromFile(serverConfig, "commandline"))
                .replace("{MEMORY}", memory)
                .replace("{JARFILE}", jarFile)
        );
        ConfigUtil.changeConfigOptionInFile(serverConfig, "type", requestedSoftware);
        System.out.println("Finished installing server.");
    }
    public static String install_CraftBukkit(File destination) {
        String jarFile = "craftbukkit.jar";
        String downloadUrl = "http://scarsz.tech:8080/job/CraftBukkit-Spigot/lastSuccessfulBuild/artifact/" + jarFile;
        DownloadUtil.download(downloadUrl, new File(destination, jarFile));

        return jarFile;
    }
    public static String install_Spigot(File destination) {
        String jarFile = "spigot.jar";
        String downloadUrl = "http://scarsz.tech:8080/job/CraftBukkit-Spigot/lastSuccessfulBuild/artifact/" + jarFile;
        DownloadUtil.download(downloadUrl, new File(destination, jarFile));

        return jarFile;
    }
    public static String install_Thermos(File destination) {
        File librariesDestination = new File(destination, "libraries.zip");
        File thermosJarDestination = new File(destination, "Thermos.jar");

        DownloadUtil.deleteFile(new File(destination, "libraries"), true);

        List<String> availableAssets = new LinkedList<>();
        List<LinkedTreeMap<String, Object>> assetsFromApi = (List<LinkedTreeMap<String, Object>>) ((LinkedTreeMap<String, Object>) JavaGSM.gson.fromJson(DownloadUtil.getUrlAsString("https://api.github.com/repos/CyberdyneCC/Thermos/releases/latest"), LinkedTreeMap.class)).get("assets");
        for (LinkedTreeMap<String, Object> asset : assetsFromApi)
            if (asset.get("name").equals("libraries.zip")) {
                DownloadUtil.download((String) asset.get("browser_download_url"), librariesDestination);
                DownloadUtil.unzip(librariesDestination);
                DownloadUtil.deleteFile(librariesDestination);
            } else availableAssets.add((String) asset.get("browser_download_url"));
        Collections.sort(availableAssets);
        String downloadUrl = availableAssets.get(availableAssets.size() - 1);
        DownloadUtil.download(downloadUrl, new File(destination, "Thermos.jar"));
        return "Thermos.jar";
    }
    public static String install_Vanilla(File destination) {
        // find latest version
        System.out.print("Obtaining latest version info...");
        String latestVersion = DownloadUtil.getUrlAsString("https://launchermeta.mojang.com/mc/game/version_manifest.json").split("release\":\"")[1].split("\"")[0];
        if (latestVersion == null) {
            System.out.println("An error occurred during checking the latest version, aborting installation");
            return null;
        }
        System.out.println(" " + latestVersion);

        // download vanilla jar
        String jarFile = "minecraft_server." + latestVersion + ".jar";
        String downloadUrl = "https://s3.amazonaws.com/Minecraft.Download/versions/" + latestVersion + "/" + jarFile;
        DownloadUtil.download(downloadUrl, new File(destination, jarFile));

        return jarFile;
    }
    public static String install_VanillaSnapshot(File destination) {
        // find latest version
        System.out.print("Obtaining latest version info...");
        String latestSnapshot = DownloadUtil.getUrlAsString("https://launchermeta.mojang.com/mc/game/version_manifest.json").split("snapshot\":\"")[1].split("\"")[0];
        if (latestSnapshot == null) {
            System.out.println("An error occurred during checking the latest snapshot, aborting installation");
            return null;
        }
        System.out.println(" " + latestSnapshot);

        // download vanilla jar
        String jarFile = "minecraft_server." + latestSnapshot + ".jar";
        String downloadUrl = "https://s3.amazonaws.com/Minecraft.Download/versions/" + latestSnapshot + "/" + jarFile;
        DownloadUtil.download(downloadUrl, new File(destination, jarFile));

        return jarFile;
    }

    public static void update(File destination) {
        String requestedSoftware = (String) ConfigUtil.getConfigOptionFromFile(new File(destination, "gsm.json"), "type");
        System.out.println("Updating " + requestedSoftware + "...\n");

        // run updater for that specific software
        for (Method method : Minecraft.class.getDeclaredMethods())
            if (method.getName().equals("update_" + requestedSoftware)) try {
                method.invoke(null, destination);
                System.out.println();
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }

        System.out.println("Finished updating " + requestedSoftware + " to \"" + destination.getAbsolutePath() + "\". Start it with the -s flag.");
    }
    public static void update_CraftBukkit(File destination) {
        install_CraftBukkit(destination);
    }
    public static void update_Spigot(File destination) {
        install_Spigot(destination);
    }
    public static void update_Thermos(File destination) {
        install_Thermos(destination);
    }
    public static void update_Vanilla(File destination) {
        install_Vanilla(destination);
    }
    public static void update_VanillaSnapshot(File destination) {
        install_VanillaSnapshot(destination);
    }

    public static void start(File target) {
        LinkedTreeMap<String, Object> config = ConfigUtil.getConfigFromFile(new File(target, "gsm.json"));

        String commandLine = (String) config.get("commandline");
        String game = (String) config.get("game");

        RuntimeUtil.runProcess("cmd /c start \"" + game + " @ " + target.getAbsolutePath() + "\" " + commandLine, target, false);

        System.out.println("Finished starting \"" + target.getAbsolutePath() + "\".");
    }

}
