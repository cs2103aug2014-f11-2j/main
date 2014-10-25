package cs2103.command;

import java.util.Map;
import java.util.Queue;

import cs2103.CommonUtil;
import cs2103.TaskList;
import cs2103.exception.FatalException;
import cs2103.exception.HandledException;
import cs2103.parameters.DeleteOption;
import cs2103.parameters.TaskID;
import cs2103.task.Task;

public class Delete extends InfluentialCommand {
	private static final String MESSAGE_DELETE_FORMAT = "You have deleted task with ID %1$d";
	
	public Delete(String command) throws HandledException{
		CommonUtil.checkNull(command, HandledException.ExceptionType.INVALID_CMD);
		Queue<String> parameterQueue = separateCommand(command);
		this.parameterList.addParameter(TaskID.parse(parameterQueue.poll()));
		Map<String, String> parameterMap = separateParameters(parameterQueue);
		this.parameterList.addParameter(DeleteOption.parse(getParameterString(parameterMap, DeleteOption.allowedLiteral)));
	}
	
	@Override
	public String execute() throws HandledException, FatalException {
		CommonUtil.checkNull(this.parameterList.getTaskID(), HandledException.ExceptionType.INVALID_CMD);
		Task deletingTask = TaskList.getInstance().getTaskByID(this.parameterList.getTaskID().getValue());
		deletingTask.updateLastModified(null);
		if (this.parameterList.getDeleteOption() != null){
			deletingTask.delete();
		}
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
			TaskList.getInstance().restoreTask(this.undoBackup);
			return this;
		}
	}

	@Override
	public InfluentialCommand redo() throws HandledException, FatalException {
		if (this.redoBackup == null){
			return null;
		} else {
			if (this.parameterList.getDeleteOption() != null){
				this.redoBackup.delete();
			}
			TaskList.getInstance().deleteTask(this.redoBackup);
			return this;
		}
	}
}
