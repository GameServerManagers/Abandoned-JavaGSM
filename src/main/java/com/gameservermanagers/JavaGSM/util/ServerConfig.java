package com.gameservermanagers.JavaGSM.util;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings({"WeakerAccess", "unused"})
public class ServerConfig {

    //region static stuff
    public static Map<String, String> defaultCommandLines = new HashMap<String, String>(){{
        put("minecraft", "java -Xmx{MEMORY} -jar {JARFILE} nogui");
    }};
    public static ServerConfig minecraft(String memory, String jarFile) {
        return new ServerConfig().setCommandLine(defaultCommandLines.get("minecraft")
                .replace("{MEMORY}", memory)
                .replace("{JARFILE}", jarFile)
        );
    }
    //endregion

    private String commandLine;
    public String getCommandLine() {
        return commandLine;
    }
    public ServerConfig setCommandLine(String commandLine) {
        this.commandLine = commandLine;
        return this;
    }

    private boolean updateOnStart;
    public boolean getUpdateOnStart() {
        return updateOnStart;
    }
    public ServerConfig setUpdateOnStart(boolean updateOnStart) {
        this.updateOnStart = updateOnStart;
        return this;
    }
}
