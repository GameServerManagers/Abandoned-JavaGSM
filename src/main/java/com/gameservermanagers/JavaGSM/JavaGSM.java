package com.gameservermanagers.JavaGSM;

import com.gameservermanagers.JavaGSM.util.ResourceUtil;
import com.gameservermanagers.JavaGSM.util.SleepUtil;
import com.gameservermanagers.JavaGSM.util.UpdateManager;
import com.gameservermanagers.JavaGSM.util.UserInputUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.stream.JsonWriter;
import org.apache.commons.io.FileUtils;
import org.reflections.Reflections;
import org.reflections.scanners.ResourcesScanner;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;

import javax.annotation.Nullable;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.util.*;
import java.util.concurrent.TimeUnit;

@SuppressWarnings({"WeakerAccess", "unchecked"})
public class JavaGSM {

    public static final String version = "0.1.0";
    public static final Map<String, String> argumentDefinitions = new LinkedHashMap<String, String>() {{
        put("-c  (-configure)", "Configure GSM or an existing server");
        put("-i  (-install)", "Install a new server");
        put("-s  (-start)", "Start a non-running existing server");
        put("-st (-stop)", "Stop a running existing server");
        put("-u  (-update)", "Update an existing server");
    }};

    public static final boolean isWindows = System.getProperty("os.name").toLowerCase().startsWith("win");
    public static final boolean isMac = System.getProperty("os.name").toLowerCase().startsWith("mac");
    public static final boolean isLinux = !isWindows && !isMac;

    public static final Map<String, Object> config = new HashMap<>();
    public static final File configFile = new File("gsm.json");
    public static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public static void main(String[] args) {
        // TODO: better header
        System.out.println("JavaGSM v" + version + " dev @ScarszRawr & collective GameServerManagers team");
        System.out.println("https://github.com/GameServerManagers/JavaGSM");
        System.out.println();

        System.out.println("Loading config...");
        loadConfig();
        
        long diff = System.currentTimeMillis() - (long) config.get("lastuc");
        if (TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS)>1){
            UpdateManager.checkForUpdates();
            System.out.println();
        }
        config.put("lastuc", System.currentTimeMillis());
        saveConfig();

        if (args.length == 0) {
            System.out.println("syntax: -flag [optional value] -flag [optional value] -flag [optional value] etc");
            System.out.println("hint: to install a new server use -i");
            System.out.println();

            // TODO: also appropriate the space before the (-argument)'s
            int maxSpace = 0;
            for (Map.Entry<String, String> definition : argumentDefinitions.entrySet())
                if (maxSpace < definition.getKey().length()) maxSpace = definition.getKey().length();

            for (Map.Entry<String, String> entry : argumentDefinitions.entrySet()) {
                System.out.print(entry.getKey());
                for (int i = 0; i < maxSpace - entry.getKey().length(); i++) System.out.print(" ");
                System.out.println(" | " + entry.getValue());
            }

            System.exit(0);
        }

        for (int i = 0; i < args.length; i++) {
            if (!args[i].startsWith("-")) continue;

            String argument = null;
            if (args.length > i + 1 && !args[i + 1].startsWith("-")) argument = args[i + 1];

            switch (args[i]) {
                case "-i":
                case "-install":
                    install(argument);
                    break;
                case "-s":
                case "-start":
                    start(argument);
                    break;
                case "-c":
                case "-configure":
                    configure(argument);
                    break;
                case "-st":
                case "-stop":
                    stop(argument);
                    break;
                case "-u":
                case "-update":
                    update(argument);
                    break;
                default:
                    System.out.println("Unknown flag \"" + args[i] + (argument == null ? "" : " " + argument) + "\"");
                    break;
            }
        }
    }

    /**
     * Install the specified server. If a null argument is given, the user will be prompted for the game to install.
     * @param argument The server class name to install
     */
    private static void install(@Nullable String argument) {
        String gameServerName;
        if (argument != null && argument.length() > 0) gameServerName = argument; else gameServerName = install_GetGame();

        System.out.println("Installing " + gameServerName + " server...");
        System.out.println();

        try {
            boolean serversFolderAvailable = new File("servers").exists() || new File("servers").mkdir();
            if (!serversFolderAvailable) {
                System.out.print("An error occurred creating the servers directory, aborting installation");
                SleepUtil.printlnEllipsis();
                return;
            }

            Method installer = Class.forName("com.gameservermanagers.JavaGSM.servers." + gameServerName).getDeclaredMethod("install", File.class);
            File destination = new File("servers/" + UserInputUtil.questionString("What should the server's main directory be in ./servers/"));

            if (destination.exists()) {
                System.out.print("An error occurred creating the destination folder " + destination.getAbsolutePath() + ": directory already exists. Aborting installation");
                SleepUtil.printlnEllipsis();
                return;
            }
            if (!destination.mkdir()) {
                System.out.print("An error occurred creating the destination folder " + destination.getAbsolutePath() + ": could not create directory. Aborting installation");
                SleepUtil.printlnEllipsis();
                return;
            }

            System.out.println();
            installer.invoke(null, destination);
        } catch (ClassNotFoundException e) {
            System.out.println("Invalid server \"" + gameServerName + "\"");
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
            System.out.print("An unknown error occurred, please content the developers immediately!");
            SleepUtil.printlnEllipsis();
        }
    }

    /**
     * Get the name of a user-supplied game server class name to install
     * @return the name of the server class name
     */
    private static String install_GetGame() {
        // don't attempt to understand this
        Reflections reflections = new Reflections(new ConfigurationBuilder().setScanners(new SubTypesScanner(false), new ResourcesScanner()).setUrls(ClasspathHelper.forClassLoader(Arrays.asList(ClasspathHelper.contextClassLoader(), ClasspathHelper.staticClassLoader()).toArray(new ClassLoader[0]))).filterInputsBy(new FilterBuilder().include(FilterBuilder.prefix("com.gameservermanagers.JavaGSM.servers"))));
        Set<Class<?>> availableServerClasses = reflections.getSubTypesOf(Object.class);

        List<String> choices = new LinkedList<>();
        for (Class<?> serverClass : availableServerClasses) {
            String[] actualNameArray = serverClass.getName().split("\\.");
            String actualName = actualNameArray[actualNameArray.length - 1];
            choices.add(actualName);
        }
        Collections.sort(choices);
        return choices.get(UserInputUtil.questionList("Which server do you want to install", choices));
    }

    private static void start(@Nullable String argument) {
        // TODO:Make Start Command
    }
    
    private static void configure(@Nullable String argument) {
        // TODO:Make Configure Command
    }
    
    private static void stop(@Nullable String argument) {
        // TODO:Make Stop Command
    }
    
    private static void update(@Nullable String argument) {
        // TODO:Make Update Command
    }

    //region Utilities
    private static void loadConfig() {
        if (!configFile.exists()) ResourceUtil.copyResourceToFile("gsm-default.json", configFile);
        try {
            config.clear();
            for (Map.Entry<String, Object> configOption : ((LinkedTreeMap<String, Object>) gson.fromJson(FileUtils.readFileToString(configFile, Charset.defaultCharset()), LinkedTreeMap.class)).entrySet())
                config.put(configOption.getKey(), configOption.getValue());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private static void saveConfig() {
        if (!configFile.exists()) return;
        for (Map.Entry<String, Object> entry : config.entrySet()) {
            //Store in config file
            try {
                JsonWriter writer = new JsonWriter(new FileWriter(configFile));
                writer.beginObject();
                writer.name(entry.getKey()).value(entry.getValue().toString());
                writer.endObject();
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    //endregion

}
