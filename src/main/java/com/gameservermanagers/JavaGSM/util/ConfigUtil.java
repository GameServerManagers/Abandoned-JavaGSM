package com.gameservermanagers.JavaGSM.util;

import com.gameservermanagers.JavaGSM.JavaGSM;
import com.gameservermanagers.JavaGSM.servers.Minecraft;
import com.google.gson.internal.LinkedTreeMap;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@SuppressWarnings({"WeakerAccess", "unused", "unchecked"})
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
    public static LinkedTreeMap<String, Object> getDefaultConfig(Class<?> serverInstallerClass) {
        LinkedTreeMap<String, Object> config = JavaGSM.gson.fromJson(ResourceUtil.getResourceAsString("gsm-server-default.json"), LinkedTreeMap.class);
        for (Map.Entry<String, Object> entry : ((Set<Map.Entry<String, Object>>) JavaGSM.gson.fromJson(ResourceUtil.getResourceAsString(serverInstallerClass.getClass().getName()), HashMap.class).entrySet())) {
            config.put(entry.getKey(), entry.getValue());
        }
        return config;
    }
    public static String getDefaultConfigAsJson(Class<?> serverInstallerClass) {
        return JavaGSM.gson.toJson(getDefaultConfig(serverInstallerClass));
    }
    public static void writeConfigToFile(HashMap<String, Object> config, File output) {
        try {
            FileUtils.writeStringToFile(output, JavaGSM.gson.toJson(config), Charset.defaultCharset());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
