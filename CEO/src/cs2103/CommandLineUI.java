package cs2103;

public class CommandLineUI {
	private final CommandParser commandParser;
	public CommandLineUI(String configFile){
		this.commandParser=new CommandParser(configFile);
	}
	public static void main(String[] args){
		CommandLineUI main;
		if (args.length > 1){
			System.err.println("Incorrect Arguement");
		}else if (args.length==1){
			main = new CommandLineUI(args[0]);
		}else{
			main = new CommandLineUI("default.xml");
		}
		//TODO call the function prompting user interface
	}
}
