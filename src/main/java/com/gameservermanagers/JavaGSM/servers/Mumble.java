package com.gameservermanagers.JavaGSM.servers;

import java.io.File;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import com.gameservermanagers.JavaGSM.JavaGSM;
import com.gameservermanagers.JavaGSM.ServerInstaller;
import com.gameservermanagers.JavaGSM.util.ConfigUtil;
import com.gameservermanagers.JavaGSM.util.DownloadUtil;
import com.gameservermanagers.JavaGSM.util.RuntimeUtil;
import com.gameservermanagers.JavaGSM.util.SleepUtil;
import com.gameservermanagers.JavaGSM.util.UserInputUtil;
import com.google.gson.internal.LinkedTreeMap;

public class Mumble extends ServerInstaller {

	private static String getSoftware(String prompt, String filter) {
		// populate possible server software
		List<String> availableServerSoftware = new LinkedList<>();
		for (Method method : Mumble.class.getDeclaredMethods())
			if (method.getName().startsWith(filter))
				availableServerSoftware.add(method.getName().substring(8));
		Collections.sort(availableServerSoftware);

		return availableServerSoftware.get(UserInputUtil.questionList(prompt, availableServerSoftware));
	}

	public static void install(File destination) {
		String requestedSoftware = getSoftware("Which server software do you want to install", "install_");
		System.out.println("Installing " + requestedSoftware + "...\n");

		String mumbleFile = null;
		for (Method method : Mumble.class.getDeclaredMethods())
			if (method.getName().equals("install_" + requestedSoftware))
				try {
					mumbleFile = String.valueOf(method.invoke(null, destination));
					System.out.println();
				} catch (Exception e) {
					e.printStackTrace();
				}

		if (mumbleFile == null) {
			System.out.println("Installation failed.");
			return;
		}

		File mumbleConfig = new File(destination, "gsm.json");
		ConfigUtil.writeDefaultConfigToFile(Mumble.class, mumbleConfig);
	}

	public static void install_MumbleWindows(File destination) {
		String msiFile = "mumble-1.2.19.msi";
		String downloadURL = "https://github.com/mumble-voip/mumble/releases/download/1.2.19/" + msiFile;
		DownloadUtil.download(downloadURL, new File(destination, msiFile));
		System.out.println("\n\n The Mumble installation program is about to run.\n"
				+ "Please make sure to select BOTH Mumble AND Murmur when installing \n" + "to " + destination);
		SleepUtil.go(5000);
		RuntimeUtil.runProcess("msiexec /i " + msiFile, destination, false);
		DownloadUtil.deleteFile(new File(msiFile));

	}

	public static void install_MumbleLinux(File destination) {
		String tarFile = "murmur-static_x86-1.2.19.tar.bz2";
		String downloadURL = "https://github.com/mumble-voip/mumble/releases/download/1.2.19/" + tarFile;
		DownloadUtil.download(downloadURL);
		DownloadUtil.unbzip(new File(tarFile), destination);
		DownloadUtil.deleteFile(new File(tarFile));
		DownloadUtil.moveAllChildrenOfFolderToParent(new File(destination, "murmur-static_x86-1.2.19"), true);
	}

	public static void start(File target) {
		LinkedTreeMap<String, Object> config = ConfigUtil.getConfigFromFile(new File(target, "gsm.json"));
		if (JavaGSM.isWindows) {
			RuntimeUtil.runProcess("cmd /C murmur", target, false);
		} else if (JavaGSM.isLinux) {
			RuntimeUtil.runProcess("cd " + target);
			RuntimeUtil.runProcess("./murmur.x86 -ini murmur.ini", target, false);
		}
	}

}
