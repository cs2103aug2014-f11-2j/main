package cs2103.exception;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;

class ErrorLogging {
	private static ErrorLogging log;
	private final File file;
	
	private ErrorLogging(){
		this.file = new File("error.log"); 
	}
	
	public static ErrorLogging getInstance(){
		if (log == null){
			log = new ErrorLogging();
		}
		return log;
	}
	
	public void writeToLog(String errorMsg, Exception exception){
		PrintWriter pw = null;
		try {
			pw = new PrintWriter(new BufferedWriter(new FileWriter(this.file, true)));
			pw.println(new Date());
			pw.println(errorMsg);
			exception.printStackTrace(pw);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (pw != null) pw.close();
		}
	}
	
	public void printErrorMsg(String errorMsg){
		if (errorMsg != null) System.err.println(errorMsg);
	}
}
