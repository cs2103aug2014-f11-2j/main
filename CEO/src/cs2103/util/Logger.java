//@author A0116713M
package cs2103.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;

/**
 * Log message to file
 */
public class Logger {
	private static Logger logger;
	private final File error;
	private final File log;
	
	private Logger() {
		this.error = new File("CEOError.log");
		this.log = new File("CEOLog.log");
	}
	
	public static Logger getInstance() {
		if (logger == null) {
			logger = new Logger();
		}
		return logger;
	}
	
	/**
	 * Write StackTrace to error log
	 * @param errorMsg
	 * @param exception
	 */
	public void writeErrLog(String errorMsg, Exception exception) {
		PrintWriter pw = null;
		try {
			pw = new PrintWriter(new BufferedWriter(new FileWriter(this.error, true)));
			pw.println(new Date());
			pw.println(errorMsg);
			exception.printStackTrace(pw);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (pw != null) pw.close();
		}
	}
	
	/**
	 * Write log message to log file
	 * @param logMsg
	 */
	public void writeLog(String logMsg) {
		PrintWriter pw = null;
		try {
			pw = new PrintWriter(new BufferedWriter(new FileWriter(this.log, true)));
			pw.print(new Date());
			pw.print(":\t");
			pw.println(logMsg);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (pw != null) pw.close();
		}
	}
}
