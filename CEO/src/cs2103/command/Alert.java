package cs2103.command;

import java.util.ArrayList;

import org.fusesource.jansi.Ansi;

import static org.fusesource.jansi.Ansi.*;
import cs2103.exception.FatalException;
import cs2103.exception.HandledException;
import cs2103.storage.TaskList;
import cs2103.task.DeadlineTask;
import cs2103.task.PeriodicTask;
import cs2103.util.Logger;

public class Alert extends QueryCommand {
	private static final String MESSAGE_TASKS_DUE = "Tasks due within one day:\n";
	private static final String MESSAGE_TASKS_STARTING = "Tasks start within one day:\n";
	private static final String LOG_ALERT = "Executing Alert";
	
	@Override
	public Ansi execute() throws HandledException, FatalException {	
		Logger.getInstance().writeLog(LOG_ALERT);
		Ansi returnString = ansi().a(MESSAGE_TASKS_DUE);
		returnString.a(parseListResponse(this.alertDeadline()));
		returnString.a(MESSAGE_TASKS_STARTING);
		returnString.a(parseListResponse(this.alertPeriodic()));
		return returnString;
	}
	
	/**Returns list of DeadlineTask due within a day
	 * @return ArrayList of DeadlineTasks
	 * @throws HandledException
	 * @throws FatalException
	 */
	private ArrayList<DeadlineTask> alertDeadline() throws HandledException, FatalException{
		ArrayList<DeadlineTask> taskList = TaskList.getInstance().getDeadlineList();
		ArrayList<DeadlineTask> returnList = new ArrayList<DeadlineTask>();
		for (DeadlineTask task:taskList){
			if (task.checkAlert()){
				returnList.add(task);
			}
		}
		return returnList;
	}

	/**
	 * Returns list of PeriodicTask due starting within one day
	 * @return ArrayList of PeriodicTask
	 * @throws HandledException
	 * @throws FatalException
	 */
	private ArrayList<PeriodicTask> alertPeriodic() throws HandledException, FatalException{
		ArrayList<PeriodicTask> taskList = TaskList.getInstance().getPeriodicList();
		ArrayList<PeriodicTask> returnList = new ArrayList<PeriodicTask>();
		for (PeriodicTask task:taskList){
			if (task.checkAlert()){
				returnList.add(task);
			}
		}
		return returnList;
	}

}
