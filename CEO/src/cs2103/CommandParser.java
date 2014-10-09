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
		ADD, LIST, SHOWDETAIL, DELETE, UPDATE, EXIT, INVALID, UNDO, REDO;
	}
	
	public static enum TaskType {
		ALL, FLOATING, DEADLINE, PERIODIC, INVALID;
	}
	
	private static String[] multiParameterCommands = {"add", "update", "search"};
	private static String[] allowedSeparateLiteral = {"\\s+-", "\\s+/", ";"};
	
	public static Queue<String> separateCommand(String userInput) throws CEOException {
		if (userInput == null) throw new CEOException(CEOException.INVALID_CMD);
		Queue<String> result = new LinkedList<String>();
		if (checkMultiParameter(splitFirstWord(userInput)[0])){
			String[] parameters = splitMultiParameter(userInput);
			String[] command = splitFirstWord(parameters[0]);
			for (String s:command){
				result.add(s.trim());
			}
			for (int i = 1;i < parameters.length; i++){
				result.add(removeDash(parameters[i]));
			}
		} else {
			String[] command = splitFirstWord(userInput);
			for (String s:command){
				if (s != null){
					result.add(s.trim());
				}
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
		} else if (command.equalsIgnoreCase("update")){
			return CommandType.UPDATE;
		} else if (command.equalsIgnoreCase("exit")){
			return CommandType.EXIT;
		} else if (command.equalsIgnoreCase("add")){
			return CommandType.ADD;
		} else if (command.equalsIgnoreCase("delete")){
			return CommandType.DELETE;
		} else if (command.equalsIgnoreCase("show")){
			return CommandType.SHOWDETAIL;
		} else if (command.equalsIgnoreCase("undo")){
			return CommandType.UNDO;
		} else if (command.equalsIgnoreCase("redo")){
			return CommandType.REDO;
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
	
	public static int parseIntegerParameter(String parameters){
		if (parameters == null || parameters.equals("")){
			return -1;
		} else {
			parameters=parameters.trim();
			if (parameters.matches("[0-9]+")){
				return Integer.parseInt(parameters);
			} else {
				return -1;
			}
		}
	}
	
	public static Map<String,String> separateParameters(Queue<String> parameterList) throws CEOException{
		if (parameterList == null) throw new CEOException(CEOException.INVALID_PARA);
		Map<String,String> parameterMap = new HashMap<String, String>();
		while(!parameterList.isEmpty()){
			String[] splitResult = splitFirstWord(parameterList.poll());
			if (splitResult[0] != null){
				parameterMap.put(splitResult[0], splitResult[1]);
			}
		}
		return parameterMap;
	}
	
	public static String getParameter(String parameterType, Map<String, String> parameterMap){
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
	
	public static String getTitle(Map<String, String> parameterMap){
		Queue<String> keywordQueue = new LinkedList<String>();
		keywordQueue.add("N");
		keywordQueue.add("title");
		String result = null;
		while(result == null && !keywordQueue.isEmpty()){
			result = getParameter(keywordQueue.poll(), parameterMap);
		}
		return result;
	}
	
	public static String getDescription(Map<String, String> parameterMap){
		Queue<String> keywordQueue = new LinkedList<String>();
		keywordQueue.add("D");
		keywordQueue.add("description");
		String result = null;
		while(result == null && !keywordQueue.isEmpty()){
			result = getParameter(keywordQueue.poll(), parameterMap);
		}
		return result;
	}
	
	public static String getLocation(Map<String, String> parameterMap){
		Queue<String> keywordQueue = new LinkedList<String>();
		keywordQueue.add("L");
		keywordQueue.add("location");
		String result = null;
		while(result == null && !keywordQueue.isEmpty()){
			result = getParameter(keywordQueue.poll(), parameterMap);
		}
		return result;
	}

	
	public static String getComplete(Map<String, String> parameterMap){
		Queue<String> keywordQueue = new LinkedList<String>();
		keywordQueue.add("C");
		keywordQueue.add("complete");
		String result = null;
		while(result == null && !keywordQueue.isEmpty()){
			result = getParameter(keywordQueue.poll(), parameterMap);
		}
		return result;
	}
	
	public static String getTimeString(Map<String, String> parameterMap){
		Queue<String> keywordQueue = new LinkedList<String>();
		keywordQueue.add("T");
		keywordQueue.add("time");
		String result = null;
		while(result == null && !keywordQueue.isEmpty()){
			result = getParameter(keywordQueue.poll(), parameterMap);
		}
		return result;
	}
	
	public static Date[] getTime(String timeString) throws CEOException{
		Date[] time = new Date[2];
		time[0] = null; time[1] = null;
		if (timeString != null){
			Pattern p = Pattern.compile("\\d{4}/\\d{2}/\\d{2}/\\d{2}:\\d{2}");
			Matcher m = p.matcher(timeString);
			int i = 0;
			while(m.find() && i < 2){
				time[i] = stringToDate(m.group());
				i++;
			}
		}
		return time;
	}
	
	public static String getRecurString(Map<String, String> parameterMap){
		Queue<String> keywordQueue = new LinkedList<String>();
		keywordQueue.add("R");
		keywordQueue.add("recurrence");
		String result = null;
		while(result == null && !keywordQueue.isEmpty()){
			result = getParameter(keywordQueue.poll(), parameterMap);
		}
		return result;
	}
	
	
	public static boolean parseComplete(String complete) throws CEOException{
		if (complete == null){
			return false;
		} else if (complete.equalsIgnoreCase("true")){
			return true;
		} else if (complete.equalsIgnoreCase("false")){
			return false;
		} else {
			throw new CEOException(CEOException.INVALID_COMPLETE);
		}
	}
	
	private static boolean checkMultiParameter(String commandType) throws CEOException{
		if (commandType == null) throw new CEOException(CEOException.INVALID_CMD);
		for (String s:multiParameterCommands){
			if (commandType.equalsIgnoreCase(s)){
				return true;
			}
		}
		return false;
	}
	
	private static String[] splitMultiParameter(String userInput) throws CEOException{
		if (userInput == null) throw new CEOException(CEOException.INVALID_CMD);
		String[] parameters;
		for (String regex:allowedSeparateLiteral){
			parameters = userInput.trim().split(regex);
			if (parameters.length > 1) return parameters;
		}
		throw new CEOException(CEOException.LESS_THAN_ONE_PARA);
	}
	
	private static String removeDash(String parameterString) throws CEOException{
		if (parameterString == null) throw new CEOException(CEOException.INVALID_PARA);
		if (parameterString.startsWith("-")){
			return parameterString.substring(1);
		}else{
			return parameterString;
		}
	}
	
	private static String[] splitFirstWord(String parameterString) throws CEOException{
		if (parameterString == null) throw new CEOException(CEOException.INVALID_PARA);
		String[] result;
		if (parameterString == null || parameterString.equals("")) return null;
		int spaceIndex = parameterString.indexOf(' ');
		if (spaceIndex == -1){
			result = new String[1];
			result[0] = parameterString;
			return result;
		}else{
			result = new String[2];
			result[0] = parameterString.substring(0, spaceIndex).trim();
			result[1] = parameterString.substring(spaceIndex).trim();
			return result;
		}
	}
	
	private static Date stringToDate(String timeString) throws CEOException{
		if (timeString == null){
			return null;
		}
		try {
			TimeZone tz=TimeZone.getDefault();
			SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy/MM/dd/HH:mm");
			dateFormat.setTimeZone(tz);
			return dateFormat.parse(timeString);
		} catch (ParseException e) {
			throw new CEOException(CEOException.INVALID_TIME);
		}
	}
	
	public static Recur stringToRecur(String recurrence) throws CEOException{
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
				throw new CEOException(CEOException.INVALID_RECUR);
			}
			Recur recur=new Recur();
			recur.setFrequency(frequency);
			recur.setInterval(interval);
			return recur;
		} else if (recurrence.equals("0")){
			return null;
		} else {
			throw new CEOException(CEOException.INVALID_RECUR);
		}
	}
}
