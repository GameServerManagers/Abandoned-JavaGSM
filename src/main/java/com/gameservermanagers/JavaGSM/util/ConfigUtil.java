package com.gameservermanagers.JavaGSM.util;

import com.gameservermanagers.JavaGSM.JavaGSM;
import com.gameservermanagers.JavaGSM.ServerInstaller;
import com.gameservermanagers.JavaGSM.servers.Minecraft;
import com.google.gson.internal.LinkedTreeMap;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("WeakerAccess")
public class ConfigUtil {

    public static Map<Class<?>, HashMap<String, Object>> defaultCommandLines = new HashMap<Class<?>, HashMap<String, Object>>(){{
        put(Minecraft.class, new HashMap<String, Object>(){{
            put("commandline", "java -Xmx{MEMORY} -jar {JARFILE} nogui");
        }});
    }};

    public static void changeConfigOptionInFile(File file, String key, Object value) {
        try {
            LinkedTreeMap<String, Object> config = JavaGSM.gson.fromJson(FileUtils.readFileToString(file, Charset.defaultCharset()), LinkedTreeMap.class);
            FileUtils.writeStringToFile(file, JavaGSM.gson.toJson(config), Charset.defaultCharset());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static Object getConfigOptionFromFile(File file, String key) {
        try {
            LinkedTreeMap<String, Object> config = JavaGSM.gson.fromJson(FileUtils.readFileToString(file, Charset.defaultCharset()), LinkedTreeMap.class);
            return config.get(key);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    public static void writeDefaultConfigToFile(Class<? extends ServerInstaller> server, File destination) {
        // get default config for all servers
        LinkedTreeMap<String, Object> newConfig = JavaGSM.gson.fromJson(ResourceUtil.getResourceAsString("gsm-server-default.json"), LinkedTreeMap.class);

        // set config game type
        newConfig.put("game", server.getSimpleName());

        // get any server-specific config changes from defaultCommandLines
        if (defaultCommandLines.containsKey(server.getClass()))
            for (Map.Entry<String, Object> entry : defaultCommandLines.get(server.getClass()).entrySet())
                newConfig.put(entry.getKey(), entry.getValue());

        // save new config to file
        try {
            FileUtils.writeStringToFile(destination, JavaGSM.gson.toJson(newConfig), Charset.defaultCharset());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
