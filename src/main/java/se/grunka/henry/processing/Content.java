package se.grunka.henry.processing;

import java.util.Map;

public class Content {
	private final String text;
	private final Map<String, Object> context;

	public Content(String text, Map<String, Object> context) {
		this.text = text;
		this.context = context;
	}

	public String getText() {
		return text;
	}

	public Map<String, Object> getContext() {
		return context;
	}
}
