package com.gameservermanagers.JavaGSM.util;

import com.gameservermanagers.JavaGSM.JavaGSM;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.jsoup.Jsoup;
import org.rauschig.jarchivelib.ArchiveFormat;
import org.rauschig.jarchivelib.ArchiverFactory;
import org.rauschig.jarchivelib.CompressionType;

import java.io.File;
import java.io.IOException;
import java.net.URL;

@SuppressWarnings({"WeakerAccess", "unused"})
public class DownloadUtil {

    public static void clone(String uri) {
        clone(uri, new File("."));
    }
    public static void clone(String uri, File destination) {
        System.out.print("Cloning " + uri + " into " + destination + "...");
        long startTime = System.currentTimeMillis();
        try { Git.cloneRepository().setURI(uri).setDirectory(destination).call(); } catch (GitAPIException e) { e.printStackTrace(); }
        System.out.println(" done in " + ((System.currentTimeMillis() - startTime)/1000L) + " seconds; " + FileUtils.sizeOfDirectory(destination)/1024L/1024L + "MB");
    }

    public static void deleteFile(File fileToDelete) {
        deleteFile(fileToDelete, false);
    }
    public static void deleteFile(File fileToDelete, boolean quiet) {
        if (!quiet) System.out.print("Deleting file \"" + fileToDelete.getAbsolutePath() + "\"... ");
        if (!fileToDelete.exists()) {
            if (!quiet) System.out.println("failed. File doesn't exist.");
        } else if (!fileToDelete.delete()) {
            if (!quiet) System.out.println("failed. Permissions!?");
        } else {
            if (!quiet) System.out.println(fileToDelete.exists() ? "failed." : "success");
        }
    }

    public static void download(String url) {
        String[] split = url.split("/");
        download(url, new File(split[split.length - 1]));
    }
    public static void download(String url, File destination) {
        System.out.print("Downloading " + url + "...");
        long startTime = System.currentTimeMillis();
        try { FileUtils.copyURLToFile(new URL(url), destination); } catch (IOException e) { e.printStackTrace(); }
        System.out.println(" done in " + ((System.currentTimeMillis() - startTime)/1000L) + " seconds; " + destination.length()/1024L/1024L + "MB");
    }

    public static String getUrlAsString(String url) {
        return getUrlAsString(url, "JavaGSM v" + JavaGSM.version);
    }
    public static String getUrlAsString(String url, String userAgent) {
        try {
            String response = Jsoup
                    .connect(url)
                    .userAgent(userAgent)
                    .ignoreContentType(true)
                    .get()
                    .html()
            ;
            if (response.startsWith("<html>\n <head></head>\n <body>\n  ")) response = response.substring(32);
            if (response.endsWith("\n </body>\n</html>")) response = response.substring(0, response.length() - 17);
            return response;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void moveAllChildrenOfFolderToParent(File target) {
        moveAllChildrenOfFolderToParent(target, false);
    }
    public static void moveAllChildrenOfFolderToParent(File target, boolean deleteTarget) {
        for (File file : target.listFiles())
            file.renameTo(new File(file.getParentFile().getParentFile(), file.getName()));
        if (deleteTarget) target.delete();
    }

    public static void unzip(File source) {
        unzip(source, source.getParentFile());
    }
    public static void unzip(File source, File destination) {
        if (source.getAbsolutePath().endsWith(".gz")) {
            ungzip(source, destination);
            return;
        }

        long startTime = System.currentTimeMillis();
        System.out.print("Unzipping " + source.getName() + " to " + destination.getPath() + "...");

        try {
            new ZipFile(source).extractAll(destination.getAbsolutePath());
        } catch (ZipException e) {
            e.printStackTrace();
        }

        System.out.println(" done in " + (System.currentTimeMillis() - startTime) + "ms");
    }

    public static void ungzip(File source) {
        ungzip(source, source.getParentFile());
    }
    public static void ungzip(File source, File destination) {
        long startTime = System.currentTimeMillis();
        System.out.print("Ungzipping " + source.getName() + " to " + destination.getPath() + "...");

        try {
            ArchiverFactory.createArchiver("tar", "gz").extract(source, destination);
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println(" done in " + (System.currentTimeMillis() - startTime) + "ms");
    }

    public static void unbzip(File source) {
    	unbzip(source, source.getParentFile());
    }
    
    public static void unbzip(File source, File destination) {
         try {
             ArchiverFactory.createArchiver(ArchiveFormat.TAR, CompressionType.BZIP2).extract(source, destination);
         } catch (IOException e) {
             e.printStackTrace();
         }
    }
}
