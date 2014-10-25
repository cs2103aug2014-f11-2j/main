package cs2103.command;

import cs2103.CommonUtil;
import cs2103.TaskList;
import cs2103.exception.FatalException;
import cs2103.exception.HandledException;
import cs2103.parameters.TaskID;
import cs2103.task.Task;

public class Restore extends InfluentialCommand {
	private static final String MESSAGE_RESTORE = "You have successfully restored a task with ID %1$d\n";
	public Restore(String command) throws HandledException {
		this.parameterList.addParameter(TaskID.parse(command));
	}

	@Override
	public InfluentialCommand undo() throws HandledException, FatalException {
		if (this.undoBackup == null){
			return null;
		} else {
			TaskList.getInstance().restoreTask(this.undoBackup);
			return this;
		}
	}
	
	@Override
	public InfluentialCommand redo() throws HandledException, FatalException {
		if (this.redoBackup == null){
			return null;
		} else {
			TaskList.getInstance().deleteTask(this.redoBackup);
			return this;
		}
	}

	@Override
	public String execute() throws HandledException, FatalException {
		CommonUtil.checkNull(this.parameterList.getTaskID(), HandledException.ExceptionType.INVALID_CMD);
		TaskList taskList = TaskList.getInstance();
		Task task = taskList.getTaskByID(parameterList.getTaskID().getValue());
		this.undoBackup = task;
		taskList.restoreTask(task);
		task = taskList.getTaskByTask(task);
		this.redoBackup = task;
		return this.formatReturnString(String.format(MESSAGE_RESTORE, parameterList.getTaskID().getValue()), task);
	}
}
