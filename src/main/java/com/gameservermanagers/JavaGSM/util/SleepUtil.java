package com.gameservermanagers.JavaGSM.util;

public class SleepUtil {

    public static void go(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void printlnEllipsis() {
        for (int i = 0; i < 3; i++) {
            System.out.print(".");
            go(1000);
        }
        System.out.println();
    }

}
