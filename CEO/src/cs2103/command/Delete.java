package cs2103.command;

import cs2103.CommonUtil;
import cs2103.TaskList;
import cs2103.exception.FatalException;
import cs2103.exception.HandledException;
import cs2103.parameters.TaskID;
import cs2103.task.Task;

public class Delete extends InfluentialCommand {
	private static final String MESSAGE_DELETE_FORMAT = "You have deleted task with ID %1$d";
	
	public Delete(String command) throws HandledException{
		this.parameterList.addParameter(TaskID.parse(command));
	}
	
	@Override
	public String execute() throws HandledException, FatalException {
		CommonUtil.checkNullParameter(this.parameterList.getTaskID(), HandledException.ExceptionType.INVALID_CMD);
		Task deletingTask = TaskList.getInstance().getTaskByID(this.parameterList.getTaskID().getValue());
		deletingTask.updateLastModified(null);
		TaskList.getInstance().deleteTask(deletingTask);
		this.undoBackup = deletingTask;
		this.redoBackup = deletingTask;
		return String.format(MESSAGE_DELETE_FORMAT, this.parameterList.getTaskID().getValue());
	}

	@Override
	public InfluentialCommand undo() throws HandledException, FatalException {
		if (this.undoBackup == null){
			return null;
		} else {
			TaskList.getInstance().addTask(this.undoBackup);
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
}
