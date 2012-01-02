package se.grunka.henry.processing;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import org.apache.commons.io.IOUtils;
import se.grunka.henry.FileLocator;
import se.grunka.henry.Path;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class LayoutProcessor implements ContentProcessor {

	private final ContentProcessor processor;
	private final FileLocator locator;

	@Inject
	public LayoutProcessor(@Named("PipelineProcessor") ContentProcessor processor, FileLocator locator) {
		this.processor = processor;
		this.locator = locator;
	}

	@Override
	public boolean shouldProcess(String name) {
		return true;
	}

	@Override
	public Content process(String name, Content content) {
		Map<String, Object> context = content.getContext();
		Object layout = context.get("layout");
		if (layout == null || !(layout instanceof String) || "".equals(layout)) {
			return content;
		} else {
			try {
				File layoutFile = locator.find(Path.LAYOUTS, (String) layout);
                context = new HashMap<String, Object>(context);
                context.remove("layout");
                context.put("content", content.getText());
				content = processor.process(layoutFile.getName(), new Content(IOUtils.toString(new FileReader(layoutFile)), context));
				return content;
			} catch (IOException e) {
				throw new IllegalArgumentException("Could not read layout " + String.valueOf(layout), e);
			}
		}
	}
}
