package cs2103;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.TimeZone;

class CommandParser {

	public static Queue<String> seperateCommand(String userInput) {
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
	
	public static Map<String,String> parseParameters(Queue<String> parameterList) throws CEOException{
		Map<String,String> parameterMap = new HashMap<String, String>();
		if (!parameterList.peek().matches("-\\S+")){
			throw new CEOException("Invalid Parameter");
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
		return parameterMap;
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
			throw new CEOException("Invalid complete type");
		}
	}
	//Write all methods static
}
