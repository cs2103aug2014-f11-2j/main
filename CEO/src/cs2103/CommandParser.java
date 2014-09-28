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

class CommandParser {
	public static Queue<String> separateCommand(String userInput) {
		String[] parameters = userInput.trim().split("\\s+");
		Queue<String> result = new LinkedList<String>();
		for (String s:parameters){
			result.add(s);
		}
		return result;
	}
	
	public static CommandLineUI.CommandType determineCommandType(String command) {
		if (command==null){
			return CommandLineUI.CommandType.INVALID;
		}
		if (command.equalsIgnoreCase("list")){
			return CommandLineUI.CommandType.LIST;
		}else if (command.equalsIgnoreCase("update")){
			return CommandLineUI.CommandType.UPDATE;
		}else if (command.equalsIgnoreCase("exit")){
			return CommandLineUI.CommandType.EXIT;
		}else if (command.equalsIgnoreCase("add")){
			return CommandLineUI.CommandType.ADD;
		}else if (command.equalsIgnoreCase("delete")){
			return CommandLineUI.CommandType.DELETE;
		}else if (command.equalsIgnoreCase("show")){
			return CommandLineUI.CommandType.SHOWDETAIL;
		}else{
			return CommandLineUI.CommandType.INVALID;
		}
	}
	
	public static CommandLineUI.TaskType determineTaskType(String parameter){
		if (parameter==null){
			return CommandLineUI.TaskType.INVALID;
		}
		if (parameter.equalsIgnoreCase("all")){
			return CommandLineUI.TaskType.ALL;
		}else if (parameter.equalsIgnoreCase("floating")){
			return CommandLineUI.TaskType.FLOATING;
		}else if (parameter.equalsIgnoreCase("deadline")){
			return CommandLineUI.TaskType.DEADLINE;
		}else if (parameter.equalsIgnoreCase("periodic")){
			return CommandLineUI.TaskType.PERIODIC;
		}else{
			return CommandLineUI.TaskType.INVALID;
		}
	}
	
	public static int parseIntegerParameter(String parameters){
		if (parameters==null || parameters.equals("")){
			return -1;
		}else{
			parameters=parameters.trim();
			if (parameters.matches("[0-9]+")){
				return Integer.parseInt(parameters);
			}else{
				return -1;
			}
		}
	}
	
	public static Map<String,String> separateParameters(Queue<String> parameterList) throws CEOException{
		Map<String,String> parameterMap = new HashMap<String, String>();
		if (!parameterList.peek().matches("-\\S+")){
			throw new CEOException(CEOException.INVALID_PARA);
		}
		String parameterType=null;
		StringBuffer parameter = new StringBuffer();
		while(!parameterList.isEmpty()){
			String parameterString = parameterList.poll();
			if (parameterString.matches("--\\w+|-[A-Z]")){
				if (parameterType!=null){
					parameterMap.put(parameterType, parameter.toString().trim());
					parameter=new StringBuffer();
				}
				if (parameterString.matches("--\\w+")){
					parameterType=parameterString.substring(2);
				}else if (parameterString.matches("-[A-Z]")){
					parameterType=parameterString.substring(1);
				}
			}else{
				parameter.append(parameterString).append(' ');
			}
		}
		if (parameterType!=null){
			parameterMap.put(parameterType, parameter.toString().trim());
		}
		return parameterMap;
	}
	
	public static String getParameter(String parameterType, Map<String, String> parameterMap){
		if (parameterMap.containsKey(parameterType)){
			String value=parameterMap.get(parameterType);
			if (value==null){
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
	
	public static String[] getTime(String timeString) throws CEOException{
		String[] time = new String[2];
		time[0]=null; time[1]=null;
		if (timeString!=null){
			Pattern p = Pattern.compile("\\d{4}/\\d{2}/\\d{2}/\\d{2}:\\d{2}");
			Matcher m = p.matcher(timeString);
			int i = 0;
			while(m.find() && i < 2){
				time[i] = m.group();
				i++;
			}
		}
		return time;
	}
	
	public static Date stringToDate(String timeString) throws ParseException{
		TimeZone tz=TimeZone.getDefault();
		SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy/MM/dd/HH:mm");
		dateFormat.setTimeZone(tz);
		return dateFormat.parse(timeString);
	}
	
	public static boolean parseComplete(String complete) throws CEOException{
		if (complete==null){
			return false;
		}else if (complete.equalsIgnoreCase("true")){
			return true;
		}else if (complete.equalsIgnoreCase("false")){
			return false;
		}else{
			throw new CEOException(CEOException.INVALID_COMPLETE);
		}
	}
	
	/*
	public Recur stringToRecur(String recurrence) throws CEOException{
		Pattern p = Pattern.compile("([0-9]+)([hdwmy])([0-9]+)");
		Matcher m = p.matcher(recurrence);
		if(m.find()){
			int interval=Integer.parseInt(m.group(1));
			int count=Integer.parseInt(m.group(3));
			String frequency;
			String found=m.group(2);
			if (found.equals("h")){
				frequency=Recur.HOURLY;
			}else if (found.equals("d")){
				frequency=Recur.DAILY;
			}else if (found.equals("w")){
				frequency=Recur.WEEKLY;
			}else if (found.equals("m")){
				frequency=Recur.MONTHLY;
			}else if (found.equals("y")){
				frequency=Recur.YEARLY;
			}else{
				throw new CEOException("Invalid Recurrence");
			}
			Recur recur=new Recur(frequency, count);
			recur.setInterval(interval);
			return recur;
		}else if (recurrence.equals("0")){
			return null;
		}else{
			throw new CEOException("Invalid Recurrence");
		}
	}
	*/
}
