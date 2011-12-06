package se.grunka.henry;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.util.Arrays;

import com.google.inject.Inject;

public class FileLocator {
	private final Configuration configuration;

	@Inject
	public FileLocator(Configuration configuration) {
		this.configuration = configuration;
	}

	public File find(Path path, String name) throws FileNotFoundException {
		File directory = new File(configuration.getSiteDirectory(), path.getPath());
		final File fullFile = new File(directory, name);
		File[] files = fullFile.getParentFile().listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return name.startsWith(fullFile.getName());
			}
		});
		if (files == null) {
			throw new FileNotFoundException("Directory not found " + directory.getAbsolutePath());
		} else if (files.length == 0) {
			throw new FileNotFoundException("File not found " + name + " in " + directory.getAbsolutePath());
		} else if (files.length > 1) {
			throw new FileNotFoundException("Ambiguous file " + name + " matches " + Arrays.toString(files) + " in " +directory.getAbsolutePath());
		} else {
			return files[0];
		}
	}
}
