package cs2103;

import java.util.Scanner;

public class CommandLineUI {
	private static final String MESSAGE_WELCOME = "Welcome to the CEO. CEO is ready for use.";
	private static final String MESSAGE_EXIT = "You have exited CEO. Hope to see you again.";
	private static final String MESSAGE_USER_PROMPT = "What would you like to do today?";
	
	private final CommandParser commandParser;
	private static Scanner scanner = new Scanner(System.in);
	
	public CommandLineUI(String configFile){
		this.commandParser=new CommandParser(configFile);
		printWelcomeMessage();
		userLoop();
	}
	
	public static void main(String[] args){
		CommandLineUI main;
		if (args.length > 1){
			System.err.println("Incorrect Arguement");
		}else if (args.length == 1){
			main = new CommandLineUI(args[0]);
		}else{
			main = new CommandLineUI("default.xml");
		}
		//TODO call the function prompting user interface
	}
	
	private static void printWelcomeMessage() {
		System.out.println(MESSAGE_WELCOME);
	}
	
	private static void userLoop() {
		while (true) {
			printUserPrompt();
			takeUserInput();
			printUserOutput();
		}
	}
	
	private static void printUserPrompt() {
		System.out.println(MESSAGE_USER_PROMPT);
	}
	
	private static void takeUserInput() {
		String userInput = scanner.nextLine();
	}	
	
	private static void printUserOutput() {
		
	}
}
