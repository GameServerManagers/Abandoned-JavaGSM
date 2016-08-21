package com.gameservermanagers.JavaGSM.util;

import java.io.File;

@SuppressWarnings({"WeakerAccess", "unused"})
public class SteamcmdUtil {

    private static boolean isWindows = System.getProperty("os.name").toLowerCase().startsWith("win");
    private static String steamcmdArchive = "steamcmd" + (isWindows ? ".zip" : "_linux.tar.gz");
    private static String steamcmdExtension = isWindows ? ".bat" : ".sh";
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

        return validSteamcmd || attemptInstall && install();
    }

    public static boolean install() {
        System.out.println("Installing SteamCMD...");

        DownloadUtil.download(steamcmdUrl);
        DownloadUtil.unzip(new File(steamcmdArchive), new File("steamcmd"));
        new File(steamcmdArchive).delete();

        return check(false);
    }

}
