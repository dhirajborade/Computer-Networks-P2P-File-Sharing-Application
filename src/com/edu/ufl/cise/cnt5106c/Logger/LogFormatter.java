package com.edu.ufl.cise.cnt5106c.Logger;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Handler;
import java.util.logging.LogRecord;
import java.util.logging.SimpleFormatter;

public class LogFormatter extends SimpleFormatter {

	private static final DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss.SSS");

	public String format(LogRecord record) {
		StringBuilder builder = new StringBuilder();
		builder.append("[").append(dateFormat.format(new Date(record.getMillis()))).append("]: ")
				.append(record.getMessage()).append(System.getProperty("line.separator"));
		System.out.println("Record Message:" + builder);
		return builder.toString();
	}

	public String getHead(Handler h) {
		return super.getHead(h);
	}

	public String getTail(Handler h) {
		return super.getTail(h);
	}

}
