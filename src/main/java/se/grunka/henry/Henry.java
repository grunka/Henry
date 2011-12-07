package se.grunka.henry;

import com.google.inject.Binder;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.name.Names;
import org.apache.commons.io.IOUtils;
import se.grunka.henry.log.LogFormatter;
import se.grunka.henry.processing.Content;
import se.grunka.henry.processing.ContentProcessor;
import se.grunka.henry.processing.PipelineProcessor;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Logger;

public class Henry {
	public static void main(String[] args) throws Exception {
		Injector injector = Guice.createInjector(new Module() {
			@Override
			public void configure(Binder binder) {
				//TODO parse files and args
				binder.bind(Configuration.class).toInstance(new Configuration());
				binder.bind(ContentProcessor.class).annotatedWith(Names.named("PipelineProcessor")).to(PipelineProcessor.class);
			}
		});
		replaceLoggingFormatter(injector.getInstance(LogFormatter.class));
		injector.getInstance(Henry.class).run();
	}

	private static void replaceLoggingFormatter(Formatter formatter) {
		for (Handler handler : Logger.getLogger("").getHandlers()) {
			handler.setFormatter(formatter);
		}
	}

	private final Logger logger;
	private final PipelineProcessor processor;
    private final Configuration configuration;

    @Inject
	public Henry(Logger logger, PipelineProcessor processor, Configuration configuration) {
		this.logger = logger;
		this.processor = processor;
        this.configuration = configuration;
    }

	private void run() throws IOException {
		File directory = new File("src/test/resources/site");
		processDirectory(directory);
		logger.info("done");
	}

	private void processDirectory(File directory) throws IOException {
		for (File file : directory.listFiles()) {
			String name = file.getName();
			if (isSpecial(name)) {
				continue;
			}
			if (file.isDirectory()) {
				processDirectory(directory);
			} else {
				String text = IOUtils.toString(new FileReader(file));
				//TODO create default context with site object and such
				//TODO read _posts and create files for entries in there
				//TODO check the features of jekyll and see if there is anything else
				//TODO see if plugins could be handled in some way, maybe javascript using rhino
                //TODO output completed files
				Content content = processor.process(name, new Content(text, new HashMap<String, Object>()));
				logger.info(content.getText());
			}
		}
	}

	private boolean isSpecial(String name) {
		return name.startsWith("_");
	}

}
