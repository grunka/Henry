package se.grunka.henry.processing;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FrontMatterProcessor implements ContentProcessor {

	private final Pattern pattern = Pattern.compile("^\\s*<!--(.*?)-->\\s*(.*)$", Pattern.MULTILINE | Pattern.DOTALL);

	@Override
	public boolean shouldProcess(String name) {
		return true;
	}

	@Override
	public Content process(String name, Content content) {
		Map<String, Object> parameters = content.getContext();
		String text = content.getText();
		Matcher matcher;
		while ((matcher = pattern.matcher(text)).matches()) {
			String rows = matcher.group(1);
			for (String row : rows.split("\\n")) {
				row = row.trim();
				int space = row.indexOf(' ');
				if (space == -1) {
					parameters.put(row, "");
				} else {
					parameters.put(row.substring(0, space), row.substring(space + 1));
				}
			}
			text = matcher.group(2);
		}
		return new Content(text, parameters);
	}
}
