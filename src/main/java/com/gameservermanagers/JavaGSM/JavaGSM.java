package com.gameservermanagers.JavaGSM;

import com.gameservermanagers.JavaGSM.util.SleepUtil;
import com.gameservermanagers.JavaGSM.util.UpdateManager;
import com.gameservermanagers.JavaGSM.util.UserInputUtil;
import com.google.gson.Gson;
import org.reflections.Reflections;
import org.reflections.scanners.ResourcesScanner;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

@SuppressWarnings("WeakerAccess")
public class JavaGSM {

    public static final String version = "0.1.0";
    public static final Map<String, String> argumentDefinitions = new LinkedHashMap<String, String>() {{
        put("-c  (-configure)", "Configure GSM or an existing server");
        put("-i  (-install)", "Install a new server");
        put("-s  (-start)", "Start a non-running existing server");
        put("-st (-stop)", "Stop a running existing server");
        put("-u  (-update)", "Update an existing server");
    }};

    public static boolean isWindows = System.getProperty("os.name").toLowerCase().startsWith("win");
    public static boolean isMac = System.getProperty("os.name").toLowerCase().startsWith("mac");
    public static boolean isLinux = !isWindows && !isMac;

    public static Gson gson = new Gson();

    public static void main(String[] args) {
        // TODO: better header
        System.out.println("JavaGSM v" + version + " dev @ScarszRawr & collective GameServerManagers team");
        System.out.println("https://github.com/GameServerManagers/JavaGSM");
        System.out.println();

        // TODO: make this periodic
        UpdateManager.checkForUpdates();
        System.out.println();

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
            switch (args[i]) {
                case "-i":
                case "-install":
                    String gameServerName;
                    if (args.length > i + 1 && !args[i + 1].startsWith("-")) gameServerName = args[i + 1];
                    else { gameServerName = install_GetGame(); }
                    System.out.println("Installing " + gameServerName + " server...");
                    System.out.println();

                    try {
                        boolean serversFolderAvailable = new File("servers").exists() || new File("servers").mkdir();
                        if (!serversFolderAvailable) {
                            System.out.print("An error occurred creating the servers directory, aborting installation");
                            SleepUtil.printlnEllipsis();
                            continue;
                        }

                        Method installer = Class.forName("com.gameservermanagers.JavaGSM.servers." + gameServerName).getDeclaredMethod("install", File.class);
                        File destination = new File("servers/" + UserInputUtil.questionString("What should the server's main directory be in ./servers/"));

                        boolean destinationFolderAvailable = !destination.exists() && destination.mkdir();
                        if (!destinationFolderAvailable) {
                            System.out.print("An error occurred creating the destination folder " + destination.getAbsolutePath() + ", aborting installation");
                            SleepUtil.printlnEllipsis();
                            continue;
                        }

                        System.out.println();
                        installer.invoke(null, destination);
                    } catch (ClassNotFoundException e) {
                        System.out.println("Invalid server \"" + gameServerName + "\"");
                    } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                        System.out.println("This should have never happened. Shit the bed. Tell the developers about this one, cause it's huge.");
                        e.printStackTrace();
                    }
                    break;
                default:
                    System.out.println("Unknown argument \"" + args[i] + "\"");
                    break;
            }
        }
    }

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

}
