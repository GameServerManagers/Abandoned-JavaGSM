package com.gameservermanagers.JavaGSM.util;

import com.gameservermanagers.JavaGSM.JavaGSM;
import com.google.common.io.Resources;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ResourceUtil {

    public static void copyResourceToFile(String resourceName, File file) {
        try {
            Resources.copy(Resources.getResource(resourceName), new FileOutputStream(file));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getResourceAsString(String resourceName) {
        try {
            return new String(Files.readAllBytes(Paths.get(JavaGSM.class.getResource(resourceName).toURI())));
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
            return null;
        }
    }

}
