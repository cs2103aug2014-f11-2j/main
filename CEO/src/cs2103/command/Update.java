//@author A0116713M
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
import cs2103.util.Logger;

public class Update extends InfluentialCommand {
	private static final String MESSAGE_UPDATE = "You have updated task with ID %1$d\n";
	private static final String MESSAGE_UPDATE_FAIL = "Fail to update task with ID %1$d\n";
	private static final String LOG_UPDATE = "Executing Update: Parameters: TaskID: %1$d\ttitle: %2$s\tdescription %2$s\tlocation: %4$s\ttime: %5$s\trecurrence: %6$s\tcomplete: %7$s";
	private static final String TIME_FORMAT = "StartTime: %1$s\tEndTime: %2$s";
	private Task target;
	
	/**
	 * Creates an instance of update from user input
	 * @param command
	 * @throws HandledException
	 * @throws FatalException
	 */
	public Update(String command) throws HandledException, FatalException {
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
		assert(this.target != null);
		Logger.getInstance().writeLog(this.formatLogString());
		Task newTask;
		if (this.parameterList.getTime() == null) {
			newTask = cloneTask(this.target);
		} else {
			newTask = this.target.updateNewTask(this.parameterList.getTime().getValue());
		}
		if (this.parameterList.getTitle() != null) {
			newTask.updateTitle(this.parameterList.getTitle().getValue());
		}
		if (this.parameterList.getDescription() != null) {
			newTask.updateDescription(this.parameterList.getDescription().getValue());
		}
		if (this.parameterList.getLocation() != null) {
			newTask.updateLocation(this.parameterList.getLocation().getValue());
		}
		if (this.parameterList.getComplete() != null) {
			newTask.updateCompleted(this.parameterList.getComplete().getValue()?new Date():null);
		}
		if (this.parameterList.getRecurrence() != null) {
			newTask.updateRecurrence(this.parameterList.getRecurrence().getValue());
		}
		newTask = TaskList.getInstance().updateTask(newTask);
		return formatReturnString(newTask);
	}
	
	/**
	 * @param newTask
	 * @return Ansi formmated string result for Update class
	 * @throws HandledException
	 */
	private Ansi formatReturnString(Task newTask) throws HandledException {
		Ansi returnString = ansi();
		if (newTask == null) {
			return returnString.fg(RED).a(String.format(MESSAGE_UPDATE_FAIL, this.parameterList.getTaskID().getValue())).reset();
		} else {
			this.undoBackup = this.target;
			this.redoBackup = newTask;
			returnString.fg(GREEN).a(String.format(MESSAGE_UPDATE, this.parameterList.getTaskID().getValue())).reset();
			return returnString.a(newTask.toDetail());
		}
	}
	
	private String readTitle() throws HandledException {
		if (this.parameterList.getTitle() == null) {
			return "null";
		} else {
			return this.parameterList.getTitle().getValue();
		}
	}
	
	private String readDescription() throws HandledException {
		if (this.parameterList.getDescription() == null) {
			return "null";
		} else {
			return this.parameterList.getDescription().getValue();
		}
	}
	
	private String readLocation() throws HandledException {
		if (this.parameterList.getLocation() == null) {
			return "null";
		} else {
			return this.parameterList.getLocation().getValue();
		}
	}
	
	private String readComplete() throws HandledException {
		if (this.parameterList.getComplete() == null) {
			return "null";
		} else {
			if (this.parameterList.getComplete().getValue()) {
				return "true";
			} else {
				return "false";
			}
		}
	}
	
	private String readRecurrence() throws HandledException {
		if (this.parameterList.getRecurrence() == null) {
			return "null";
		} else {
			if (this.parameterList.getRecurrence().getValue() == null) {
				return "No Recurrence";
			} else {
				return this.parameterList.getRecurrence().getValue().toString();
			}
		}
	}
	
	private String formatTimeString(Date[] time) {
		assert (time != null);
		return String.format(TIME_FORMAT, this.formatTimeString(time[0]), this.formatTimeString(time[1]));
	}
	
	private String formatTimeString(Date time) {
		if (time == null) {
			return "null";
		} else {
			return time.toString();
		}
	}
	
	private String readTime() throws HandledException {
		if (this.parameterList.getTime() == null) {
			return "null";
		} else {
			return this.formatTimeString(this.parameterList.getTime().getValue());
		}
	}
	
	private String formatLogString() throws HandledException {
		assert(this.parameterList.getTaskID() != null);
		return String.format(LOG_UPDATE, this.parameterList.getTaskID().getValue(), this.readTitle(), this.readDescription(), this.readLocation(), this.readTime(), this.readRecurrence(), this.readComplete());
	}
	
	@Override
	public InfluentialCommand undo() throws HandledException, FatalException {
		if (this.undoBackup == null) {
			return null;
		} else {
			this.undoBackup.updateLastModified(null);
			TaskList.getInstance().updateTask(this.undoBackup);
			return this;
		}
	}
	
	@Override
	public InfluentialCommand redo() throws HandledException, FatalException {
		if (this.redoBackup == null) {
			return null;
		} else {
			this.undoBackup.updateLastModified(null);
			TaskList.getInstance().updateTask(this.redoBackup);
			return this;
		}
	}
}
