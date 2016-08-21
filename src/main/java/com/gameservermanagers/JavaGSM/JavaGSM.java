package com.gameservermanagers.JavaGSM;

import com.gameservermanagers.JavaGSM.util.UpdateManager;
import com.gameservermanagers.JavaGSM.util.UserInput;
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
    public static final Map<String, String> argumentDefinitions = new HashMap<String, String>() {{
        put("-c (-configure)", "Configure GSM or an existing server");
        put("-i (-install)", "Install a new server");
        put("-s (-start)", "Start a non-running existing server");
        put("-st (-stop)", "Stop a running existing server");
        put("-u (-update)", "Update an existing server");
    }};

    public static Gson gson = new Gson();

    public static void main(String[] args) {
        // TODO: better header
        System.out.println("JavaGSM v" + version + " @ScarszRawr\n");

        // TODO: make this periodic
        UpdateManager.checkForUpdates();
        System.out.println();

        if (args.length == 0) {
            System.out.println("syntax: -flag [optional value] -flag [optional value] -flag [optional value] etc");
            System.out.println("hint: to install a new server use -i");
            System.out.println();

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
                        // TODO: check if server destination is already taken
                        new File("servers").mkdir();
                        Method installer = Class.forName("com.gameservermanagers.JavaGSM.servers." + gameServerName).getDeclaredMethod("install", File.class);
                        File destination = new File("servers/" + UserInput.questionString("What should the server's main directory be in ./servers/", false));
                        destination.mkdir();
                        System.out.println();
                        installer.invoke(null, destination);
                    } catch (ClassNotFoundException e) {
                        System.out.println("Invalid server \"" + gameServerName + "\"");
                    } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                        System.out.println("This should have never happened. Fuck.");
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
        Reflections reflections = new Reflections(new ConfigurationBuilder().setScanners(new SubTypesScanner(false), new ResourcesScanner()).setUrls(ClasspathHelper.forClassLoader(Arrays.asList(ClasspathHelper.contextClassLoader(), ClasspathHelper.staticClassLoader()).toArray(new ClassLoader[0]))).filterInputsBy(new FilterBuilder().include(FilterBuilder.prefix("com.gameservermanagers.JavaGSM.servers"))));
        Set<Class<?>> availableServerClasses = reflections.getSubTypesOf(Object.class);
        List<String> choices = new LinkedList<>();
        availableServerClasses.forEach(serverClass -> {
            String[] actualNameArray = serverClass.getName().split("\\.");
            String actualName = actualNameArray[actualNameArray.length - 1];
            choices.add(actualName);
        });
        return choices.get(UserInput.questionList("Which server do you want to install", choices));
    }

}
