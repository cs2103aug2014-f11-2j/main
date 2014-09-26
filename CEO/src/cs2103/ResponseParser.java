package cs2103;

import java.util.ArrayList;

public class ResponseParser {
	//All the methods here should be static
	//Taking in various response type and convert to Strings and to be displayed in CommandLineUI
	
	private static final String MESSAGE_ADD = "You have added %1$s.";
	private static final String MESSAGE_DELETE = "You have deleted %1$s.";
	private static final String MESSAGE_SHOWDETAIL = "The details for %1$s - %2$s.";
	private static final String MESSAGE_UPDATE = "%1$s has been updated.";
	
	private static final String FORMAT_LINE_DISPLAY = "%1$s. %2$s\n";
	
	private enum ResponseTypeDummy {
		ADD, LIST, SHOWDETAIL, DELETE, UPDATE
	}
	
	private static String parseResponse(ResponseTypeDummy type) {
		return null;
	}
	
	private static String parserResponse(ResponseTypeDummy type, Task task) {
		if (type == ResponseTypeDummy.ADD) {
			return String.format(MESSAGE_ADD, task.getTitle());
		} else if (type == ResponseTypeDummy.DELETE) {
			return String.format(MESSAGE_DELETE, task.getTitle());
		} else if (type == ResponseTypeDummy.SHOWDETAIL) {
			return String.format(MESSAGE_SHOWDETAIL, task.getTitle(), task.getDescription());
		} else if (type == ResponseTypeDummy.UPDATE) {
			return String.format(MESSAGE_UPDATE, task.getTitle());
		} else {
			return null; 
		}
	}
	
	private static String parserResponse(ResponseTypeDummy type, ArrayList<Task> tasks) {
		if (type == ResponseTypeDummy.LIST) {
			String toDisplay = "";
			int lineNum = 1;
			for (Task t : tasks) {
				toDisplay += String.format(FORMAT_LINE_DISPLAY, lineNum, t.getTitle());
				lineNum++;
			}
			return toDisplay;
		} else {
			return null;
		}
	}
	
}
