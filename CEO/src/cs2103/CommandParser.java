package cs2103;

class CommandParser {

	public static String[] seperateCommand(String userInput) {
		String[] commandStr = new String[2];
		int splitterIndex = userInput.indexOf(' ');
		if (splitterIndex==-1){
			commandStr[0]=userInput;
		}else{
			commandStr[0] = userInput.substring(0, splitterIndex);
			commandStr[1] = userInput.substring(splitterIndex).trim();
		}
		return commandStr;
	}
	
	public static CommandLineUI.CommandType determineCommandType(String command) {
		if (command.equalsIgnoreCase("list")){
			return CommandLineUI.CommandType.LIST;
		}else if (command.equals("update")){
			return CommandLineUI.CommandType.UPDATE;
		}else if (command.equals("exit")){
			return CommandLineUI.CommandType.EXIT;
		}else if (command.equals("add")){
			return CommandLineUI.CommandType.ADD;
		}else if (command.equals("delete")){
			return CommandLineUI.CommandType.DELETE;
		}else if (command.equals("show")){
			return CommandLineUI.CommandType.SHOWDETAIL;
		}else{
			return CommandLineUI.CommandType.INVALID;
		}
	}
	
	public static int parseIntegerParameter(String parameters){
		if (parameters==null || parameters.equals("")){
			return -1;
		}else{
			if (parameters.matches("[0-9]+")){
				return Integer.parseInt(parameters);
			}else{
				return -1;
			}
		}
	}
	//Write all methods static
}
