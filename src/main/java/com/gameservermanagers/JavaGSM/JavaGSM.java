package com.gameservermanagers.JavaGSM;

import com.gameservermanagers.JavaGSM.util.UpdateManager;
import com.gameservermanagers.JavaGSM.util.UserInput;
import org.reflections.Reflections;
import org.reflections.scanners.ResourcesScanner;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class JavaGSM {

    public static final String version = "0.1.0";

    public static void main(String[] args) {
        UpdateManager.checkForUpdates();
        //Minecraft.install();

        // TODO: make actual menu shit here instead of just going to installing Minecraft

        if (args.length == 0) {
            install_GetGame();
            System.exit(0);
        }

        if (args.length > 0) {
            for (int i = 0; i < args.length; i++) {
                switch (args[i]) {
                    case "-i":
                    case "-install":
                        String gameServerName = args.length > i + 1 && !args[i].startsWith("-") ? args[i + 1] : install_GetGame();
                        try {
                            Method installer = Class.forName("com.gameservermanagers.JavaGSM.servers." + gameServerName).getDeclaredMethod("install");
                            installer.invoke(null, new File(UserInput.questionString("What should the server's main directory be in ./servers", false)));
                        } catch (ClassNotFoundException e) {
                            System.out.println("Invalid game server \"" + gameServerName + "\"");
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
            System.exit(0);
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
