package cs2103;

public class CommandLineUI {
	private static final String MESSAGE_WELCOME = "Welcome to the CEO. CEO is ready for use";
	private final CommandParser commandParser;
	public CommandLineUI(String configFile){
		this.commandParser=new CommandParser(configFile);
		printWelcomeMessage();
		promptUserLoop();
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
	
	private static void promptUserLoop() {
		
	}
	
}
