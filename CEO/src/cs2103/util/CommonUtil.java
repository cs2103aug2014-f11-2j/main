package cs2103.util;

import java.awt.Desktop;
import java.awt.Desktop.Action;
import java.io.IOException;
import java.net.InetAddress;

import org.fusesource.jansi.Ansi;
import org.fusesource.jansi.AnsiConsole;
import static org.fusesource.jansi.Ansi.*;
import static org.fusesource.jansi.Ansi.Color.*;

import cs2103.exception.FatalException;
import cs2103.exception.HandledException;

public class CommonUtil {
	
	public static String[] splitFirstWord(String parameterString) throws HandledException{
		checkNull(parameterString, HandledException.ExceptionType.INVALID_CMD);
		String[] result = new String[2];
		int splitIndex = parameterString.indexOf(' ');
		if (splitIndex == -1){
			result[0] = parameterString;
			result[1] = null;
		}else{
			result[0] = parameterString.substring(0, splitIndex).trim();
			result[1] = parameterString.substring(splitIndex).trim();
		}
		return result;
	}
	
	public static int parseIntegerParameter(String parameter) throws HandledException {
		checkNull(parameter, HandledException.ExceptionType.INVALID_PARA);
		parameter = parameter.trim();
		if (parameter.matches("[0-9]+")){
			return Integer.parseInt(parameter);
		} else {
			return -1;
		}
	}
	
	public static void checkNull(Object obj, HandledException.ExceptionType expectedException) throws HandledException{
		if (obj == null) throw new HandledException(expectedException);
	}
	
	public static void checkNull(Object obj, FatalException.ExceptionType expectedException) throws FatalException {
		if (obj == null) throw new FatalException(expectedException);
	}

	public static String removeDash(String parameterString) throws HandledException{
		checkNull(parameterString, HandledException.ExceptionType.INVALID_PARA);
		if (parameterString.startsWith("-")){
			return parameterString.substring(1);
		}else{
			return parameterString;
		}
	}
	
	public static void print(String feedback){
		if (feedback != null && !feedback.isEmpty()){
			print(ansi().a(feedback));
		}
	}
	
	public static void print(Ansi feedback) {
		if (feedback != null){
			AnsiConsole.out.print(feedback);
		}
	}
	
	public static void printErrMsg(String errorMsg){
		if (errorMsg != null && !errorMsg.isEmpty()){
			print(ansi().bold().bg(RED).a(errorMsg).a('\n').reset());
		}
	}
	
	public static void printPrompt(String prompt){
		if (prompt != null && !prompt.isEmpty()){
			System.out.print(prompt);
		}
	}
	
	public static boolean checkSyncSupport(){
		return checkNetwork() && checkBrowser();
	}
	
	private static boolean checkNetwork(){
		try {
			InetAddress ad = InetAddress.getByName("accounts.google.com");
			return ad.isReachable(5000);
		} catch (IOException e) {
			return false;
		}
	}
	
	private static boolean checkBrowser(){
		try{
			if (Desktop.isDesktopSupported()) {
				Desktop desktop = Desktop.getDesktop();
				if (desktop.isSupported(Action.BROWSE)) {
					return true;
				}
			}
			return false;
		} catch (InternalError e) {
			return checkBrowser();
		}
	}
	
	public static void clearConsole(){
		try{
			if (System.getProperty("os.name").contains("Windows")){
		        Console.clr();
			} else {
				AnsiConsole.out.println(ansi().eraseScreen().reset());
			}
		} catch (UnsatisfiedLinkError | NoClassDefFoundError e) {
			AnsiConsole.out.println(ansi().eraseScreen().reset());
		}
	}
}
