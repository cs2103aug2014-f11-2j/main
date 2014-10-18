package cs2103.command;

import cs2103.CommonUtil;
import cs2103.FatalException;
import cs2103.HandledException;
import cs2103.StorageEngine;
import cs2103.Task;
import cs2103.parameters.TaskID;

public class Delete extends WriteCommand {
	private static final String MESSAGE_DELETE_FORMAT = "You have deleted task with ID %1$d";
	
	public Delete(String command) throws HandledException{
		this.parameterList.addParameter(TaskID.parse(command));
	}
	
	@Override
	public String execute() throws HandledException, FatalException {
		CommonUtil.checkNullParameter(this.parameterList.getTaskID(), HandledException.ExceptionType.INVALID_CMD);
		Task deletingTask = getTaskByID(this.parameterList.getTaskID().getValue());
		StorageEngine.getInstance().deleteTask(deletingTask);
		this.undoBackup = deletingTask;
		this.redoBackup = deletingTask;
		return String.format(MESSAGE_DELETE_FORMAT, this.parameterList.getTaskID().getValue());
	}

	@Override
	public WriteCommand undo() throws HandledException, FatalException {
		if (this.undoBackup == null){
			return null;
		} else {
			StorageEngine.getInstance().updateTask(this.undoBackup);
			return this;
		}
	}

	@Override
	public WriteCommand redo() throws HandledException, FatalException {
		if (this.redoBackup == null){
			return null;
		} else {
			StorageEngine.getInstance().deleteTask(this.redoBackup);
			return this;
		}
	}
}
