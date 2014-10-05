package cs2103;

import java.util.Map;
import java.util.Queue;
import java.util.Scanner;
import java.util.Date;
import java.util.ArrayList;

import net.fortuna.ical4j.model.Recur;

public class CommandLineUI {
	private static final String MESSAGE_WELCOME_FORMAT = "Welcome to the CEO. %1$s is ready for use.";
	private static final String MESSAGE_EXIT = "You have exited CEO. Hope to see you again.";
	private static final String MESSAGE_USER_PROMPT = "Command me please: ";
	private static final String MESSAGE_COMMAND_ERROR = "Your input command is invalid, please check your command and try again";
	private static final String MESSAGE_INVALID_TASKTYPE_FORMAT = "Your input Task Type %1$s is invalid, corrected to All";
	private static final String MESSAGE_DELETE_FORMAT = "You have deleted task with ID %1$s";
	private static final String MESSAGE_DELETE_ERROR_FORMAT = "Failed to delete task with ID %1$s";
	private static final String MESSAGE_ADD = "You have added a new task.";
	private static final String MESSAGE_ADD_ERROR = "Failed to add new task";
	private static final String MESSAGE_UPDATE_FORMAT = "You have updated task with ID %1$s";
	private static final String MESSAGE_UPDATE_ERROR_FORMAT = "Failed to update task with ID %1$s";
	private static final String MESSAGE_SHOW_ERROR_FORMAT = "Failed to show task with ID %1$s";
	private static final String MESSAGE_UNDO_FORMAT = "Successfully undo %1$d tasks";
	
	private final CommandExecutor executor;
	private Scanner scanner = new Scanner(System.in);
	
	public CommandLineUI(String dataFile){
		this.executor = new CommandExecutor(dataFile);
		print(String.format(MESSAGE_WELCOME_FORMAT, dataFile));
	}
	
	public static void main(String[] args){
		CommandLineUI main;
		if (args.length > 1){
			System.err.println("Incorrect Argument");
			main = new CommandLineUI(args[0]);
		}else if (args.length == 1){
			main = new CommandLineUI(args[0]);
		}else{
			main = new CommandLineUI("default.ics");
		}
		main.userLoop();
	}
	
	private void userLoop() {
		String feedback;
		alertTask();
		updateTimeFromRecur();
		while (true) {
			printPrompt(MESSAGE_USER_PROMPT);
			feedback=takeUserInput();
			if (feedback.equalsIgnoreCase("EXIT")){
				print(MESSAGE_EXIT);
				break;
			}
			print(feedback);
		}
	}
	
	private String takeUserInput() {
		String userInput = scanner.nextLine();
		Queue<String> separateResult=CommandParser.separateCommand(userInput);
		String commandTypeString = separateResult.poll();
		if (commandTypeString==null || commandTypeString.equals("")){
			return MESSAGE_COMMAND_ERROR;
		}else{
			CommandParser.CommandType commandType = CommandParser.determineCommandType(commandTypeString);
			switch (commandType){
			case LIST:
				return list(separateResult.poll());
			case UPDATE:
				return update(separateResult);
			case EXIT:
				return "EXIT";
			case ADD:
				return add(separateResult);
			case DELETE:
				return delete(separateResult.poll());
			case SHOWDETAIL:
				return show(separateResult.poll());
			case UNDO:
				return undo(separateResult.poll());
			case REDO:
				return redo(separateResult.poll());
			case INVALID:
			default:
				return MESSAGE_COMMAND_ERROR;
			}
		}
	}
	
	private String add(Queue<String> parameterList){
		String result;
		try{
			Map<String, String> parameterMap = CommandParser.separateParameters(parameterList);
			String title = CommandParser.getTitle(parameterMap);
			if (title==null || title.equals("")){
				throw new CEOException(CEOException.NO_TITLE);
			}
			String description = CommandParser.getDescription(parameterMap);
			String location = CommandParser.getLocation(parameterMap);
			String timeString = CommandParser.getTimeString(parameterMap);
			String recurString = CommandParser.getRecurString(parameterMap);
			Date[] time = CommandParser.getTime(timeString);
			executor.addTask(title, description, location, time[0], time[1], CommandParser.stringToRecur(recurString));
			result = MESSAGE_ADD;
		}catch (CEOException e){
			result = MESSAGE_ADD_ERROR;
		}
		return result;
	}
	
	private String list(String parameter) {
		CommandParser.TaskType taskType = CommandParser.determineTaskType(parameter);
		try{
			switch (taskType){
			case ALL:
				return ResponseParser.parseAllListResponse(executor.getAllList());
			case FLOATING:
				return ResponseParser.parseFloatingListResponse(executor.getFloatingList());
			case DEADLINE:
				return ResponseParser.parseDeadlineListResponse(executor.getDeadlineList());
			case PERIODIC:
				return ResponseParser.parsePeriodicListResponse(executor.getPeriodicList());
			case INVALID:
			default:
				print(String.format(MESSAGE_INVALID_TASKTYPE_FORMAT, parameter));
				return ResponseParser.parseAllListResponse(executor.getAllList());
			}
		} catch (CEOException e){
			return CEOException.READ_ERROR;
		}
	}
	
	private String show(String parameters) {
		int taskID=CommandParser.parseIntegerParameter(parameters);
		String response;
		try {
			response=ResponseParser.parseShowDetailResponse(executor.showTaskDetail(taskID),taskID);
		} catch (CEOException e) {
			response = String.format(MESSAGE_SHOW_ERROR_FORMAT, parameters);
		}
		return response;
	}

	private String delete(String parameter) {
		int taskID=CommandParser.parseIntegerParameter(parameter);
		String response;
		try {
			executor.deleteTask(taskID);
			response = String.format(MESSAGE_DELETE_FORMAT, parameter);
		} catch (CEOException e) {
			response = String.format(MESSAGE_DELETE_ERROR_FORMAT, parameter);
		}
		return response;
	}

	private String update(Queue<String> parameterList) {
		String result;
		String taskIDString = parameterList.poll();
		int taskID=0;
		try{
			taskID = CommandParser.parseIntegerParameter(taskIDString);
			Map<String, String> parameterMap = CommandParser.separateParameters(parameterList);
			String title = CommandParser.getTitle(parameterMap);
			String description = CommandParser.getDescription(parameterMap);
			String location = CommandParser.getLocation(parameterMap);
			String completeString = CommandParser.getComplete(parameterMap);
			String timeString = CommandParser.getTimeString(parameterMap);
			String recurString = CommandParser.getRecurString(parameterMap);
			Date[] time = CommandParser.getTime(timeString);
			Recur recur = CommandParser.stringToRecur(recurString);
			boolean complete = CommandParser.parseComplete(completeString);
			if (title==null && description==null && location==null && completeString==null && timeString==null && recurString==null){
				throw new CEOException(CEOException.LESS_THAN_ONE_PARA);
			}else{
				executor.updateTask(taskID, title, description, location, complete, completeString != null, time, timeString != null, recur, recurString != null);
			}
			result = String.format(MESSAGE_UPDATE_FORMAT, taskIDString);
		}catch (CEOException e){
			result = String.format(MESSAGE_UPDATE_ERROR_FORMAT, taskIDString);
		}
		return result;
	}
	
	private String undo(String parameter) {
		int count = CommandParser.parseIntegerParameter(parameter);
		int result = 0;
		try {
			result = executor.undoTasks(count);
		} catch (CEOException e) {
			e.printStackTrace();
		}
		return String.format(MESSAGE_UNDO_FORMAT, result);
	}
	
	private String redo(String parameter) {
		int count = CommandParser.parseIntegerParameter(parameter);
		int result = 0;
		try {
			result = executor.redoTasks(count);
		} catch (CEOException e) {
			e.printStackTrace();
		}
		return String.format(MESSAGE_UNDO_FORMAT, result);
	}
	
	private static void print(String feedback) {
		if (feedback != null){
			System.out.println(feedback);
		}
	}
	
	private static void printPrompt(String prompt){
		if (prompt != null){
			System.out.print(prompt);
		}
	}
	
	private void alertTask() {
		try {
			ArrayList<DeadlineTask> deadlineList = executor.getDeadlineList();
			print(ResponseParser.alertDeadline(deadlineList));
			ArrayList<PeriodicTask> periodicList = executor.getPeriodicList();
			print(ResponseParser.alertPeriodic(periodicList));
		} catch (CEOException e) {
			e.printStackTrace();
		}
	}
	
	private void updateTimeFromRecur() {
		try {
			ArrayList<PeriodicTask> periodicList = executor.getPeriodicList();
			for (PeriodicTask task:periodicList){
				executor.updateTimeFromRecur(task);
			}
		} catch (CEOException e) {
			e.printStackTrace();
		}
		
	}
}
