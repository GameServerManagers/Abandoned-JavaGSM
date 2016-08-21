package com.gameservermanagers.JavaGSM.servers;

import com.gameservermanagers.JavaGSM.util.SteamcmdUtil;

import java.io.File;

@SuppressWarnings("unused")
public class GarrysMod {

    public static void install(File destination) {
        SteamcmdUtil.installApp(destination, "4020");
    }

}
