package cs2103.command;

import java.util.ArrayList;

import cs2103.FatalException;
import cs2103.HandledException;
import cs2103.PeriodicTask;
import cs2103.StorageEngine;
import cs2103.Task;

public class UpdateTimeFromRecur extends InfluentialCommand {
	private static final String MESSAGE_UPDATE_RECUR_TIME_FORMAT = "Successfully updated %1$d recurring tasks";
	
	@Override
	public String execute() throws HandledException, FatalException {
		int count = 0;
		ArrayList<PeriodicTask> periodicList = getPeriodicList();
		for (PeriodicTask task:periodicList){
			Task newTask = task.updateTimeFromRecur();
			if (newTask != null){
				count++;
				StorageEngine.getInstance().updateTask(newTask);
			}
		}
		return String.format(MESSAGE_UPDATE_RECUR_TIME_FORMAT, count);
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
