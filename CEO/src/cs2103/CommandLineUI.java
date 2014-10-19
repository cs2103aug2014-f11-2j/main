package cs2103;

import java.util.Scanner;
import java.util.Stack;

import cs2103.command.*;
import cs2103.exception.FatalException;
import cs2103.exception.HandledException;
import cs2103.parameters.CommandType;

public class CommandLineUI {
	private static final String MESSAGE_WELCOME_FORMAT = "Welcome to the CEO. %1$s is ready for use.";
	private static final String MESSAGE_EXIT = "You have exited CEO. Hope to see you again.";
	private static final String MESSAGE_USER_PROMPT = "Command me please: ";
	private static final String MESSAGE_COMMAND_ERROR = "Your input command is invalid, please check your command and try again";
	private static final String MESSAGE_FATAL_ERR = "A fatal error has occurred, program will now exit. Check log for detail";
	private static final String MESSAGE_INCORRECT_ARG = "Incorrect Argument\n";
	private static final String MESSAGE_INITIALIZATION_ERROR = "Failed to initialize CEO, program will now exit";
	private static final String MESSAGE_UNDO_FORMAT = "Successfully undo %1$d tasks";
	private static final String MESSAGE_REDO_FORMAT = "Successfully redo %1$d tasks";
	
	private static CommandLineUI commandLine;
	private TaskList taskList;
	private Stack<InfluentialCommand> undoStack;
	private Stack<InfluentialCommand> redoStack;
	private Scanner scanner = new Scanner(System.in);
	
	private CommandLineUI(String dataFile, boolean writeToFile) throws HandledException, FatalException{
		undoStack = new Stack<InfluentialCommand>();
		redoStack = new Stack<InfluentialCommand>();
		this.taskList = TaskList.getInstance(dataFile, writeToFile);
		print(String.format(MESSAGE_WELCOME_FORMAT, dataFile));
	}
	
	private static CommandLineUI getInstance(String dataFile, boolean writeToFile) throws HandledException, FatalException{
		if (commandLine == null){
			commandLine = new CommandLineUI(dataFile, writeToFile);
			assert(commandLine.taskList.getAllList() != null);
		}
		return commandLine;
	}
	
	public static void main(String[] args){
		CommandLineUI main;
		try{
			if (args.length > 1){
				print(MESSAGE_INCORRECT_ARG);
				main = CommandLineUI.getInstance(args[0], true);
			}else if (args.length == 1){
				main = CommandLineUI.getInstance(args[0], true);
			}else{
				main = CommandLineUI.getInstance("default.ics", true);
			}
		main.userLoop();
		} catch (HandledException | FatalException e){
			System.err.println(MESSAGE_INITIALIZATION_ERROR);
		}
	}
	
	private void userLoop() throws HandledException, FatalException {
		print(new UpdateTimeFromRecur().execute());
		print(new Alert().execute());
		while (true) {
			printPrompt(MESSAGE_USER_PROMPT);
			String command = takeUserInput();
			if (command != null && !command.isEmpty()){
				String feedback=processUserInput(command);
				if (feedback != null && !feedback.isEmpty()){
					if (feedback.equalsIgnoreCase("EXIT")){
						print(MESSAGE_EXIT);
						break;
					}
					print(feedback);
				}
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
			assert(userInput != null);
			String command[] = CommonUtil.splitFirstWord(userInput);
			CommandType commandType = CommandType.parse(command[0]);
			Command commandObject;
			switch (commandType.getValue()){
			case LIST:
				commandObject = new List(command[1]);
				break;
			case UPDATE:
				commandObject = new Update(command[1]);
				break;
			case EXIT:
				return "EXIT";
			case ADD:
				commandObject = new Add(command[1]);
				break;
			case DELETE:
				commandObject = new Delete(command[1]);
				break;
			case SHOW:
				commandObject = new Show(command[1]);
				break;
			case UNDO:
				return undo(command[1]);
			case REDO:
				return redo(command[1]);
			case HELP:
				commandObject = new Help(command[1]);
				break;
			case SEARCH:
				commandObject = new Search(command[1]);
				break;
			case ALERT:
				commandObject = new Alert();
				break;
			case MARK:
				commandObject = new Mark(command[1]);
				break;
			case INVALID:
			default:
				return MESSAGE_COMMAND_ERROR;
			}
			if (commandObject instanceof InfluentialCommand){
				this.undoStack.push((InfluentialCommand) commandObject);
			}
			return commandObject.execute();
		} catch (HandledException e) {
			return e.printErrorMsg();
		} catch (FatalException e) {
			printErrorAndExit();
			return null;
		}
	}
	
	private String undo(String steps) throws HandledException, FatalException {
		int result;
		if (steps == null || steps.isEmpty()){
			result = executeUndo(1);
		} else {
			result = executeUndo(CommonUtil.parseIntegerParameter(steps));
		}
		return String.format(MESSAGE_UNDO_FORMAT, result);
	}
	
	private int executeUndo(int steps) throws HandledException, FatalException{
		int result = 0;
		while(result < this.undoStack.size() && result < steps){
			InfluentialCommand undoCommand = undoStack.pop().undo();
			if (undoCommand != null){
				result++;
				this.redoStack.push(undoCommand);
			}
		}
		return result;
	}
	
	private String redo(String steps) throws HandledException, FatalException {
		int result;
		if (steps == null || steps.isEmpty()){
			result = executeRedo(1);
		} else {
			result = executeRedo(CommonUtil.parseIntegerParameter(steps));
		}
		return String.format(MESSAGE_REDO_FORMAT, result);
	}
	
	private int executeRedo(int steps) throws HandledException, FatalException{
		int result = 0;
		while(result < this.redoStack.size() && result < steps){
			InfluentialCommand undoCommand = redoStack.pop().redo();
			if (undoCommand != null){
				result++;
				this.undoStack.push(undoCommand);
			}
		}
		return result;
	}
	
	private static void print(String feedback) {
		if (feedback != null && !feedback.isEmpty()){
			System.out.println(feedback);
		}
	}
	
	private static void printPrompt(String prompt){
		if (prompt != null){
			System.out.print(prompt);
		}
	}
	
	private static void printErrorAndExit(){
		System.err.print(MESSAGE_FATAL_ERR);
		System.exit(-1);
	}
	
	public String testCommand(String testCommandInput){
		return processUserInput(testCommandInput);
	}
}
