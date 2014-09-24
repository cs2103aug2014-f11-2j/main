package cs2103;

class CommandParser {
	private final CommandExecutor commandExecutor;
	public CommandParser(String configFile){
		this.commandExecutor = new CommandExecutor(configFile);
	}
}
