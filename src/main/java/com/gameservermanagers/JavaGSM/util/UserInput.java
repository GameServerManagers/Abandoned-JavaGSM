package com.gameservermanagers.JavaGSM.util;

import org.apache.commons.lang.StringUtils;

import java.util.List;

@SuppressWarnings("unused")
public class UserInput {

    public static boolean questionYesNo(String message) {
        String response = "";
        while (!response.toLowerCase().startsWith("y") && !response.toLowerCase().startsWith("n")) {
            System.out.print(message.endsWith(" ") ? message : message + " ");
            response = System.console().readLine();
        }

        return response.toLowerCase().startsWith("y");
    }

    public static int questionInt(String message) {
        while (true) {
            System.out.print(message.endsWith(" ") ? message : message + " ");
            String response = System.console().readLine();

            try {
                return Integer.parseInt(response);
            } catch (Exception ignored) {}
        }
    }

    public static String questionString(String message) {
        System.out.print(message.endsWith(" ") ? message : message + " ");
        return System.console().readLine();
    }

    public static int questionList(String message, List<String> options) {
        String response = "";
        while ("".equals(response) || !StringUtils.isNumeric(response) || Integer.parseInt(response) < 1 || Integer.parseInt(response) > options.size()) {
            for (String option : options) {
                System.out.println((options.indexOf(option) + 1) + ": " + option);
            }
            System.out.print(message.endsWith(" ") ? message : message + " ");
            response = System.console().readLine();
        }

        return Integer.parseInt(response);
    }

}
