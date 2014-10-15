package cs2103;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.fortuna.ical4j.model.Recur;

class CommandParser {
	public static enum CommandType {
		ADD, LIST, SHOWDETAIL, DELETE, UPDATE, EXIT, UNDO, REDO, HELP, SEARCH, QUICK, INVALID;
	}
	
	public static enum TaskType {
		ALL, FLOATING, DEADLINE, PERIODIC, INVALID;
	}
	
	public static final String[] allowedTitleLiteral = {"S", "title", "summary"};
	public static final String[] allowedDescriptionLiteral = {"D", "description", "detail"};
	public static final String[] allowedLocationLiteral = {"L", "location", "place"};
	public static final String[] allowedCompleteLiteral = {"C", "complete", "status"};
	public static final String[] allowedTimeLiteral = {"T", "time", "from", "by"};
	public static final String[] allowedRecurrenceLiteral = {"R", "reccuring", "recur"};
	public static final String[] allowedKeywordLiteral = {"K", "keyword"};
	private static final String[] multiParameterCommands = {"add", "update", "search", "new", "modify", "find"};
	private static final String[] allowedSeparateLiteral = {"\\s+-", "\\s+/", ";"};
	
	public static Queue<String> separateCommand(String userInput) throws HandledException{
		checkNullString(userInput, HandledException.ExceptionType.INVALID_CMD);
		Queue<String> result = new LinkedList<String>();
		if (checkMultiParameter(splitFirstWord(userInput)[0])){
			String[] parameters = splitMultiParameter(userInput);
			String[] command = splitFirstWord(parameters[0]);
			for (String s:command){
				if (s != null) result.add(s.trim());
			}
			for (int i = 1;i < parameters.length; i++){
				result.add(removeDash(parameters[i]));
			}
		} else {
			String[] command = splitFirstWord(userInput);
			for (String s:command){
				if (s != null) result.add(s.trim());
			}
		}
		return result;
	}
	
	public static CommandType determineCommandType(String command) {
		if (command == null){
			return CommandType.INVALID;
		}
		if (command.equalsIgnoreCase("list")){
			return CommandType.LIST;
		} else if (command.equalsIgnoreCase("update") || command.equalsIgnoreCase("modify")){
			return CommandType.UPDATE;
		} else if (command.equalsIgnoreCase("exit") || command.equalsIgnoreCase("bye")){
			return CommandType.EXIT;
		} else if (command.equalsIgnoreCase("add") || command.equalsIgnoreCase("new")){
			return CommandType.ADD;
		} else if (command.equalsIgnoreCase("delete") || command.equalsIgnoreCase("remove") || command.equalsIgnoreCase("del")){
			return CommandType.DELETE;
		} else if (command.equalsIgnoreCase("show") || command.equalsIgnoreCase("detail")){
			return CommandType.SHOWDETAIL;
		} else if (command.equalsIgnoreCase("undo")){
			return CommandType.UNDO;
		} else if (command.equalsIgnoreCase("redo")){
			return CommandType.REDO;
		} else if (command.equalsIgnoreCase("help")){
			return CommandType.HELP;
		} else if (command.equalsIgnoreCase("search") || command.equalsIgnoreCase("find")){
			return CommandType.SEARCH;
		} else if (command.equalsIgnoreCase("quick") || command.equalsIgnoreCase("q")){
			return CommandType.QUICK;
		} else {
			return CommandType.INVALID;
		}
	}
	
	public static TaskType determineTaskType(String parameter){
		if (parameter == null){
			return TaskType.INVALID;
		}
		if (parameter.equalsIgnoreCase("all")){
			return TaskType.ALL;
		} else if (parameter.equalsIgnoreCase("floating")){
			return TaskType.FLOATING;
		} else if (parameter.equalsIgnoreCase("deadline")){
			return TaskType.DEADLINE;
		} else if (parameter.equalsIgnoreCase("periodic")){
			return TaskType.PERIODIC;
		} else {
			return TaskType.INVALID;
		}
	}
	
	public static int parseIntegerParameter(String parameter) throws HandledException {
		checkNullString(parameter, HandledException.ExceptionType.INVALID_PARA);
		parameter = parameter.trim();
		if (parameter.matches("[0-9]+")){
			return Integer.parseInt(parameter);
		} else {
			return -1;
		}
	}
	
	public static Map<String,String> separateParameters(Queue<String> parameterList) throws HandledException{
		if (parameterList == null) throw new HandledException(HandledException.ExceptionType.INVALID_PARA);
		Map<String,String> parameterMap = new HashMap<String, String>();
		while(!parameterList.isEmpty()){
			String[] splitResult = splitFirstWord(parameterList.poll());
			if (splitResult[0] != null){
				parameterMap.put(splitResult[0], splitResult[1]);
			}
		}
		return parameterMap;
	}
	
	public static String getParameterString(Map<String, String> parameterMap, String[] allowedLiteral){
		for (String s:allowedLiteral){
			String result = getParameterFromMap(parameterMap, s);
			if (result != null) return result;
		}
		return null;
	}
	
	private static String getParameterFromMap(Map<String, String> parameterMap, String parameterType){
		if (parameterMap.containsKey(parameterType)){
			String value=parameterMap.get(parameterType);
			if (value == null){
				return "";
			}else{
				return value;
			}
		}else{
			return null;
		}
	}
	
	public static Date[] getTime(String timeString) throws HandledException{
		Date[] time = new Date[2];
		time[0] = null; time[1] = null;
		if (timeString != null){
			Pattern p = Pattern.compile("\\d{4}/\\d{2}/\\d{2}\\s\\d{2}:\\d{2}");
			Matcher m = p.matcher(timeString);
			int i = 0;
			while(m.find() && i < 2){
				time[i] = stringToDate(m.group());
				i++;
			}
		}
		return time;
	}
	
	public static boolean parseComplete(String complete) throws HandledException {
		if (complete == null || complete.equals("")){
			return true;
		} else if (complete.equalsIgnoreCase("true")){
			return true;
		} else if (complete.equalsIgnoreCase("false")){
			return false;
		} else {
			throw new HandledException(HandledException.ExceptionType.INVALID_COMPLETE);
		}
	}
	
	private static void checkNullString(String str, HandledException.ExceptionType expectedException) throws HandledException{
		if (str == null || str.equals("")) throw new HandledException(expectedException);
	}
	
	private static boolean checkMultiParameter(String commandType) throws HandledException{
		checkNullString(commandType, HandledException.ExceptionType.INVALID_CMD);
		for (String s:multiParameterCommands){
			if (commandType.equalsIgnoreCase(s)){
				return true;
			}
		}
		return false;
	}
	
	private static String[] splitMultiParameter(String userInput) throws HandledException{
		checkNullString(userInput, HandledException.ExceptionType.INVALID_CMD);
		String[] parameters;
		for (String regex:allowedSeparateLiteral){
			parameters = userInput.trim().split(regex);
			if (parameters.length > 1) return parameters;
		}
		throw new HandledException(HandledException.ExceptionType.LESS_THAN_ONE_PARA);
	}
	
	private static String removeDash(String parameterString) throws HandledException{
		checkNullString(parameterString, HandledException.ExceptionType.INVALID_PARA);
		if (parameterString.startsWith("-")){
			return parameterString.substring(1);
		}else{
			return parameterString;
		}
	}
	
	private static String[] splitFirstWord(String parameterString) throws HandledException{
		checkNullString(parameterString, HandledException.ExceptionType.INVALID_PARA);
		String[] result = new String[2];;
		int spaceIndex = parameterString.indexOf(' ');
		if (spaceIndex == -1){
			result[0] = parameterString;
			result[1] = null;
			return result;
		}else{
			result[0] = parameterString.substring(0, spaceIndex).trim();
			result[1] = parameterString.substring(spaceIndex).trim();
			return result;
		}
	}
	
	private static Date stringToDate(String timeString) throws HandledException{
		if (timeString == null){
			return null;
		}
		try {
			TimeZone tz=TimeZone.getDefault();
			SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy/MM/dd HH:mm");
			dateFormat.setTimeZone(tz);
			return dateFormat.parse(timeString);
		} catch (ParseException e) {
			throw new HandledException(HandledException.ExceptionType.INVALID_TIME);
		}
	}
	
	public static Recur stringToRecur(String recurrence) throws HandledException{
		if (recurrence == null || recurrence.equals("")){
			return null;
		}
		Pattern p = Pattern.compile("([0-9]+)([hdwmy])");
		Matcher m = p.matcher(recurrence);
		if (m.find()){
			int interval=Integer.parseInt(m.group(1));
			String frequency;
			String found=m.group(2);
			if (found.equals("h")){
				frequency=Recur.HOURLY;
			} else if (found.equals("d")){
				frequency=Recur.DAILY;
			} else if (found.equals("w")){
				frequency=Recur.WEEKLY;
			} else if (found.equals("m")){
				frequency=Recur.MONTHLY;
			} else if (found.equals("y")){
				frequency=Recur.YEARLY;
			} else {
				throw new HandledException(HandledException.ExceptionType.INVALID_RECUR);
			}
			Recur recur=new Recur();
			recur.setFrequency(frequency);
			recur.setInterval(interval);
			return recur;
		} else if (recurrence.equals("0")){
			return null;
		} else {
			throw new HandledException(HandledException.ExceptionType.INVALID_RECUR);
		}
	}
}
