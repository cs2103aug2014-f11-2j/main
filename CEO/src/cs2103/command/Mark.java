package cs2103.command;

import java.util.Date;

import cs2103.TaskList;
import cs2103.exception.FatalException;
import cs2103.exception.HandledException;
import cs2103.parameters.TaskID;
import cs2103.task.PeriodicTask;
import cs2103.task.Task;
import cs2103.util.CommonUtil;

public class Mark extends InfluentialCommand {
	private static final String MESSAGE_MARK = "Successfully marked task %1$d as completed\n";
	private static final String MESSAGE_MARK_FAILED = "Failed to mark task %1$d as completed";
	private Task target;
	
	public Mark(String command) throws HandledException, FatalException{
		this.parameterList.addParameter(TaskID.parse(command));
		CommonUtil.checkNull(this.parameterList.getTaskID(), HandledException.ExceptionType.INVALID_TASKID);
		this.target = TaskList.getInstance().getTaskByID(this.parameterList.getTaskID().getValue());
	}

	@Override
	public String execute() throws HandledException, FatalException {
		CommonUtil.checkNull(this.target, HandledException.ExceptionType.INVALID_TASK_OBJ);
		if (this.target instanceof PeriodicTask){
			return String.format(MESSAGE_MARK_FAILED, this.parameterList.getTaskID().getValue());
		} else {
			Task newTask = cloneTask(this.target);
			newTask.updateCompleted(new Date());
			newTask = TaskList.getInstance().updateTask(newTask);
			if (newTask == null){
				return String.format(MESSAGE_MARK_FAILED, this.parameterList.getTaskID().getValue());
			} else {
				this.undoBackup = this.target;
				this.redoBackup = newTask;
				return this.formatReturnString(String.format(MESSAGE_MARK, this.parameterList.getTaskID().getValue()), newTask);
			}
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
