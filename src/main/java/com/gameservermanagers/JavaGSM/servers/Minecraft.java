package com.gameservermanagers.JavaGSM.servers;

import com.gameservermanagers.JavaGSM.JavaGSM;
import com.gameservermanagers.JavaGSM.util.DownloadUtil;
import com.gameservermanagers.JavaGSM.util.ServerConfig;
import com.gameservermanagers.JavaGSM.util.UserInputUtil;
import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.util.LinkedList;
import java.util.List;

@SuppressWarnings("unused")
public class Minecraft {

    // TODO: add more stuff to default command line

    private static String getLatestVersion() {
        System.out.print("Obtaining latest version info...");
        String latestVersion = null;
        try { latestVersion = Jsoup.connect("https://launchermeta.mojang.com/mc/game/version_manifest.json").ignoreContentType(true).get().html().split("release\":\"")[1].split("\"")[0]; } catch (IOException e) { e.printStackTrace(); }
        if (latestVersion == null) {
            System.out.println("An error occurred during checking the latest version, aborting installation");
            return null;
        }
        System.out.println(" " + latestVersion);
        return latestVersion;
    }

    public static void install(File destination) {
        // populate possible server software
        List<String> availableServerSoftware = new LinkedList<>();
        for (Method method : Minecraft.class.getDeclaredMethods())
            if (method.getName().startsWith("install_")) availableServerSoftware.add(method.getName().substring(8));

        String requestedSoftware = availableServerSoftware.get(UserInputUtil.questionList("Which server software do you want to install", availableServerSoftware));
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
        String memory = UserInputUtil.questionString("How much memory should be this server use (ex. 1G, 512M)").toUpperCase();

        // write user's input to eula acceptance to file
        boolean userAgreesToEula = UserInputUtil.questionYesNo("Do you agree to follow the Minecraft EULA");
        try { FileUtils.writeStringToFile(new File(destination, "eula.txt"), "eula=" + userAgreesToEula, Charset.defaultCharset()); } catch (IOException e) { e.printStackTrace(); }

        // generate non-gsm scripts
        System.out.print("Creating non-GSM scripts...");
        try {
            if (JavaGSM.isWindows) {
                FileUtils.writeStringToFile(new File(destination, "Start-NoGSM.bat"), "@echo off\ncd " + destination.getAbsolutePath() + "\n" + ServerConfig.minecraft(memory, jarFile).getCommandLine(), Charset.defaultCharset());
            } else {
                FileUtils.writeStringToFile(new File(destination, "Start-NoGSM.sh"), "#!/bin/bash\ncd " + destination.getAbsolutePath() + "\n" + ServerConfig.minecraft(memory, jarFile).getCommandLine(), Charset.defaultCharset());
                new File(destination, "Start-NoGSM.sh").setExecutable(true);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(" done");
        System.out.println();

        try { FileUtils.writeStringToFile(new File(destination, "gsm.json"), JavaGSM.gson.toJson(ServerConfig.minecraft(memory, jarFile)), Charset.defaultCharset()); } catch (IOException e) { e.printStackTrace(); }
        System.out.println("Finished installing server. Start it with " + new File(destination, "/Start-NoGSM." + (JavaGSM.isWindows ? "bat" : "sh")));
    }

    public static String install_Vanilla(File destination) {
        // find latest version
        String latestVersion = getLatestVersion();
        if (latestVersion == null) return null;

        // download vanilla jar
        String jarFile = "minecraft_server." + latestVersion + ".jar";
        String downloadUrl = "https://s3.amazonaws.com/Minecraft.Download/versions/" + latestVersion + "/" + jarFile;
        DownloadUtil.download(downloadUrl, new File(destination, jarFile));

        return jarFile;
    }

    public static String install_CraftBukkit(File destination) {
        // download spigot jar
        String jarFile = "craftbukkit.jar";
        String downloadUrl = "http://scarsz.tech:8080/job/CraftBukkit-Spigot/lastSuccessfulBuild/artifact/" + jarFile;
        DownloadUtil.download(downloadUrl, new File(destination, jarFile));

        return jarFile;
    }

    public static String install_Spigot(File destination) {
        // download spigot jar
        String jarFile = "spigot.jar";
        String downloadUrl = "http://scarsz.tech:8080/job/CraftBukkit-Spigot/lastSuccessfulBuild/artifact/" + jarFile;
        DownloadUtil.download(downloadUrl, new File(destination, jarFile));

        return jarFile;
    }

    public static String install_KCauldron(File destination) {
        // find the latest version
        System.out.print("Obtaining latest KCauldron build...");
        String[] splitVersions = new String[0];
        try { splitVersions = Jsoup.connect("https://api.prok.pw/repo/versions/pw.prok/KCauldron").ignoreContentType(true).get().html().split("version"); } catch (IOException e) { e.printStackTrace(); } // download latest versions JSON
        String latestVersion = splitVersions[splitVersions.length - 1].substring(3).split("\"")[0]; // shitty JSON parsing, come @ me, square up homie
        System.out.println(" " + latestVersion);

        // download/extract/delete the latest bundle
        String bundleFile = "KCauldron-" + latestVersion + "-bundle.zip";
        String downloadUrl = "https://repo.prok.pw/pw/prok/KCauldron/" + latestVersion + "/" + bundleFile;
        DownloadUtil.download(downloadUrl, new File(destination, bundleFile)); // download bundle
        DownloadUtil.unzip(new File(destination, bundleFile)); // unzip downloaded zip
        new File(destination, bundleFile).delete(); // delete extracted zip
        new File(destination, "README.txt").delete(); // delete pointless readme

        for (File file : destination.listFiles()) if (file.getAbsolutePath().endsWith(".jar")) return file.getName();
        return "KCauldron.jar";
    }

}
