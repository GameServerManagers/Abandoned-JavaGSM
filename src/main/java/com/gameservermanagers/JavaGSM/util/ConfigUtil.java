package com.gameservermanagers.JavaGSM.util;

import com.gameservermanagers.JavaGSM.JavaGSM;
import com.google.gson.internal.LinkedTreeMap;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings({"WeakerAccess", "unused", "unchecked"})
public class ConfigUtil {

    public static Map<String, String> defaultCommandLines = new HashMap<String, String>(){{
        put("minecraft", "java -Xmx{MEMORY} -jar {JARFILE} nogui");
    }};

    public static void changeConfigOptionInFile(File file, String key, Object value) {
        try {
            LinkedTreeMap<String, Object> config = JavaGSM.gson.fromJson(FileUtils.readFileToString(file, Charset.defaultCharset()), LinkedTreeMap.class);
            FileUtils.writeStringToFile(file, JavaGSM.gson.toJson(config), Charset.defaultCharset());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void writeConfigToFile() {

    }

    public static File minecraft(String memory, String jarFile) {
        return null;
    }

}
