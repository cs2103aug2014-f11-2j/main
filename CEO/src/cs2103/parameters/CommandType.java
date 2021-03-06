//@author A0112673L
package cs2103.parameters;

public class CommandType implements Parameter {
	public static final String type = "COMMANDTYPE";
	private final Value value;
	
	public static enum Value {
		ADD, LIST, SHOW, DELETE, UPDATE, EXIT, UNDO, REDO, HELP, SEARCH, QUICK, ALERT, MARK, RESTORE, SYNC, INVALID;
	}
	
	/**
	 * @param value
	 */
	public CommandType(Value value) {
		this.value = value;
	}
	
	/**
	 * @return Value enumerator of CommandType
	 */
	public Value getValue() {
		return this.value;
	}
	
	@Override
	public String getType() {
		return type;
	}
	
	/**
	 * @param commandTypeString
	 * @return CommandType object of String commandTypeString
	 */
	public static CommandType parse(String commandTypeString) {
		return new CommandType(parseTaskType(commandTypeString));
	}
	
	/**
	 * @param commandTypeString
	 * @return Value enumerator of String commandTypeString
	 */
	private static Value parseTaskType(String commandTypeString) {
		if (commandTypeString == null) {
			return Value.INVALID;
		}
		if (commandTypeString.equalsIgnoreCase("list")) {
			return Value.LIST;
		} else if (commandTypeString.equalsIgnoreCase("update") || commandTypeString.equalsIgnoreCase("modify")) {
			return Value.UPDATE;
		} else if (commandTypeString.equalsIgnoreCase("exit") || commandTypeString.equalsIgnoreCase("bye")) {
			return Value.EXIT;
		} else if (commandTypeString.equalsIgnoreCase("add") || commandTypeString.equalsIgnoreCase("new") || commandTypeString.equalsIgnoreCase("create")) {
			return Value.ADD;
		} else if (commandTypeString.equalsIgnoreCase("delete") || commandTypeString.equalsIgnoreCase("remove") || commandTypeString.equalsIgnoreCase("del")) {
			return Value.DELETE;
		} else if (commandTypeString.equalsIgnoreCase("show") || commandTypeString.equalsIgnoreCase("detail")) {
			return Value.SHOW;
		} else if (commandTypeString.equalsIgnoreCase("undo")) {
			return Value.UNDO;
		} else if (commandTypeString.equalsIgnoreCase("redo")) {
			return Value.REDO;
		} else if (commandTypeString.equalsIgnoreCase("help")) {
			return Value.HELP;
		} else if (commandTypeString.equalsIgnoreCase("search") || commandTypeString.equalsIgnoreCase("find")) {
			return Value.SEARCH;
		} else if (commandTypeString.equalsIgnoreCase("mark") || commandTypeString.equalsIgnoreCase("complete") || commandTypeString.equalsIgnoreCase("finish")) {
			return Value.MARK;
		} else if (commandTypeString.equalsIgnoreCase("alert") || commandTypeString.equalsIgnoreCase("remind")) {
			return Value.ALERT;
		} else if (commandTypeString.equalsIgnoreCase("recover") || commandTypeString.equalsIgnoreCase("restore")) {
			return Value.RESTORE;
		} else if (commandTypeString.equalsIgnoreCase("google") || commandTypeString.equalsIgnoreCase("sync")) {
			return Value.SYNC;
		} else {
			return Value.INVALID;
		}
	}

}
