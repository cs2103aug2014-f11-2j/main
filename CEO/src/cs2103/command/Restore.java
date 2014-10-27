package cs2103.command;

import cs2103.TaskList;
import cs2103.exception.FatalException;
import cs2103.exception.HandledException;
import cs2103.parameters.TaskID;
import cs2103.task.Task;
import cs2103.util.CommonUtil;

public class Restore extends InfluentialCommand {
	private static final String MESSAGE_RESTORE = "You have successfully restored a task with ID %1$d\n";
	private static final String MESSAGE_RESTORE_FAIL = "Failed to restor the task with ID %1$d";
	private Task target;
	
	public Restore(String command) throws HandledException, FatalException {
		this.parameterList.addParameter(TaskID.parse(command));
		CommonUtil.checkNull(this.parameterList.getTaskID(), HandledException.ExceptionType.INVALID_CMD);
		this.target = TaskList.getInstance().getTaskByID(this.parameterList.getTaskID().getValue());
	}

	@Override
	public InfluentialCommand undo() throws HandledException, FatalException {
		if (this.undoBackup == null){
			return null;
		} else {
			this.undoBackup.delete();
			TaskList.getInstance().updateTask(this.undoBackup);
			return this;
		}
	}
	
	@Override
	public InfluentialCommand redo() throws HandledException, FatalException {
		if (this.redoBackup == null){
			return null;
		} else {
			this.execute();
			return this;
		}
	}

	@Override
	public String execute() throws HandledException, FatalException {
		CommonUtil.checkNull(this.target, HandledException.ExceptionType.INVALID_TASK_OBJ);
		this.target.restore();
		this.target = TaskList.getInstance().updateTask(this.target);
		if (this.target == null){
			return String.format(MESSAGE_RESTORE_FAIL, parameterList.getTaskID().getValue());
		} else {
			this.undoBackup = this.target;
			this.redoBackup = this.target;
			return this.formatReturnString(String.format(MESSAGE_RESTORE, parameterList.getTaskID().getValue()), this.target);
		}
	}
}
