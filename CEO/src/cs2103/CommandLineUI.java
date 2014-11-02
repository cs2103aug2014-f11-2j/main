package cs2103;

import java.util.Scanner;
import java.util.Stack;

import org.fusesource.jansi.Ansi;

import cs2103.command.*;
import cs2103.exception.FatalException;
import cs2103.exception.HandledException;
import cs2103.parameters.CommandType;
import cs2103.parameters.Option;
import cs2103.storage.TaskList;
import cs2103.util.CommonUtil;
import static org.fusesource.jansi.Ansi.*;
import static org.fusesource.jansi.Ansi.Color.*;

public class CommandLineUI {
	private static final String MESSAGE_WELCOME_FORMAT = "Welcome to the CEO. %1$s";
	private static final String MESSAGE_SYNC_ENABLED = "Google Sync is enabled";
	private static final String MESSAGE_SYNC_DISABLED = "Google Sync is disabled";
	private static final String MESSAGE_TEST_MODE = "You are now in test mode";
	private static final String MESSAGE_EXIT = "You have exited CEO. Hope to see you again.\n";
	private static final String MESSAGE_USER_PROMPT = "Command me please: ";
	private static final String MESSAGE_SYNC_PROMPT = "Do you want to enable google sync? y/n:";
	private static final String MESSAGE_COMMAND_ERROR = "Your input command is invalid, please check your command and try again\n";
	private static final String MESSAGE_FATAL_ERR = "A fatal error has occurred, program will now exit. Check log for detail\n";
	private static final String MESSAGE_INITIALIZATION_ERROR = "Failed to initialize CEO, program will now exit\n";
	private static final String MESSAGE_UNDO_FORMAT = "Successfully undo %1$d operations\n";
	private static final String MESSAGE_REDO_FORMAT = "Successfully redo %1$d operations\n";
	
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
		Ansi welcomeMsg;
		switch(option.getValue()){
		default:
		case SYNC:
		case DEFAULT:
			welcomeMsg = ansi().fg(GREEN).a(String.format(MESSAGE_WELCOME_FORMAT, MESSAGE_SYNC_ENABLED));
			break;
		case NOSYNC:
			welcomeMsg = ansi().fg(RED).a(String.format(MESSAGE_WELCOME_FORMAT, MESSAGE_SYNC_DISABLED));
			break;
		case TEST:
			welcomeMsg = ansi().a(String.format(MESSAGE_WELCOME_FORMAT, MESSAGE_TEST_MODE));
			break;
		}
		CommonUtil.print(welcomeMsg.a('\n').reset());
		
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
				Ansi feedback = processUserInput(command);
				if (feedback == null){
					CommonUtil.print(MESSAGE_EXIT);
					break;
				} else {
					CommonUtil.print(feedback);
				}
			}
		}
	}
	
	private Ansi processUserInput(String userInput) {
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
				return null;
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
				commandObject = new Sync(command[1]);
				break;
			case INVALID:
			default:
				return ansi().bg(RED).a(MESSAGE_COMMAND_ERROR).reset();
			}
			if (commandObject instanceof InfluentialCommand){
				this.undoStack.push((InfluentialCommand) commandObject);
			}
			return commandObject.execute();
		} catch (HandledException e) {
			return ansi().bg(RED).a(e.getErrorMsg()).a('\n').reset();
		} catch (FatalException e) {
			CommonUtil.printErrMsg(MESSAGE_FATAL_ERR);
			return null;
		}
	}
	
	private Ansi undo(String steps) throws HandledException, FatalException {
		int result;
		if (steps == null || steps.isEmpty()){
			result = executeUndo(1);
		} else {
			result = executeUndo(CommonUtil.parseIntegerParameter(steps));
		}
		return ansi().fg(GREEN).a(String.format(MESSAGE_UNDO_FORMAT, result)).reset();
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
	
	private Ansi redo(String steps) throws HandledException, FatalException {
		int result;
		if (steps == null || steps.isEmpty()){
			result = executeRedo(1);
		} else {
			result = executeRedo(CommonUtil.parseIntegerParameter(steps));
		}
		return ansi().fg(GREEN).a(String.format(MESSAGE_REDO_FORMAT, result)).reset();
	}
	
	private int executeRedo(int steps) throws HandledException, FatalException{
		int result = 0;
		while(!this.redoStack.isEmpty() && result < steps){
			InfluentialCommand redoCommand = redoStack.pop().redo();
			if (redoCommand != null){
				result++;
				this.undoStack.push(redoCommand);
			}
		}
		return result;
	}
	
	private Option verifyOption(Option option) throws HandledException{
		if (option == null || option.getValue().equals(Option.Value.DEFAULT)){
			if (CommonUtil.checkSyncSupport()){
				return askOption();
			} else {
				return new Option(Option.Value.NOSYNC);
			}
		} else {
			return option;
		}
	}
	
	private Option askOption(){
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
	}
	
	public String testCommand(String testCommandInput){
		return processUserInput(testCommandInput).toString();
	}
}
