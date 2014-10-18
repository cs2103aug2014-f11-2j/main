package cs2103.command;

import cs2103.CommonUtil;
import cs2103.StorageEngine;
import cs2103.exception.FatalException;
import cs2103.exception.HandledException;
import cs2103.parameters.TaskID;
import cs2103.task.PeriodicTask;
import cs2103.task.Task;

public class Mark extends InfluentialCommand {
	private static final String MESSAGE_MARK_FORMAT = "Successfully marked %1$d as completed";
	private static final String MESSAGE_MARK_FAILED = "The task you specified does not contain status information";
	Task undoBackup;
	Task redoBackup;
	
	public Mark(String command) throws HandledException{
		this.parameterList.addParameter(TaskID.parse(command));
	}
	@Override
	public InfluentialCommand undo() throws HandledException, FatalException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public InfluentialCommand redo() throws HandledException, FatalException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String execute() throws HandledException, FatalException {
		CommonUtil.checkNullParameter(this.parameterList.getTaskID(), HandledException.ExceptionType.INVALID_CMD);
		Task task = getTaskByID(parameterList.getTaskID().getValue());
		if (task instanceof PeriodicTask){
			return MESSAGE_MARK_FAILED;
		} else {
			Task newTask = cloneTask(task);
			newTask.updateComplete(true);
			StorageEngine.getInstance().updateTask(newTask);
			this.undoBackup = task;
			this.redoBackup = newTask;
			return String.format(MESSAGE_MARK_FORMAT, parameterList.getTaskID().getValue());
		}
	}
	
	private static Task cloneTask(Task task) throws HandledException{
		try {
			Task newTask = (Task) task.clone();
			return newTask;
		} catch (CloneNotSupportedException e) {
			throw new HandledException(HandledException.ExceptionType.CLONE_FAILED);
		}
	}
}
