package cs2103;

import java.util.Scanner;
import java.util.Stack;

import cs2103.command.*;
import cs2103.exception.FatalException;
import cs2103.exception.HandledException;
import cs2103.parameters.CommandType;
import cs2103.parameters.Option;
import cs2103.util.CommonUtil;

public class CommandLineUI {
	private static final String MESSAGE_WELCOME_FORMAT = "Welcome to the CEO. %1$s";
	private static final String MESSAGE_SYNC_ENABLED = "Google Sync is enabled";
	private static final String MESSAGE_SYNC_DISABLED = "Google Sync is disabled";
	private static final String MESSAGE_TEST_MODE = "You are now in test mode";
	private static final String MESSAGE_EXIT = "You have exited CEO. Hope to see you again.";
	private static final String MESSAGE_USER_PROMPT = "Command me please: ";
	private static final String MESSAGE_SYNC_PROMPT = "Do you want to enable google sync? y/n:";
	private static final String MESSAGE_COMMAND_ERROR = "Your input command is invalid, please check your command and try again";
	private static final String MESSAGE_FATAL_ERR = "A fatal error has occurred, program will now exit. Check log for detail";
	private static final String MESSAGE_INITIALIZATION_ERROR = "Failed to initialize CEO, program will now exit";
	private static final String MESSAGE_UNDO_FORMAT = "Successfully undo %1$d tasks";
	private static final String MESSAGE_REDO_FORMAT = "Successfully redo %1$d tasks";
	
	private static CommandLineUI commandLine;
	private TaskList taskList;
	private Stack<InfluentialCommand> undoStack;
	private Stack<InfluentialCommand> redoStack;
	private Scanner scanner = new Scanner(System.in);
	
	private CommandLineUI(Option option) throws HandledException, FatalException{
		undoStack = new Stack<InfluentialCommand>();
		redoStack = new Stack<InfluentialCommand>();
		option = this.verifyOption(option);
		this.taskList = TaskList.getInstance(option);
		switch(option.getValue()){
		default:
		case SYNC:
		case DEFAULT:
			CommonUtil.print(String.format(MESSAGE_WELCOME_FORMAT, MESSAGE_SYNC_ENABLED));
			break;
		case NOSYNC:
			CommonUtil.print(String.format(MESSAGE_WELCOME_FORMAT, MESSAGE_SYNC_DISABLED));
			break;
		case TEST:
			CommonUtil.print(String.format(MESSAGE_WELCOME_FORMAT, MESSAGE_TEST_MODE));
			break;
		}
	}
	
	public static CommandLineUI getInstance(Option option) throws HandledException, FatalException{
		if (commandLine == null){
			commandLine = new CommandLineUI(option);
			assert(commandLine.taskList.getAllList() != null);
		}
		return commandLine;
	}
	
	public static void main(String[] args){
		CommandLineUI main;
		try{
			main = CommandLineUI.getInstance(Option.parse(args));
			main.userLoop();
		} catch (HandledException | FatalException e){
			System.err.println(MESSAGE_INITIALIZATION_ERROR);
		}
	}
	
	private void userLoop() throws HandledException, FatalException {
		CommonUtil.print(new UpdateTimeFromRecur().execute());
		CommonUtil.print(new Alert().execute());
		while (true) {
			CommonUtil.printPrompt(MESSAGE_USER_PROMPT);
			String command = scanner.nextLine();
			CommonUtil.clearConsole();
			if (command != null && !command.isEmpty()){
				String feedback = processUserInput(command);
				if (feedback != null && feedback.equalsIgnoreCase("EXIT")){
					CommonUtil.print(MESSAGE_EXIT);
					break;
				}
				CommonUtil.print(feedback);
			}
		}
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
			case RESTORE:
				commandObject = new Restore(command[1]);
				break;
			case SYNC:
				commandObject = new Sync();
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
			return e.getErrorMsg();
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
		while(!this.undoStack.isEmpty() && result < steps){
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
		while(!this.undoStack.isEmpty() && result < steps){
			InfluentialCommand undoCommand = redoStack.pop().redo();
			if (undoCommand != null){
				result++;
				this.undoStack.push(undoCommand);
			}
		}
		return result;
	}
	
	private static void printErrorAndExit(){
		System.err.print(MESSAGE_FATAL_ERR);
		System.exit(-1);
	}
	
	private Option verifyOption(Option option) throws HandledException{
		if (option == null || option.getValue().equals(Option.Value.DEFAULT)){
			if (CommonUtil.checkSyncSupport()){
				CommonUtil.printPrompt(MESSAGE_SYNC_PROMPT);
				String answer = null;
				while(true){
					answer = this.scanner.nextLine();
					if (answer != null){
						if (answer.equalsIgnoreCase("y")){
							return new Option(Option.Value.SYNC);
						} else if (answer.equalsIgnoreCase("n")){
							return new Option(Option.Value.NOSYNC);
						}
					}
				}
			} else {
				return new Option(Option.Value.NOSYNC);
			}
		} else {
			return option;
		}
	}
	
	public String testCommand(String testCommandInput){
		return processUserInput(testCommandInput);
	}
}
