package cs2103.command;

import cs2103.CommonUtil;
import cs2103.TaskList;
import cs2103.exception.FatalException;
import cs2103.exception.HandledException;
import cs2103.parameters.TaskID;
import cs2103.task.PeriodicTask;
import cs2103.task.Task;

public class Mark extends InfluentialCommand {
	private static final String MESSAGE_MARK_FORMAT = "Successfully marked %1$d as completed";
	private static final String MESSAGE_MARK_FAILED = "The task you specified does not contain status information";
	
	public Mark(String command) throws HandledException{
		this.parameterList.addParameter(TaskID.parse(command));
	}

	@Override
	public String execute() throws HandledException, FatalException {
		CommonUtil.checkNull(this.parameterList.getTaskID(), HandledException.ExceptionType.INVALID_CMD);
		Task task = TaskList.getInstance().getTaskByID(parameterList.getTaskID().getValue());
		if (task instanceof PeriodicTask){
			return MESSAGE_MARK_FAILED;
		} else {
			Task newTask = cloneTask(task);
			newTask.updateComplete(true);
			TaskList.getInstance().updateTask(newTask);
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
	

	@Override
	public InfluentialCommand undo() throws HandledException, FatalException {
		if (this.undoBackup == null){
			return null;
		} else {
			TaskList.getInstance().updateTask(this.undoBackup);
			return this;
		}
	}
	
	@Override
	public InfluentialCommand redo() throws HandledException, FatalException {
		if (this.redoBackup == null){
			return null;
		} else {
			TaskList.getInstance().updateTask(this.redoBackup);
			return this;
		}
	}
}
