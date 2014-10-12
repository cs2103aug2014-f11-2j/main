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
	private static final String MESSAGE_DELETE_FORMAT = "You have succesfully deleted task with ID %1$s";
	private static final String MESSAGE_DELETE_ERROR_FORMAT = "Failed to delete task with ID %1$s";
	private static final String MESSAGE_ADD = "You have succesfully added a new task.\n %1$s";
	private static final String MESSAGE_ADD_ERROR = "Failed to add new task";
	private static final String MESSAGE_UPDATE_FORMAT = "You have updated task with ID %1$s";
	private static final String MESSAGE_UPDATE_ERROR_FORMAT = "Failed to update task with ID %1$s";
	private static final String MESSAGE_SHOW_ERROR_FORMAT = "Failed to show task with ID %1$s";
	private static final String MESSAGE_UNDO_FORMAT = "Successfully undo %1$d tasks";
	private static final String MESSAGE_REDO_FORMAT = "Successfully redo %1$d tasks";
	private static final String MESSAGE_UPDATE_RECUR_TIME_FORMAT = "Successfully updated %1$d recurring tasks";
	
	private final CommandExecutor executor;
	private Scanner scanner = new Scanner(System.in);
	private static CommandLineUI commandLine;
	private static ResponseParser responseParser = ResponseParser.getInstance();
	
	private CommandLineUI(String dataFile) throws CEOException{
		this.executor = CommandExecutor.getInstance(dataFile);
		print(String.format(MESSAGE_WELCOME_FORMAT, dataFile));
	}
	
	public static CommandLineUI getInstance(String dataFile) throws CEOException{
		if (commandLine == null){
			commandLine = new CommandLineUI(dataFile);
		}
		return commandLine;
	}
	
	public static void main(String[] args){
		CommandLineUI main;
		try{
			if (args.length > 1){
				System.err.println("Incorrect Argument");
				main = CommandLineUI.getInstance(args[0]);
			}else if (args.length == 1){
				main = CommandLineUI.getInstance(args[0]);
			}else{
				main = CommandLineUI.getInstance("default.ics");
			}
		main.userLoop();
		} catch (CEOException e){
			e.printStackTrace();
		}
	}
	
	private void userLoop() {
		updateTimeFromRecur();
		alertTask();
		while (true) {
			printPrompt(MESSAGE_USER_PROMPT);
			String command = takeUserInput();
			if (command != null){
				String feedback=processUserInput(command);
				if (feedback.equalsIgnoreCase("EXIT")){
					print(MESSAGE_EXIT);
					break;
				}
				print(feedback);
			}
		}
	}
	
	private String takeUserInput(){
		String userInput = scanner.nextLine();
		if (userInput.equals("")){
			userInput = null;
		}
		return userInput;
	}
	
	private String processUserInput(String userInput) {
		try {
			Queue<String> separateResult = CommandParser.separateCommand(userInput);
			String commandTypeString = separateResult.poll();
			if (commandTypeString==null || commandTypeString.equals("")){
				return MESSAGE_COMMAND_ERROR;
			} else {
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
				case HELP:
					return getHelp(separateResult.poll());
				case INVALID:
				default:
					return MESSAGE_COMMAND_ERROR;
				}
			}
		} catch (CEOException e) {
			return MESSAGE_COMMAND_ERROR;
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
			result = ResponseParser.parseAddResponse(title,description,location,time[0],time[1], 
					CommandParser.stringToRecur(recurString));
		}catch (CEOException e){
			result = ResponseParser.parseAddResponseError();
		}
		return result;
	}
	
	private String list(String parameter) {
		String response;
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
				response = ResponseParser.parseAllListResponse(executor.getAllList());
			}
		} catch (CEOException e){
			return CEOException.READ_ERROR;
		}
		return response;
	}
	
	private String show(String parameter) {
		String response;
		try {
			int taskID = CommandParser.parseIntegerParameter(parameter);
			response = ResponseParser.parseShowDetailResponse(executor.showTaskDetail(taskID),taskID);
		} catch (CEOException e) {
			response = ResponseParser.parseShowDetailResponseError(parameter);
		}
		return response;
	}

	private String delete(String parameter) {
		String response;
		try {
			int taskID=CommandParser.parseIntegerParameter(parameter);
			executor.deleteTask(taskID);
			response = ResponseParser.parseDeleteResponse(taskID);
		} catch (CEOException e) {
			response = ResponseParser.parseDeleteResponseError(parameter);
		}
		return response;
	}

	private String update(Queue<String> parameterList) {
		String result;
		String taskIDString = parameterList.poll();
		int taskID = 0;
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
			if (title == null && description == null && location == null && completeString == null && timeString == null && recurString == null){
				throw new CEOException(CEOException.LESS_THAN_ONE_PARA);
			}else{
				executor.updateTask(taskID, title, description, location, complete, completeString != null, time, timeString != null, recur, recurString != null);
			}
			result = ResponseParser.parseUpdateResponse(taskID);
		}catch (CEOException e){
			result = ResponseParser.parseUpdateResponseError(taskIDString);
		}
		return result;
	}
	
	private String undo(String parameter) {
		int result = 0;
		try {
			int count = CommandParser.parseIntegerParameter(parameter);
			result = executor.undoTasks(count);
		} catch (CEOException e) {
			e.printStackTrace();
		}
		return ResponseParser.parseUndoResponse(result);
	}
	
	private String redo(String parameter) {
		int result = 0;
		try {
			int count = CommandParser.parseIntegerParameter(parameter);
			result = executor.redoTasks(count);
		} catch (CEOException e) {
			e.printStackTrace();
		}
		return ResponseParser.parseRedoResponse(result);
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
		int count = 0;
		try {
			ArrayList<PeriodicTask> periodicList = executor.getPeriodicList();
			for (PeriodicTask task:periodicList){
				if (executor.updateTimeFromRecur(task)){
					count++;
				}
			}
		} catch (CEOException e) {
			e.printStackTrace();
		}
		print(String.format(MESSAGE_UPDATE_RECUR_TIME_FORMAT, count));
	}
	
	private String getHelp(String parameter){
		CommandParser.CommandType commandType = CommandParser.determineCommandType(parameter);
		switch (commandType){
		case LIST:
			return ResponseParser.HELP_LIST;
		case UPDATE:
			return ResponseParser.HELP_UPDATE;
		case ADD:
			return ResponseParser.HELP_ADD;
		case DELETE:
			return ResponseParser.HELP_DELETE;
		case SHOWDETAIL:
			return ResponseParser.HELP_SHOW;
		case UNDO:
			return ResponseParser.HELP_UNDO;
		case REDO:
			return ResponseParser.HELP_REDO;
		case INVALID:
		default:
			return ResponseParser.HELP_DEFAULT;
		}
	}
}
