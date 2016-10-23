package com.gameservermanagers.JavaGSM.servers;

import com.gameservermanagers.JavaGSM.JavaGSM;
import com.gameservermanagers.JavaGSM.ServerInstaller;
import com.gameservermanagers.JavaGSM.util.DownloadUtil;
import com.gameservermanagers.JavaGSM.util.RuntimeUtil;

import java.io.File;

public class AceOfSpades extends ServerInstaller {

    public static void install(File destination) {
        if (JavaGSM.isWindows) {
            // download pre-compiled binaries and stuff
            DownloadUtil.download("https://github.com/NateShoffner/PySnip/releases/download/f8808c5/pysnip-feature_server-0.75.zip");
            DownloadUtil.unzip(new File("pysnip-feature_server-0.75.zip"), destination);
            new File("pysnip-feature_server-0.75.zip").delete();
            DownloadUtil.moveAllChildrenOfFolderToParent(new File(destination, "dist"));
            System.out.println();
        } else {
            // clone repo
            DownloadUtil.clone("https://github.com/NateShoffner/PySnip", destination);
            // install python dependencies
            System.out.println("Installing Python dependancies...");
            RuntimeUtil.runProcessWaitFor("pip install cython twisted jinja2 pillow pygeoip pycrypto pyasn1");
            System.out.println();
            // compile server
            System.out.println("Building PySpades (this will show a LOT of warnings/errors)...");
            RuntimeUtil.runProcessWaitFor("sh build_all.sh", destination);
            System.out.println();
        }

        System.out.println("Finished installing Ace of Spades server. Start it with the -s flag.");
    }

}
