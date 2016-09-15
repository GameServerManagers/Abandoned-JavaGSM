package com.gameservermanagers.JavaGSM.servers;

import com.gameservermanagers.JavaGSM.util.SteamcmdUtil;

import java.io.File;

@SuppressWarnings("unused")
public class CounterStrikeSource {

    public static void install(File destination) {
        boolean installedSuccessfully = SteamcmdUtil.installApp("anonymous", destination, "232330");
        System.out.println(installedSuccessfully
                ? "Finished installing Counter-Strike: Source server. Start it with the -s flag."
                : "Failed installing Counter-Strike: Source server. See above for errors generated by SteamCMD.")
        ;
        System.out.println();
    }

}