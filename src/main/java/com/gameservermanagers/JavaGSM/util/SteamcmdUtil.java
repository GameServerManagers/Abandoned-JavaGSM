package com.gameservermanagers.JavaGSM.util;

import java.io.File;
import java.io.IOException;

@SuppressWarnings({"WeakerAccess", "unused"})
public class SteamcmdUtil {

    private static boolean isWindows = System.getProperty("os.name").toLowerCase().startsWith("win");
    private static boolean isMac = System.getProperty("os.name").toLowerCase().startsWith("mac");
    private static boolean isLinux = !isWindows && !isMac;

    private static String steamcmdArchive = "steamcmd" + (isWindows ? ".zip" : isMac ? "_osx.tar.gz" : "_linux.tar.gz");
    private static String steamcmdExtension = isWindows ? ".exe" : ".sh";
    private static String steamcmdExecutable = "steamcmd" + steamcmdExtension;
    private static String steamcmdCommand = "+login anonymous +force_install_dir {DESTINATION} +app_update {APP} validate +exit";
    private static String steamcmdUrl = "https://steamcdn-a.akamaihd.net/client/installer/" + steamcmdArchive;

    public static boolean check() {
        return check(true);
    }
    public static boolean check(boolean attemptInstall) {
        System.out.print("Checking if SteamCMD is installed...");

        boolean validSteamcmd = true;
        if (!new File("steamcmd").exists()) validSteamcmd = false;
        if (!new File("steamcmd/steamcmd" + steamcmdExtension).exists()) validSteamcmd = false;

        System.out.println(" " + (validSteamcmd ? "installed" : "not installed"));

        return validSteamcmd || attemptInstall && installSteamcmd();
    }

    public static boolean installSteamcmd() {
        System.out.println("Installing SteamCMD...");

        DownloadUtil.download(steamcmdUrl);
        DownloadUtil.unzip(new File(steamcmdArchive), new File("steamcmd"));
        new File(steamcmdArchive).delete();

        return check(false);
    }

    public static void installApp(File destination, String app) {
        SteamcmdUtil.check();
        try {
            Process steamcmdProcess = Runtime.getRuntime().exec("steamcmd/" + steamcmdExecutable + " " + steamcmdCommand.replace("{DESTINATION}", destination.getAbsolutePath()).replace("{APP}", app));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
