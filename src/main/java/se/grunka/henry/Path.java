package se.grunka.henry;

public enum Path {
	INCLUDES("_includes"),
	LAYOUTS("_layouts");

	private final String path;

	Path(String path) {
		this.path = path;
	}

	public String getPath() {
		return path;
	}
}
