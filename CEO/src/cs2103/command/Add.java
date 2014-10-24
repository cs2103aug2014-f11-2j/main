package cs2103.command;

import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.Queue;

import net.fortuna.ical4j.model.Recur;
import cs2103.CommonUtil;
import cs2103.TaskList;
import cs2103.exception.FatalException;
import cs2103.exception.HandledException;
import cs2103.parameters.Description;
import cs2103.parameters.Location;
import cs2103.parameters.Parameter;
import cs2103.parameters.Recurrence;
import cs2103.parameters.Time;
import cs2103.parameters.Title;
import cs2103.task.*;

public class Add extends InfluentialCommand {
	private static final String MESSAGE_ADD = "You have successfully added a new task.\n";
	private static final String[] allowedQuickTimeLiteral = {"from", "by", "on", "in", "at"};
	
	public Add(String command) throws HandledException{
		CommonUtil.checkNull(command, HandledException.ExceptionType.INVALID_CMD);
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
		task.updateLastModified(null);
		TaskList.getInstance().addTask(task);
		this.undoBackup = task;
		this.redoBackup = task;
		return this.formatReturnString(MESSAGE_ADD, task);
	}
	
	private static ArrayList<Parameter> parseQuickAdd(String quickAddString) throws HandledException{
		ArrayList<Parameter> parameterList = new ArrayList<Parameter>();
		int timeIndex = -1;
		for (String s:allowedQuickTimeLiteral){
			timeIndex = quickAddString.lastIndexOf(s);
			if (timeIndex > 0) break;
		}
		int everyIndex = quickAddString.lastIndexOf("every");
		if (timeIndex > 0 && everyIndex < timeIndex){
			Time time = parseQuickTime(quickAddString.substring(timeIndex));
			if (time == null){
				parameterList.add(Title.parse(quickAddString));
			} else {
				parameterList.add(Title.parse(quickAddString.substring(0, timeIndex)));
				parameterList.add(time);
			}
		} else if (timeIndex > 0 && everyIndex > timeIndex){
			Time time = parseQuickTime(quickAddString.substring(timeIndex, everyIndex));
			if (time == null){
				parameterList.add(Title.parse(quickAddString));
			} else {
				parameterList.add(Title.parse(quickAddString.substring(0, timeIndex)));
				parameterList.add(time);
				parameterList.add(Recurrence.parse(quickAddString.substring(everyIndex)));
			}
		} else {
			parameterList.add(Title.parse(quickAddString));
		}
		return parameterList;
	}
	
	private static Time parseQuickTime(String timeString){
		Time time = Time.parse(timeString);
		Date[] timeArray = getTime(time);
		if (timeArray[0] == null && timeArray[1] == null){
			return null;
		} else {
			return time;
		}
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
	public InfluentialCommand undo() throws HandledException, FatalException {
		if (this.undoBackup == null){
			return null;
		} else {
			TaskList.getInstance().deleteTask(this.undoBackup);
			return this;
		}
	}

	@Override
	public InfluentialCommand redo() throws HandledException, FatalException {
		if (this.redoBackup == null){
			return null;
		} else {
			TaskList.getInstance().addTask(this.redoBackup);
			return this;
		}
	}
}
