//@author A0110906R
package cs2103.util;

import java.awt.Desktop;
import java.awt.Desktop.Action;

import org.fusesource.jansi.Ansi;
import org.fusesource.jansi.AnsiConsole;

import static org.fusesource.jansi.Ansi.*;
import static org.fusesource.jansi.Ansi.Color.*;
import cs2103.exception.FatalException;
import cs2103.exception.HandledException;
import cs2103.task.Task;

/**
 * @author Zheng Han
 * Carries out the basic utilities in CEO
 */
public class CommonUtil {
	/**
	 * Splits the first word from the rest of the string argument
	 * and returns an array which contains this first word and
	 * the rest of the string.
	 * 
	 * @param parameterString
	 * @return an array which contains the split up String
	 * @throws HandledException
	 */
	public static String[] splitFirstWord(String parameterString) throws HandledException {
		checkNull(parameterString, HandledException.ExceptionType.INVALID_CMD);
		assert(parameterString != null);
		String[] result = new String[2];
		int splitIndex = parameterString.indexOf(' ');
		if (splitIndex == -1) {
			result[0] = parameterString;
			result[1] = null;
		} else {
			result[0] = parameterString.substring(0, splitIndex).trim();
			result[1] = parameterString.substring(splitIndex).trim();
		}
		return result;
	}
	
	/**
	 * Parse an integer from the string argument and returns this integer.
	 * 
	 * @param parameter
	 * @return the parsed integer from the String parameter
	 * @throws HandledException
	 */
	public static int parseIntegerParameter(String parameter) throws HandledException {
		checkNull(parameter, HandledException.ExceptionType.INVALID_PARA);
		assert(parameter != null);
		parameter = parameter.trim();
		if (parameter.matches("[0-9]+")) {
			return Integer.parseInt(parameter);
		} else {
			return -1;
		}
	}
	
	/**
	 * Checks if the Object argument is a null object. Throws a 
	 * HandledException if Object argument is null.
	 *  
	 * @param obj
	 * @param expectedException
	 * @throws HandledException
	 */
	public static void checkNull(Object obj, HandledException.ExceptionType expectedException) throws HandledException {
		if (obj == null) throw new HandledException(expectedException);
	}
	
	/**
	 * Checks if the Object argument is a null object. Throws a 
	 * FatalException if Object argument is a null object.
	 * 
	 * @param obj
	 * @param expectedException
	 * @throws FatalException
	 */
	public static void checkNull(Object obj, FatalException.ExceptionType expectedException) throws FatalException {
		if (obj == null) throw new FatalException(expectedException);
	}
	
	/**
	 * Removes the dash character from the String argument and returns the
	 * resultant String argument.
	 * 
	 * @param parameterString
	 * @return parameterString with the front dash removed
	 * @throws HandledException
	 */
	public static String removeDash(String parameterString) throws HandledException {
		checkNull(parameterString, HandledException.ExceptionType.INVALID_PARA);
		assert(parameterString != null);
		if (parameterString.startsWith("-")) {
			return parameterString.substring(1);
		}else{
			return parameterString;
		}
	}
	
	/**
	 * Formats the String parameter with the Task parameter and returns
	 * the formatted string.
	 * 
	 * @param format
	 * @param task
	 * @return a String containing the formatted log information
	 */
	public static String formatLogString(String format, Task task) {
		assert(task != null);
		return String.format(format, task.getTaskUID());
	}
	
	/**
	 * Prints the String argument in ANSI format if the String
	 * argument is not a null object and is not an empty String.
	 * 
	 * @param feedback
	 */
	public static void print(String feedback) {
		if (feedback != null && !feedback.isEmpty()) {
			print(ansi().a(feedback));
		}
	}
	
	/**
	 * Prints the Ansi argument if the Ansi argument is not a
	 * null object.
	 * 
	 * @param feedback
	 */
	public static void print(Ansi feedback) {
		if (feedback != null) {
			AnsiConsole.out.print(feedback);
		}
	}
	
	/**
	 * Prints the error message from the String argument if the String
	 * argument is not a null object and is not an empty String.
	 * 
	 * @param errorMsg
	 */
	public static void printErrMsg(String errorMsg) {
		if (errorMsg != null && !errorMsg.isEmpty()) {
			print(ansi().bold().bg(RED).a(errorMsg).a('\n').reset());
		}
	}
	
	/**
	 * Prints the String argument if it is not a null object
	 * and if it is not an empty String.
	 * 
	 * @param prompt
	 */
	public static void printPrompt(String prompt) {
		if (prompt != null && !prompt.isEmpty()) {
			System.out.print(prompt);
		}
	}
	
	/**
	 * Checks to see if the system supports google sync.
	 * 
	 * @return a true value if system supports google sync false otherwise
	 */
	public static boolean checkSyncSupport() {
		try{
			if (Desktop.isDesktopSupported()) {
				Desktop desktop = Desktop.getDesktop();
				if (desktop.isSupported(Action.BROWSE)) {
					return true;
				}
			}
			return false;
		} catch (InternalError e) {
			return checkSyncSupport();
		}
	}
	
	/**
	 * This method clears the user display
	 */
	public static void clearConsole() {
		try{
			if (System.getProperty("os.name").contains("Windows")) {
		        Console.clr();
			} else {
				AnsiConsole.out.println(ansi().eraseScreen().reset());
			}
		} catch (UnsatisfiedLinkError | NoClassDefFoundError e) {
			AnsiConsole.out.println(ansi().eraseScreen().reset());
		}
	}
}
