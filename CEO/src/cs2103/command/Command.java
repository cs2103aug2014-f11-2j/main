package cs2103.command;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

import cs2103.CommonUtil;
import cs2103.DeadlineTask;
import cs2103.FatalException;
import cs2103.FloatingTask;
import cs2103.HandledException;
import cs2103.PeriodicTask;
import cs2103.StorageEngine;
import cs2103.Task;
import cs2103.parameters.ParameterList;
import edu.emory.mathcs.backport.java.util.Collections;

public abstract class Command {
	protected ParameterList parameterList = new ParameterList();
	public abstract String execute() throws HandledException, FatalException;
	
	protected static Queue<String> separateCommand(String userInput) throws HandledException{
		CommonUtil.checkNullString(userInput, HandledException.ExceptionType.INVALID_CMD);
		Queue<String> result = new LinkedList<String>();
		String[] parameters = userInput.trim().split("\\s+-");
		for (String s:parameters){
			result.add(removeDash(s.trim()));
		}
		return result;
	}
	
	protected static Map<String,String> separateParameters(Queue<String> parameterList) throws HandledException{
		if (parameterList == null) throw new HandledException(HandledException.ExceptionType.INVALID_PARA);
		Map<String,String> parameterMap = new HashMap<String, String>();
		while(!parameterList.isEmpty()){
			String[] splitResult = CommonUtil.splitFirstWord(parameterList.poll());
			if (splitResult[0] != null){
				parameterMap.put(splitResult[0], splitResult[1]);
			}
		}
		return parameterMap;
	}
	
	private static String removeDash(String parameterString) throws HandledException{
		CommonUtil.checkNullString(parameterString, HandledException.ExceptionType.INVALID_PARA);
		if (parameterString.startsWith("-")){
			return parameterString.substring(1);
		}else{
			return parameterString;
		}
	}
	
	protected static ArrayList<PeriodicTask> getPeriodicList() throws HandledException, FatalException{
		ArrayList<Task> allList = getAllList();
		ArrayList<PeriodicTask> returnList = new ArrayList<PeriodicTask>();
		for (Task task:allList){
			if (task instanceof PeriodicTask){
				returnList.add((PeriodicTask) task);
			}
		}
		Collections.sort(returnList, PeriodicTask.getComparator());
		return returnList;
	}
	
	protected static ArrayList<DeadlineTask> getDeadlineList() throws HandledException, FatalException{
		ArrayList<Task> allList = getAllList();
		ArrayList<DeadlineTask> returnList = new ArrayList<DeadlineTask>();
		for (Task task:allList){
			if (task instanceof DeadlineTask){
				returnList.add((DeadlineTask) task);
			}
		}
		Collections.sort(returnList, DeadlineTask.getComparator());
		return returnList;
	}
	
	protected static ArrayList<FloatingTask> getFloatingList() throws HandledException, FatalException{
		ArrayList<Task> allList = getAllList();
		ArrayList<FloatingTask> returnList = new ArrayList<FloatingTask>();
		for (Task task:allList){
			if (task instanceof FloatingTask){
				returnList.add((FloatingTask) task);
			}
		}
		return returnList;
	}
	
	protected static ArrayList<Task> getAllList() throws HandledException, FatalException{
		return StorageEngine.getInstance().getTaskList();
	}
	
	protected static Task getTaskByID(int taskID) throws HandledException, FatalException{
		ArrayList<Task> allList = getAllList();
		if (taskID > allList.size() || taskID < 1){
			throw new HandledException(HandledException.ExceptionType.INVALID_TASKID);
		} else {
			return allList.get(taskID-1);
		}
	}
	
	protected static String getParameterString(Map<String, String> parameterMap, String[] allowedLiteral){
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
}
