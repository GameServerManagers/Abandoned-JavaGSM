package com.gameservermanagers.JavaGSM.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;

class StreamGobbler extends Thread {

    private InputStream is;
    List<String> output = new LinkedList<>();

    StreamGobbler(InputStream is) {
        this.is = is;
    }

    public void run() {
        try {
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            String line;
            while ((line = br.readLine()) != null) {
                System.out.println(line);
                output.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}