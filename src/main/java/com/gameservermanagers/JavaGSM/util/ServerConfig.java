package com.gameservermanagers.JavaGSM.util;

import java.util.HashMap;
import java.util.Map;

public class ServerConfig {

    public static Map<String, String> defaultCommandLines = new HashMap<String, String>(){{
        put("minecraft", "java -Xmx{MEMORY} -jar {JARFILE} nogui");
    }};
    public static ServerConfig minecraft(String memory, String jarFile) {
        return new ServerConfig().setCommandLine(defaultCommandLines.get("minecraft")
                .replace("{MEMORY}", memory)
                .replace("{JARFILE}", jarFile)
        );
    }

    private String commandLine;

    public ServerConfig setCommandLine(String commandLine) {
        this.commandLine = commandLine;
        return this;
    }

}
