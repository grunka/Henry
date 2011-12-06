package se.grunka.henry.processing;

import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.HashMap;

import javax.inject.Named;

import com.google.inject.Inject;
import com.samskivert.mustache.Mustache;
import org.apache.commons.io.IOUtils;
import se.grunka.henry.FileLocator;
import se.grunka.henry.Path;

public class MustacheProcessor implements ContentProcessor {
	private final Mustache.Compiler compiler;

	@Inject
	public MustacheProcessor(final FileLocator locator, @Named("PipelineProcessor") ContentProcessor processor) {
		compiler = Mustache.compiler().nullValue("").withLoader(new ProcessingTemplateLoader(processor, locator));
	}

	@Override
	public boolean shouldProcess(String name) {
		return true;
	}

	@Override
	public Content process(String name, Content content) {
		String text = compiler.compile(content.getText()).execute(content.getContext());
		return new Content(text, content.getContext());
	}

	private static class ProcessingTemplateLoader implements Mustache.TemplateLoader {
		private final ContentProcessor processor;
		private final FileLocator locator;

		public ProcessingTemplateLoader(ContentProcessor processor, FileLocator locator) {
			this.processor = processor;
			this.locator = locator;
		}

		@Override
		public Reader getTemplate(String name) throws Exception {
			File file = locator.find(Path.INCLUDES, name);
			String template = IOUtils.toString(new FileReader(file));
			//TODO get context from the template that include this one
			Content content = processor.process(file.getName(), new Content(template, new HashMap<String, Object>()));
			return new StringReader(content.getText());
		}
	}
}
