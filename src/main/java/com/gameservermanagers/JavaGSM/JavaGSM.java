package com.gameservermanagers.JavaGSM;

import com.gameservermanagers.JavaGSM.servers.Minecraft;
import com.gameservermanagers.JavaGSM.util.UpdateManager;

public class JavaGSM {

    public static final String version = "0.1.0";

    public static void main(String[] args) {
        UpdateManager.checkForUpdates();
        Minecraft.install();

        // TODO: make actual menu shit here instead of just going to installing Minecraft
    }

}
