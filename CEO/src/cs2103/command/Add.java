package cs2103.command;

import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.Queue;

import org.fusesource.jansi.Ansi;
import static org.fusesource.jansi.Ansi.*;
import static org.fusesource.jansi.Ansi.Color.*;
import net.fortuna.ical4j.model.Recur;
import cs2103.exception.FatalException;
import cs2103.exception.HandledException;
import cs2103.parameters.Description;
import cs2103.parameters.Location;
import cs2103.parameters.Parameter;
import cs2103.parameters.Recurrence;
import cs2103.parameters.Time;
import cs2103.parameters.Title;
import cs2103.storage.TaskList;
import cs2103.task.*;
import cs2103.util.CommonUtil;

public class Add extends InfluentialCommand {
	private static final String MESSAGE_ADD = "You have successfully added a new task.\n";
	private static final String MESSAGE_ADD_FAIL = "Fail to add a new task.\n";
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
	public Ansi execute() throws HandledException, FatalException {
		Task task;
		Date[] time = getTime(this.parameterList.getTime());
		if (time[0] == null){
			task = new FloatingTask(null, null);
		} else if (time[1] == null){
			task = new DeadlineTask(null, null, time[0]);
		} else {
			task = new PeriodicTask(null, null, time[0], time[1]);
		}
		task.updateTitle(this.readTitle());
		task.updateDescription(this.readDescription());
		task.updateLocation(this.readLocation());
		task.updateRecurrence(this.readRecurrence());
		task.updateLastModified(null);
		task = TaskList.getInstance().addTask(task);
		return this.formatReturnString(task);
	}
	
	private static ArrayList<Parameter> parseQuickAdd(String quickAddString) throws HandledException{
		ArrayList<Parameter> parameterList = new ArrayList<Parameter>();
		int timeIndex = -1;
		for (String s:allowedQuickTimeLiteral){
			timeIndex = quickAddString.lastIndexOf(s);
			if (timeIndex > 0) break;
		}
		int everyIndex = quickAddString.lastIndexOf("every");
		if (timeIndex < 0) {
			parameterList.add(Title.parse(quickAddString));
		} else if (everyIndex > timeIndex){
			Time time = parseQuickTime(quickAddString.substring(timeIndex, everyIndex));
			if (time == null){
				parameterList.add(Title.parse(quickAddString));
			} else {
				parameterList.add(Title.parse(quickAddString.substring(0, timeIndex)));
				parameterList.add(time);
				parameterList.add(Recurrence.parse(quickAddString.substring(everyIndex)));
			}
		} else {
			Time time = parseQuickTime(quickAddString.substring(timeIndex));
			if (time == null){
				parameterList.add(Title.parse(quickAddString));
			} else {
				parameterList.add(Title.parse(quickAddString.substring(0, timeIndex)));
				parameterList.add(time);
			}
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
		if (timeParameter == null){
			return new Date[2];
		} else {
			return timeParameter.getValue();
		}
	}
	
	private Ansi formatReturnString(Task task){
		if (task == null){
			return ansi().bold().fg(RED).a(MESSAGE_ADD_FAIL).reset();
		} else {
			this.undoBackup = task;
			this.redoBackup = task;
			return ansi().fg(GREEN).a(MESSAGE_ADD).a(task.toDetail());
		}
	}
	
	private String readTitle() throws HandledException{
		if (this.parameterList.getTitle() == null){
			return null;
		} else {
			return this.parameterList.getTitle().getValue();
		}
	}
	
	private String readDescription() throws HandledException{
		if (this.parameterList.getDescription() == null){
			return null;
		} else {
			return this.parameterList.getDescription().getValue();
		}
	}
	
	private String readLocation() throws HandledException{
		if (this.parameterList.getLocation() == null){
			return null;
		} else {
			return this.parameterList.getLocation().getValue();
		}
	}
	
	private Recur readRecurrence() throws HandledException{
		if (this.parameterList.getRecurrence() == null){
			return null;
		} else {
			return this.parameterList.getRecurrence().getValue();
		}
	}
	
	@Override
	public InfluentialCommand undo() throws HandledException, FatalException {
		if (this.undoBackup == null){
			return null;
		} else {
			this.undoBackup.updateLastModified(null);
			TaskList.getInstance().deleteTask(this.undoBackup);
			return this;
		}
	}

	@Override
	public InfluentialCommand redo() throws HandledException, FatalException {
		if (this.redoBackup == null){
			return null;
		} else {
			this.redoBackup.updateLastModified(null);
			TaskList.getInstance().addTask(this.redoBackup);
			return this;
		}
	}
}
