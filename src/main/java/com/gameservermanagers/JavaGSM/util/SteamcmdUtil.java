package com.gameservermanagers.JavaGSM.util;

import com.gameservermanagers.JavaGSM.JavaGSM;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@SuppressWarnings({"WeakerAccess", "unused"})
public class SteamcmdUtil {

    public static Map<String, String> errorResolutions = new HashMap<String, String>(){{
        put("Invalid platform", "This server does not support this OS; nothing we can do about it.");
    }};

    private static String steamcmdArchive = "steamcmd" + (JavaGSM.isWindows ? ".zip" : JavaGSM.isMac ? "_osx.tar.gz" : "_linux.tar.gz");
    private static String steamcmdExtension = JavaGSM.isWindows ? ".exe" : ".sh";
    private static String steamcmdExecutable = "steamcmd" + steamcmdExtension;
    private static String steamcmdCommand = "+login {LOGIN} +force_install_dir {DESTINATION} +app_update {APP} validate +exit";
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

        return validSteamcmd || (attemptInstall && installSteamcmd());
    }

    public static boolean installSteamcmd() {
        System.out.println("Installing SteamCMD...");

        DownloadUtil.download(steamcmdUrl);
        DownloadUtil.unzip(new File(steamcmdArchive), new File("steamcmd"));
        new File(steamcmdArchive).delete();

        return check(false);
    }

    public static boolean installApp(String login, File destination, String app) {
        try {
            SteamcmdUtil.check();
            System.out.println("\nInstalling app " + app + " to " + destination + "...");

            Process steamcmdProcess = RuntimeUtil.runProcess("steamcmd/" + steamcmdExecutable + " " + steamcmdCommand
                    .replace("{LOGIN}", login)
                    .replace("{DESTINATION}", destination.getAbsolutePath())
                    .replace("{APP}", app))
            ;
            if (steamcmdProcess == null) {
                System.out.println("An error occurred during starting SteamCMD, aborting");
                return false;
            }

            StreamGobbler errorGobbler = new StreamGobbler(steamcmdProcess.getErrorStream());
            StreamGobbler outputGobbler = new StreamGobbler(steamcmdProcess.getInputStream());
            errorGobbler.start();
            outputGobbler.start();

            steamcmdProcess.waitFor();
            outputGobbler.join();
            errorGobbler.join();

            System.out.println("Steam finished with exit code " + steamcmdProcess.exitValue() + "\n");
            
            System.out.print("Scanning output for errors...");
            List<String> errors = scanForErrors(outputGobbler.output);
            if (errors.size() != 0) {
                System.out.println(" " + errors.size() + " errors found:");
                for (String error : errors) {
                    System.out.println("- " + error);
                    System.out.println("  Resolution: " + getResolutionForError(error));
                }
                System.out.println();
                return false;
            } else {
                System.out.println(" none found");
                return true;
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static List<String> scanForErrors(List<String> output) {
        List<String> errors = new LinkedList<>();
        for (String s : output) if (s.startsWith("ERROR!")) errors.add(s);
        return errors;
    }

    public static String getResolutionForError(String error) {
        for (Map.Entry<String, String> entry : errorResolutions.entrySet())
            if (error.contains(entry.getKey())) return entry.getValue();
        return "Unknown. :(";
    }

}
