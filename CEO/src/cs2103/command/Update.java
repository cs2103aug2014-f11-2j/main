package cs2103.command;

import java.util.Date;
import java.util.Map;
import java.util.Queue;

import org.fusesource.jansi.Ansi;
import static org.fusesource.jansi.Ansi.*;
import static org.fusesource.jansi.Ansi.Color.*;
import cs2103.exception.FatalException;
import cs2103.exception.HandledException;
import cs2103.parameters.Complete;
import cs2103.parameters.Description;
import cs2103.parameters.Location;
import cs2103.parameters.Recurrence;
import cs2103.parameters.TaskID;
import cs2103.parameters.Time;
import cs2103.parameters.Title;
import cs2103.storage.TaskList;
import cs2103.task.Task;
import cs2103.util.CommonUtil;

public class Update extends InfluentialCommand {
	private static final String MESSAGE_UPDATE = "You have updated task with ID %1$d\n";
	private static final String MESSAGE_UPDATE_FAIL = "Fail to update task with ID %1$d\n";
	private Task target;
	
	public Update(String command) throws HandledException, FatalException{
		CommonUtil.checkNull(command, HandledException.ExceptionType.INVALID_CMD);
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
		CommonUtil.checkNull(this.parameterList.getTaskID(), HandledException.ExceptionType.INVALID_TASKID);
		this.target = TaskList.getInstance().getTaskByID(this.parameterList.getTaskID().getValue());
	}
	
	@Override
	public Ansi execute() throws HandledException, FatalException {
		CommonUtil.checkNull(this.target, HandledException.ExceptionType.INVALID_TASK_OBJ);
		Task newTask;
		if (this.parameterList.getTime() == null){
			newTask = cloneTask(this.target);
		} else {
			newTask = this.target.update(this.parameterList.getTime().getValue());
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
			newTask.updateCompleted(this.parameterList.getComplete().getValue()?new Date():null);
		}
		if (this.parameterList.getRecurrence() != null){
			newTask.updateRecurrence(this.parameterList.getRecurrence().getValue());
		}
		newTask = TaskList.getInstance().updateTask(newTask);
		return formatReturnString(newTask);
	}
	
	private static Task cloneTask(Task task) throws HandledException{
		try {
			Task newTask = (Task) task.clone();
			return newTask;
		} catch (CloneNotSupportedException e) {
			throw new HandledException(HandledException.ExceptionType.CLONE_FAILED);
		}
	}
	
	private Ansi formatReturnString(Task newTask) throws HandledException{
		Ansi returnString = ansi();
		if (newTask == null){
			return returnString.fg(RED).a(String.format(MESSAGE_UPDATE_FAIL, this.parameterList.getTaskID().getValue())).reset();
		} else {
			this.undoBackup = this.target;
			this.redoBackup = newTask;
			returnString.fg(GREEN).a(String.format(MESSAGE_UPDATE, this.parameterList.getTaskID().getValue())).reset();
			return returnString.a(newTask.toDetail());
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
