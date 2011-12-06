package se.grunka.henry.processing;

import java.util.Arrays;
import java.util.List;

import com.google.inject.Inject;

public class PipelineProcessor implements ContentProcessor {

	private final List<ContentProcessor> processors;

	@Inject
	public PipelineProcessor(FrontMatterProcessor frontMatter, MarkdownTextProcessor markdown, MustacheProcessor mustache, LayoutProcessor layout) {
		this.processors = Arrays.asList(frontMatter, markdown, mustache, layout);
	}

	@Override
	public boolean shouldProcess(String name) {
		return true;
	}

	@Override
	public Content process(String name, Content content) {
		for (ContentProcessor processor : processors) {
			if (processor.shouldProcess(name)) {
				content = processor.process(name, content);
			}
		}
		return content;
	}
}
