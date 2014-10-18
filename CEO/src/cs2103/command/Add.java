package cs2103.command;

import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.Queue;

import net.fortuna.ical4j.model.Recur;
import cs2103.CommonUtil;
import cs2103.DeadlineTask;
import cs2103.FatalException;
import cs2103.FloatingTask;
import cs2103.HandledException;
import cs2103.PeriodicTask;
import cs2103.StorageEngine;
import cs2103.Task;
import cs2103.parameters.Description;
import cs2103.parameters.Location;
import cs2103.parameters.Parameter;
import cs2103.parameters.Recurrence;
import cs2103.parameters.Time;
import cs2103.parameters.Title;

public class Add extends WriteCommand {
	private static final String MESSAGE_ADD = "You have successfully added a new task.";
	
	public Add(String command) throws HandledException{
		CommonUtil.checkNullString(command, HandledException.ExceptionType.INVALID_CMD);
		Queue<String> parameterQueue = separateCommand(command);
		if (!command.startsWith("-")){
			this.parameterList.addAllParameters(parseQuickAdd(parameterQueue.poll()));
		}
		Map<String, String> parameterMap = separateParameters(parameterQueue);
		this.parameterList.addParameter(Title.parse(getParameterString(parameterMap, Title.allowedLiteral)));
		this.parameterList.addParameter(Description.parse(getParameterString(parameterMap, Description.allowedLiteral)));
		this.parameterList.addParameter(Time.parse(getParameterString(parameterMap, Time.allowedLiteral)));
		this.parameterList.addParameter(Location.parse(getParameterString(parameterMap, Location.allowedLiteral)));
		this.parameterList.addParameter(Recurrence.parse(getParameterString(parameterMap, Recurrence.allowedLiteral)));
	}
	
	@Override
	public String execute() throws HandledException, FatalException {
		Task task;
		Date[] time = getTime(this.parameterList.getTime());
		String title = this.parameterList.getTitle() == null ? null : this.parameterList.getTitle().getValue();
		String description = this.parameterList.getDescription() == null ? null : this.parameterList.getDescription().getValue();
		String location = this.parameterList.getLocation() == null? null : this.parameterList.getLocation().getValue();
		Recur recurrence = this.parameterList.getRecurrence() == null ? null : this.parameterList.getRecurrence().getValue();
		if (time[0] == null && time[1] == null){
			task = new FloatingTask(null, null, title, false);
		} else if (time[1] == null){
			task = new DeadlineTask(null, null, title, time[0], false);
		} else {
			task = new PeriodicTask(null, null, title, location, time[0], time[1], recurrence);
		}
		task.updateDescription(description);
		StorageEngine.getInstance().updateTask(task);
		this.undoBackup = task;
		this.redoBackup = task;
		return MESSAGE_ADD;
	}
	
	private static ArrayList<Parameter> parseQuickAdd(String quickAddString){
		return null;
	}
	
	private static Date[] getTime(Time timeParameter){
		Date[] time = new Date[2];
		if (timeParameter == null){
			return time;
		} else {
			return timeParameter.getValue();
		}
	}

	@Override
	public WriteCommand undo() throws HandledException, FatalException {
		if (this.undoBackup == null){
			return null;
		} else {
			StorageEngine.getInstance().deleteTask(this.undoBackup);
			return this;
		}
	}

	@Override
	public WriteCommand redo() throws HandledException, FatalException {
		if (this.redoBackup == null){
			return null;
		} else {
			StorageEngine.getInstance().updateTask(this.redoBackup);
			return this;
		}
	}
}
