package com.gameservermanagers.JavaGSM;

import com.gameservermanagers.JavaGSM.util.UpdateManager;

public class JavaGSM {

    public static final String version = "0.1.0";

    public static void main(String[] args) {
        UpdateManager.checkForUpdates();
    }

}
