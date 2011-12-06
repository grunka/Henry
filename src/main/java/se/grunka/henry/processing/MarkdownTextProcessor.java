package se.grunka.henry.processing;

import com.petebevin.markdown.MarkdownProcessor;

public class MarkdownTextProcessor implements ContentProcessor {
	private final MarkdownProcessor processor = new MarkdownProcessor();

	@Override
	public boolean shouldProcess(String name) {
		return name.endsWith(".md") || name.endsWith(".text");
	}

	@Override
	public Content process(String name, Content content) {
		return new Content(processor.markdown(content.getText()), content.getContext());
	}
}
