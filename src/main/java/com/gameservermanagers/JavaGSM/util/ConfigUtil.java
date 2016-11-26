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

    public static Map<Class<?>, HashMap<String, Object>> defaultConfigFileChanges = new HashMap<Class<?>, HashMap<String, Object>>(){{
        put(Minecraft.class, new HashMap<String, Object>(){{
            put("commandline", "java -Xmx{MEMORY} -server -jar {JARFILE} nogui");
        }});
    }};

    public static void changeConfigOptionInFile(File file, String key, Object value) {
        try {
            LinkedTreeMap<String, Object> config = JavaGSM.gson.fromJson(FileUtils.readFileToString(file, Charset.defaultCharset()), LinkedTreeMap.class);
            config.put(key, value);
            FileUtils.writeStringToFile(file, JavaGSM.gson.toJson(config), Charset.defaultCharset());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static LinkedTreeMap<String, Object> getConfigFromFile(File file) {
        try {
            return JavaGSM.gson.fromJson(FileUtils.readFileToString(file, Charset.defaultCharset()), LinkedTreeMap.class);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    public static Object getConfigOptionFromFile(File file, String key) {
        return getConfigFromFile(file).get(key);
    }
    public static void writeDefaultConfigToFile(Class<? extends ServerInstaller> server, File destination) {
        // get default config for all servers
        LinkedTreeMap<String, Object> newConfig = JavaGSM.gson.fromJson(ResourceUtil.getResourceAsString("gsm-server-default.json"), LinkedTreeMap.class);

        // set config game type
        newConfig.put("game", server.getSimpleName());

        // get any server-specific config changes from defaultConfigFileChanges
        try {
            Class serverClass = Class.forName(server.getCanonicalName());
            if (defaultConfigFileChanges.containsKey(serverClass))
                for (Map.Entry<String, Object> entry : defaultConfigFileChanges.get(serverClass).entrySet())
                    newConfig.put(entry.getKey(), entry.getValue());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        // save new config to file
        try {
            FileUtils.writeStringToFile(destination, JavaGSM.gson.toJson(newConfig), Charset.defaultCharset());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
