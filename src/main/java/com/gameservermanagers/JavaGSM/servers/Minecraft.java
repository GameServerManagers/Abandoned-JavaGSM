package com.gameservermanagers.JavaGSM.servers;

import com.gameservermanagers.JavaGSM.JavaGSM;
import com.gameservermanagers.JavaGSM.util.ServerConfig;
import com.gameservermanagers.JavaGSM.util.UserInput;
import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.LinkedList;
import java.util.List;

@SuppressWarnings("unused")
public class Minecraft {

    // TODO: add more stuff to default command line
    // TODO: prompt user for memory specs during install
    // TODO: send installation files to a dedicated server folder instead of current dir

    public static void install(File destination) {
        // populate possible server software
        List<String> availableServerSoftware = new LinkedList<>();
        for (Method method : Minecraft.class.getDeclaredMethods())
            if (method.getName().startsWith("install_")) availableServerSoftware.add(method.getName().substring(8));

        String requestedSoftware = availableServerSoftware.get(UserInput.questionList("Which server software do you want to install", availableServerSoftware));
        System.out.println("Installing " + requestedSoftware + "...\n");

        // run installer for that specific software
        String jarFile = null;
        for (Method method : Minecraft.class.getDeclaredMethods())
            if (method.getName().equals("install_" + requestedSoftware)) try {
                jarFile = String.valueOf(method.invoke(null, destination));
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }

        if (jarFile == null) {
            System.out.println("Installation failed.");
            return;
        }

        // ask how much memory should be max for this server
        String memory = UserInput.questionString("How much memory should be this server use (ex. 1G, 512M)", false).toUpperCase();

        // write user's input to eula acceptance to file
        boolean userAgreesToEula = UserInput.questionYesNo("Do you agree to follow the Minecraft EULA");
        try { FileUtils.writeStringToFile(new File(destination, "eula.txt"), "eula=" + userAgreesToEula, Charset.defaultCharset()); } catch (IOException e) { e.printStackTrace(); }

        // generate non-gsm scripts
        System.out.print("Creating non-GSM scripts...");
        boolean isWindows = System.getProperty("os.name").toLowerCase().startsWith("win");
        try {
            if (isWindows) {
                FileUtils.writeStringToFile(new File(destination, "Start-NoGSM.bat"), "@echo off\n" + ServerConfig.defaultCommandLines.get("minecraft").replace("{JARFILE}", jarFile), Charset.defaultCharset());
            } else {
                FileUtils.writeStringToFile(new File(destination, "Start-NoGSM.sh"), "#!/bin/bash\n" + ServerConfig.defaultCommandLines.get("minecraft").replace("{JARFILE}", jarFile), Charset.defaultCharset());
                Runtime.getRuntime().exec("chmod +x \"" + destination.getAbsolutePath() + "/Start-NoGSM.sh\""); // TODO: fix this
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(" done");
        System.out.println();

        try { FileUtils.writeStringToFile(new File(destination, "gsm.json"), JavaGSM.gson.toJson(ServerConfig.minecraft(memory, jarFile)), Charset.defaultCharset()); } catch (IOException e) { e.printStackTrace(); }
        System.out.println("Finished installing server. Start it with " + new File(destination, "/Start-NoGSM." + (isWindows ? "bat" : "sh")));
    }

    public static String install_Vanilla(File destination) {
        // find latest version
        System.out.print("Obtaining latest version info...");
        String latestVersion = null;
        try { latestVersion = Jsoup.connect("https://launchermeta.mojang.com/mc/game/version_manifest.json").ignoreContentType(true).get().html().split("release\":\"")[1].split("\"")[0]; } catch (IOException e) { e.printStackTrace(); }
        if (latestVersion == null) {
            System.out.println("An error occurred during checking the latest version, aborting installation");
            return null;
        }
        System.out.println(" " + latestVersion);

        // download vanilla jar
        String jarFile = "minecraft_server." + latestVersion + ".jar";
        String downloadUrl = "https://s3.amazonaws.com/Minecraft.Download/versions/" + latestVersion + "/" + jarFile;
        System.out.print("Downloading " + downloadUrl + "...");
        long startTime = System.currentTimeMillis();
        try { FileUtils.copyURLToFile(new URL(downloadUrl), new File(destination, jarFile)); } catch (IOException e) { e.printStackTrace(); }
        System.out.println(" done in " + ((System.currentTimeMillis() - startTime)/1000L) + " seconds; " + new File(destination, jarFile).length()/1024L/1024L + "MB");

        System.out.println();
        return jarFile;
    }

}
