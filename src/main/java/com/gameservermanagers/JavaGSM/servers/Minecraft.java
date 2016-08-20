package com.gameservermanagers.JavaGSM.servers;

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

    private static final String defaultCommandLine = "java -jar {JARFILE} nogui";

    public static void install() {
        // populate possible server software
        List<String> availableServerSoftware = new LinkedList<>();
        for (Method method : Minecraft.class.getDeclaredMethods())
            if (method.getName().startsWith("install_")) availableServerSoftware.add(method.getName().substring(8));

        String requestedSoftware = availableServerSoftware.get(UserInput.questionList("Which server software do you want to install", availableServerSoftware) - 1);
        System.out.println("Installing " + requestedSoftware + "...\n");

        // run installer for that specific software
        for (Method method : Minecraft.class.getDeclaredMethods())
            if (method.getName().equals("install_" + requestedSoftware)) try {
                method.invoke(null, null);
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
    }

    public static void install_Vanilla() {
        // find latest version
        System.out.print("Obtaining latest version info...");
        String latestVersion = null;
        try { latestVersion = Jsoup.connect("https://launchermeta.mojang.com/mc/game/version_manifest.json").ignoreContentType(true).get().html().split("release\":\"")[1].split("\"")[0]; } catch (IOException e) { e.printStackTrace(); }
        if (latestVersion == null) {
            System.out.println("An error occurred during checking the latest version, aborting installation");
            return;
        }
        System.out.println(" " + latestVersion);

        // download vanilla jar
        String jarFile = "minecraft_server." + latestVersion + ".jar";
        String downloadUrl = "https://s3.amazonaws.com/Minecraft.Download/versions/" + latestVersion + "/" + jarFile;
        System.out.print("Downloading " + downloadUrl + "...");
        long startTime = System.currentTimeMillis();
        try { FileUtils.copyURLToFile(new URL(downloadUrl), new File(jarFile)); } catch (IOException e) { e.printStackTrace(); }
        System.out.println(" done in " + ((System.currentTimeMillis() - startTime)/1000L) + " seconds; " + new File(jarFile).length()/1024L/1024L + "MB");

        // write user's input to eula acceptance to file
        boolean userAgreesToEula = UserInput.questionYesNo("Do you agree to follow the Minecraft EULA");
        try { FileUtils.writeStringToFile(new File("eula.txt"), "eula=" + userAgreesToEula, Charset.defaultCharset()); } catch (IOException e) { e.printStackTrace(); }

        // generate non-gsm scripts
        System.out.print("Creating non-GSM scripts...");
        boolean isWindows = System.getProperty("os.name").toLowerCase().startsWith("win");
        try {
            if (isWindows) {
                FileUtils.writeStringToFile(new File("Start-NoGSM.bat"), "@echo off\n" + defaultCommandLine.replace("{JARFILE}", jarFile), Charset.defaultCharset());
            } else {
                FileUtils.writeStringToFile(new File("Start-NoGSM.sh"), "#!/bin/bash\n" + defaultCommandLine.replace("{JARFILE}", jarFile), Charset.defaultCharset());
                Runtime.getRuntime().exec("chmod +x Start-NoGSM.sh");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(" done");

        System.out.println();
        System.out.println("Vanilla Minecraft server has been installed to the current directory. Start it with " + (isWindows ? "Start-NoGSM.bat" : "./Start-NoGSM.sh"));
        System.out.println();
    }

}
