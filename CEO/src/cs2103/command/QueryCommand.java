//@author A0116713M
package cs2103.command;

import java.util.ArrayList;
import cs2103.task.Task;
import org.fusesource.jansi.Ansi;
import static org.fusesource.jansi.Ansi.*;
import static org.fusesource.jansi.Ansi.Color.*;

public abstract class QueryCommand extends Command {
	private static final Ansi MESSAGE_EMPTY_LIST = ansi().bold().fg(RED).a("The task list is empty\n").reset();
	
	/**
	 * @param taskList
	 * @return Ansi formatted result string
	 */
	protected static <T extends Task> Ansi parseListResponse(ArrayList<T> taskList) {
		if (taskList == null || taskList.size() == 0) {
			return MESSAGE_EMPTY_LIST;
		} else {
			Ansi returnString = ansi();
			for (Task task:taskList) {
				returnString.a(task.toSummary()).a('\n');
			}
			return returnString;
		}
	}
}
