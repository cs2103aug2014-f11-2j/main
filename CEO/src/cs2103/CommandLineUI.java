package cs2103;

import java.util.Map;
import java.util.Queue;
import java.util.Scanner;

public class CommandLineUI {
	private static final String MESSAGE_WELCOME = "Welcome to the CEO. CEO is ready for use.";
	private static final String MESSAGE_EXIT = "You have exited CEO. Hope to see you again.";
	private static final String MESSAGE_USER_PROMPT = "Command me please: ";
	private static final String MESSAGE_COMMAND_ERROR = "Your input command is invalid, please check your command and try again";
	private static final String MESSAGE_INVALID_TASKID = "Your input taskID is invalid, please check your command and try again";
	private static final String MESSAGE_INVALID_TASKTYPE = "Your input TaskType is invalid, corrected to ALL";
	private static final String MESSAGE_UNKNOWN_ERROR = "An Unknown Error occurred";
	private static final String MESSAGE_DELETE_FORMAT = "You have deleted task %1$d";
	private static final String MESSAGE_ADD = "You have added a new task.";
	private static final String MESSAGE_ADD_ERROR = "Failed to add new task";
	private static final String MESSAGE_UPDATE_FORMAT = "You have updated task %1$d";
	private static final String MESSAGE_UPDATE_ERROR_FORMAT = "Failed to update task %1$d";
	
	public enum CommandType {
		ADD, LIST, SHOWDETAIL, DELETE, UPDATE, EXIT, INVALID;
	}
	public enum TaskType {
		ALL, FLOATING, DEADLINE, PERIODIC, INVALID;
	}
	private final CommandExecutor commandExecutor;
	private Scanner scanner = new Scanner(System.in);
	
	public CommandLineUI(String configFile){
		this.commandExecutor = new CommandExecutor(configFile);
		
	}
	
	public static void main(String[] args){
		CommandLineUI main;
		if (args.length > 1){
			System.err.println("Incorrect Arguement");
			main = new CommandLineUI(args[0]);
		}else if (args.length == 1){
			main = new CommandLineUI(args[0]);
		}else{
			main = new CommandLineUI("default.xml");
		}
		main.userLoop();
		//TODO call the function prompting user interface
	}
	
	private static void printWelcomeMessage() {
		System.out.println(MESSAGE_WELCOME);
	}
	
	public void userLoop() {
		printWelcomeMessage();
		String feedback;
		while (true) {
			printUserPrompt();
			feedback=takeUserInput();
			if (feedback.equalsIgnoreCase("EXIT")){
				printFeedback(MESSAGE_EXIT);
				break;
			}
			printFeedback(feedback);
		}
	}
	
	private static void printUserPrompt() {
		System.out.print(MESSAGE_USER_PROMPT);
	}
	
	private String takeUserInput() {
		String userInput = scanner.nextLine();
		Queue<String> seperateResult=CommandParser.seperateCommand(userInput);
		String commandTypeString = seperateResult.poll();
		if (commandTypeString==null || commandTypeString.equals("")){
			return MESSAGE_COMMAND_ERROR;
		}else{
			CommandType commandType = CommandParser.determineCommandType(commandTypeString);
			switch (commandType){
			case LIST:
				return list(seperateResult.poll());
			case UPDATE:
				return update(seperateResult);
			case EXIT:
				return "EXIT";
			case ADD:
				return add(seperateResult);
			case DELETE:
				return delete(seperateResult.poll());
			case SHOWDETAIL:
				return show(seperateResult.poll());
			case INVALID:
			default:
				return MESSAGE_COMMAND_ERROR;
			}
		}
	}	
	
	private String show(String parameters) {
		int taskID=CommandParser.parseIntegerParameter(parameters);
		String response;
		try {
			response=ResponseParser.parseShowDetailResponse(commandExecutor.showTaskDetail(taskID),taskID);
		} catch (CEOException e) {
			if(e.getMessage().equals("Invalid TaskID")){
				response=MESSAGE_INVALID_TASKID;
			}else{
				response=MESSAGE_UNKNOWN_ERROR;
			}
		}
		return response;
	}

	private String delete(String parameter) {
		int taskID=CommandParser.parseIntegerParameter(parameter);
		String response;
		try {
			commandExecutor.deleteTask(taskID);
			response = String.format(MESSAGE_DELETE_FORMAT, taskID);
		} catch (CEOException e) {
			if(e.getMessage().equals("Invalid TaskID")){
				response=MESSAGE_INVALID_TASKID;
			}else{
				response=MESSAGE_UNKNOWN_ERROR;
			}
		}
		return response;
	}

	private String update(Queue<String> parameterList) {
		String result;
		int taskID=0;
		try{
			taskID = CommandParser.parseIntegerParameter(parameterList.poll());
			Map<String, String> parameterMap = CommandParser.seperateParameters(parameterList);
			String title = CommandParser.getTitle(parameterMap);
			String description = CommandParser.getDescription(parameterMap);
			String location = CommandParser.getLocation(parameterMap);
			String complete = CommandParser.getComplete(parameterMap);
			String timeString = CommandParser.getTimeString(parameterMap);
			if (timeString==null){
				commandExecutor.updateTask(taskID, title, description, location, complete, null, null);
			}else{
				String[] time = CommandParser.getTime(timeString);
				if (time[0]==null && time[1]==null){
					commandExecutor.updateTask(taskID, title, description, location, complete, "", "");
				}else if(time[1]==null){
					commandExecutor.updateTask(taskID, title, description, location, complete, time[0], "");
				}else{
					commandExecutor.updateTask(taskID, title, description, location, complete, time[0], time[1]);
				}
			}
			result = String.format(MESSAGE_UPDATE_FORMAT, taskID);
		}catch (CEOException e){
			result = String.format(MESSAGE_UPDATE_ERROR_FORMAT, taskID);
			//e.printStackTrace();
		}
		return result;
	}

	private String list(String parameter) {
		TaskType taskType = CommandParser.determineTaskType(parameter);
		switch (taskType){
		case ALL:
			return ResponseParser.parseListResponse(commandExecutor.listTask(), "ALL");
		case FLOATING:
			return ResponseParser.parseListResponse(commandExecutor.listTask("FLOATING"), "FLOATING");
		case DEADLINE:
			return ResponseParser.parseListResponse(commandExecutor.listTask("DEADLINE"), "DEADLINE");
		case PERIODIC:
			return ResponseParser.parseListResponse(commandExecutor.listTask("PERIODIC"), "PERIODIC");
		case INVALID:
		default:
			printFeedback(MESSAGE_INVALID_TASKTYPE);
			return ResponseParser.parseListResponse(commandExecutor.listTask(), "ALL");
		}
	}

	private String add(Queue<String> parameterList){
		String result;
		try{
			Map<String, String> parameterMap = CommandParser.seperateParameters(parameterList);
			String title = CommandParser.getTitle(parameterMap);
			if (title==null || title.equals("")){
				throw new CEOException("No title error");
			}
			String description = CommandParser.getDescription(parameterMap);
			String location = CommandParser.getLocation(parameterMap);
			String[] time = CommandParser.getTime(CommandParser.getTimeString(parameterMap));
			commandExecutor.addTask(title, description, location, time[0], time[1]);
			result = MESSAGE_ADD;
		}catch (CEOException e){
			result = MESSAGE_ADD_ERROR;
		}
		return result;
	}
	
	private static void printFeedback(String feedback) {
		System.out.println(feedback);
	}
}
