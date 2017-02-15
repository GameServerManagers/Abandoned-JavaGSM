package com.gameservermanagers.JavaGSM.servers;

import com.gameservermanagers.JavaGSM.JavaGSM;
import com.gameservermanagers.JavaGSM.ServerInstaller;
import com.gameservermanagers.JavaGSM.util.ConfigUtil;
import com.gameservermanagers.JavaGSM.util.DownloadUtil;
import com.gameservermanagers.JavaGSM.util.RuntimeUtil;
import com.gameservermanagers.JavaGSM.util.UserInputUtil;
import com.google.gson.internal.LinkedTreeMap;

import java.io.File;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

@SuppressWarnings({"unused", "WeakerAccess"})
public class TeamSpeak3 extends ServerInstaller {

    private static String getSoftware(String prompt, String filter) {
        // populate possible server software
        List<String> availableServerSoftware = new LinkedList<>();
        for (Method method : TeamSpeak3.class.getDeclaredMethods())
            if (method.getName().startsWith(filter)) availableServerSoftware.add(method.getName().substring(8));
        Collections.sort(availableServerSoftware);

        return availableServerSoftware.get(UserInputUtil.questionList(prompt, availableServerSoftware));
    }

    public static void install(File destination) {
        String requestedSoftware = getSoftware("Which server software do you want to install", "install_");
        System.out.println("Installing " + requestedSoftware + "...\n");

        String ts3File = null;
        for (Method method : TeamSpeak3.class.getDeclaredMethods())
            if (method.getName().equals("install_" + requestedSoftware)) try {
                ts3File = String.valueOf(method.invoke(null, destination));
                System.out.println();
            } catch (Exception e) {
                e.printStackTrace();
            }

        if (ts3File == null) {
            System.out.println("Installation failed.");
            return;
        }

        File ts3Config = new File(destination, "gsm.json");
        ConfigUtil.writeDefaultConfigToFile(TeamSpeak3.class, ts3Config);
    }

    // Installs 32-bit version of TeamSpeak 3 Server
    public static void install_TS3Windows32(File destination) {
        String zipFile = "teamspeak3-server_win32-3.0.13.6.zip";
        String downloadURL = "http://dl.4players.de/ts/releases/3.0.13.6/" + zipFile;
        DownloadUtil.download(downloadURL);
        DownloadUtil.unzip(new File(zipFile), destination);
        DownloadUtil.deleteFile(new File(zipFile));
        DownloadUtil.moveAllChildrenOfFolderToParent(new File(destination, "teamspeak3-server_win32"), true);
        System.out.println();
    }

    // Installs 64-bit version of TeamSpeak 3 Server
    public static void install_TS3Windows64(File destination) {
        String zipFile = "teamspeak3-server_win64-3.0.13.6.zip";
        String downloadURL = "http://dl.4players.de/ts/releases/3.0.13.6/" + zipFile;
        DownloadUtil.download(downloadURL);
        DownloadUtil.unzip(new File(zipFile), destination);
        DownloadUtil.deleteFile(new File(zipFile));
        DownloadUtil.moveAllChildrenOfFolderToParent(new File(destination, "teamspeak3-server_win64"), true);
        System.out.println();
    }

    public static void install_TS3Linux32(File destination) {
        String tarFile = "teamspeak3-server_linux_x86-3.0.13.6.tar.bz2";
        String downloadURL = "http://dl.4players.de/ts/releases/3.0.13.6/" + tarFile;
        DownloadUtil.download(downloadURL);
        DownloadUtil.unbzip(new File(tarFile), destination);
        DownloadUtil.deleteFile(new File(tarFile));
        DownloadUtil.moveAllChildrenOfFolderToParent(new File(destination, "teamspeak3-server_linux_x86"), true);
    }

    public static void install_TS3Linux64(File destination) {
        String tarFile = "teamspeak3-server_linux_amd64-3.0.13.6.tar.bz2";
        String downloadURL = "http://dl.4players.de/ts/releases/3.0.13.6/" + tarFile;
        DownloadUtil.download(downloadURL);
        DownloadUtil.unbzip(new File(tarFile), destination);
        DownloadUtil.deleteFile(new File(tarFile));
        DownloadUtil.moveAllChildrenOfFolderToParent(new File(destination, "teamspeak3-server_linux_amd64"), true);
    }

    public static void start(File target) {
        LinkedTreeMap<String, Object> config = ConfigUtil.getConfigFromFile(new File(target, "gsm.json"));
        if (JavaGSM.isWindows) {
            RuntimeUtil.runProcess("cmd /C ts3server", target, false);
        } else if (JavaGSM.isLinux) {
            RuntimeUtil.runProcess("./ts3server_startscript.sh start", target, false);
        }
    }

}
