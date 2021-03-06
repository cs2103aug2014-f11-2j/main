//@author A0112673L
package cs2103.command;

import java.util.ArrayList;

import org.fusesource.jansi.Ansi;

import static org.fusesource.jansi.Ansi.*;
import static org.fusesource.jansi.Ansi.Color.*;
import cs2103.exception.FatalException;
import cs2103.exception.HandledException;
import cs2103.storage.TaskList;
import cs2103.task.PeriodicTask;
import cs2103.task.Task;

public class UpdateTimeFromRecur extends InfluentialCommand {
	private static final String MESSAGE_UPDATE_RECUR_TIME_FORMAT = "Successfully updated %1$d recurring tasks\n";
	
	@Override
	public Ansi execute() throws HandledException, FatalException {
		int count = 0;
		ArrayList<PeriodicTask> periodicList = TaskList.getInstance().getPeriodicList();
		for (PeriodicTask task:periodicList) {
			Task newTask = task.updateTimeFromRecur();
			if (newTask != null) {
				count++;
				TaskList.getInstance().updateTask(newTask);
			}
		}
		return ansi().fg(GREEN).a(String.format(MESSAGE_UPDATE_RECUR_TIME_FORMAT, count)).reset();
	}

	@Override
	public InfluentialCommand undo() {
		//not required for this type of command
		return null;
	}

	@Override
	public InfluentialCommand redo() {
		//not required for this type of command
		return null;
	}

}
