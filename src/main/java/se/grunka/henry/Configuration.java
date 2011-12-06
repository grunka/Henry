package se.grunka.henry;

import java.io.File;

public class Configuration {
	//TODO load from config
	private final File siteDirectory = new File("src/test/resources/site");

	public File getSiteDirectory() {
		return siteDirectory;
	}
}
