package cs2103.command;

import java.util.Map;
import java.util.Queue;

import cs2103.CommonUtil;
import cs2103.TaskList;
import cs2103.exception.FatalException;
import cs2103.exception.HandledException;
import cs2103.parameters.Complete;
import cs2103.parameters.Description;
import cs2103.parameters.Location;
import cs2103.parameters.Recurrence;
import cs2103.parameters.TaskID;
import cs2103.parameters.Time;
import cs2103.parameters.Title;
import cs2103.task.Task;

public class Update extends InfluentialCommand {
	private static final String MESSAGE_UPDATE_FORMAT = "You have updated task with ID %1$d";
	
	public Update(String command) throws HandledException{
		CommonUtil.checkNullString(command, HandledException.ExceptionType.INVALID_CMD);
		Queue<String> parameterQueue = separateCommand(command);
		this.parameterList.addParameter(TaskID.parse(parameterQueue.poll()));
		Map<String, String> parameterMap = separateParameters(parameterQueue);
		this.parameterList.addParameter(Title.parse(getParameterString(parameterMap, Title.allowedLiteral)));
		this.parameterList.addParameter(Description.parse(getParameterString(parameterMap, Description.allowedLiteral)));
		this.parameterList.addParameter(Time.parse(getParameterString(parameterMap, Time.allowedLiteral)));
		this.parameterList.addParameter(Location.parse(getParameterString(parameterMap, Location.allowedLiteral)));
		this.parameterList.addParameter(Recurrence.parse(getParameterString(parameterMap, Recurrence.allowedLiteral)));
		this.parameterList.addParameter(Complete.parse(getParameterString(parameterMap, Complete.allowedLiteral)));
		if (this.parameterList.getParameterCount() <= 1) throw new HandledException(HandledException.ExceptionType.LESS_THAN_ONE_PARA);
	}
	
	@Override
	public String execute() throws HandledException, FatalException {
		if (this.parameterList.getTaskID() == null){
			throw new HandledException(HandledException.ExceptionType.INVALID_TASKID);
		} else {
			Task task = TaskList.getInstance().getTaskByID(this.parameterList.getTaskID().getValue());
			Task newTask;
			if (this.parameterList.getTime() == null){
				newTask = cloneTask(task);
			} else {
				newTask = task.convert(this.parameterList.getTime().getValue());
			}
			if (this.parameterList.getTitle() != null){
				newTask.updateTitle(this.parameterList.getTitle().getValue());
			}
			if (this.parameterList.getDescription() != null){
				newTask.updateDescription(this.parameterList.getDescription().getValue());
			}
			if (this.parameterList.getLocation() != null){
				newTask.updateLocation(this.parameterList.getLocation().getValue());
			}
			if (this.parameterList.getComplete() != null){
				newTask.updateComplete(this.parameterList.getComplete().getValue());
			}
			if (this.parameterList.getRecurrence() != null){
				newTask.updateRecurrence(this.parameterList.getRecurrence().getValue());
			}
			newTask.updateLastModified(null);
			TaskList.getInstance().updateTask(newTask);
			this.undoBackup = task;
			this.redoBackup = newTask;
			return String.format(MESSAGE_UPDATE_FORMAT, this.parameterList.getTaskID().getValue());
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
