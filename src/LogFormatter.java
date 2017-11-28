import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Handler;
import java.util.logging.LogRecord;
import java.util.logging.SimpleFormatter;

/**
 *
 */

/**
 * @author apurv
 *
 */
public class LogFormatter extends SimpleFormatter {

	// Create a DateFormat to format the logger timestamp.
	private static final DateFormat df = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss.SSS");

	public String format(LogRecord record) {
		StringBuilder builder = new StringBuilder();
		builder.append("[").append(df.format(new Date(record.getMillis()))).append("]: ");
		builder.append(record.getMessage());
		builder.append(System.getProperty("line.separator"));
		System.out.println("Record message:" + builder);
		return builder.toString();
	}

	public String getHead(Handler h) {
		return super.getHead(h);
	}

	public String getTail(Handler h) {
		return super.getTail(h);
	}

}
