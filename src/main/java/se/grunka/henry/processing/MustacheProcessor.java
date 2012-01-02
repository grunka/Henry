package se.grunka.henry.processing;

import com.google.inject.Inject;
import com.samskivert.mustache.Mustache;
import org.apache.commons.io.IOUtils;
import se.grunka.henry.FileLocator;
import se.grunka.henry.Path;

import javax.inject.Named;
import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

public class MustacheProcessor implements ContentProcessor {
	private final Mustache.Compiler compiler;
    private final ProcessingTemplateLoader loader;

    @Inject
	public MustacheProcessor(final FileLocator locator, @Named("PipelineProcessor") ContentProcessor processor) {
        loader = new ProcessingTemplateLoader(processor, locator);
        compiler = Mustache.compiler().nullValue("").withLoader(loader);
	}

	@Override
	public boolean shouldProcess(String name) {
		return true;
	}

	@Override
	public Content process(String name, Content content) {
        loader.setCurrentContext(content.getContext());
		String text = compiler.compile(content.getText()).execute(content.getContext());
		return new Content(text, content.getContext());
	}

	private static class ProcessingTemplateLoader implements Mustache.TemplateLoader {
		private final ContentProcessor processor;
		private final FileLocator locator;
        private Map<String, Object> currentContext;

		public ProcessingTemplateLoader(ContentProcessor processor, FileLocator locator) {
			this.processor = processor;
			this.locator = locator;
		}

		@Override
		public Reader getTemplate(String name) throws Exception {
			File file = locator.find(Path.INCLUDES, name);
			String template = IOUtils.toString(new FileReader(file));
            //TODO fix the potential thread safety issue with the use of the context this way... if I this project is ever multi-threaded
            currentContext = new HashMap<String, Object>(currentContext);
            currentContext.remove("layout");
            Content content = processor.process(file.getName(), new Content(template, currentContext));
			return new StringReader(content.getText());
		}

        public void setCurrentContext(Map<String, Object> currentContext) {
            this.currentContext = currentContext;
        }
    }
}
