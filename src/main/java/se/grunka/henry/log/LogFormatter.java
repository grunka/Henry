package se.grunka.henry.log;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public class LogFormatter extends Formatter {
	private final DateFormat date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	@SuppressWarnings({"ThrowableResultOfMethodCallIgnored"})
    @Override
	public String format(LogRecord record) {
		StringBuilder message = new StringBuilder();
		message.append("[").append(date.format(record.getMillis())).append("]");
		message.append("[").append(record.getLevel()).append("]");
		message.append("[").append(record.getLoggerName()).append("]");
		message.append(" ").append(record.getMessage());
		Throwable thrown = record.getThrown();
		if (thrown != null) {
			StringWriter stackTrace = new StringWriter();
			PrintWriter writer = new PrintWriter(stackTrace);
			thrown.printStackTrace(writer);
			message.append(stackTrace.toString());
		}
		message.append("\n");
		return message.toString();
	}
}
