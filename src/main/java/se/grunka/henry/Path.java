package se.grunka.henry;

public enum Path {
	INCLUDES("_includes"),
	LAYOUTS("_layouts"),
    PLUGINS("_plugins");

	private final String path;

	Path(String path) {
		this.path = path;
	}

	public String toString() {
		return path;
	}
}
