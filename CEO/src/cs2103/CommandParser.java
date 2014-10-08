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
	
	public static Queue<String> separateCommand(String userInput) {
		String[] parameters = userInput.trim().split("\\s+-");
		String[] command = parameters[0].split("\\s+");
		Queue<String> result = new LinkedList<String>();
		for (String s:command){
			result.add(s.trim());
		}
		for (int i = 1;i < parameters.length; i++){
			result.add(removeDash(parameters[i]));
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
			parameterMap.put(splitResult[0], splitResult[1]);
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
	
	private static String removeDash(String parameterString){
		if (parameterString.startsWith("-")){
			return parameterString.substring(1);
		}else{
			return parameterString;
		}
	}
	
	private static String[] splitFirstWord(String parameterString){
		String[] result = new String[2];
		result[0] = null; result[1] = null;
		if (parameterString == null || parameterString.equals("")) return result;
		int spaceIndex = parameterString.indexOf(' ');
		if (spaceIndex == -1){
			result[0] = parameterString;
			return result;
		}else{
			result[0] = parameterString.substring(0, spaceIndex);
			result[1] = parameterString.substring(spaceIndex);
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
