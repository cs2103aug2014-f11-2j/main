package cs2103.command;

import java.util.ArrayList;

import cs2103.DeadlineTask;
import cs2103.FatalException;
import cs2103.HandledException;
import cs2103.PeriodicTask;

public class Alert extends ReadCommand {
	private static final String MESSAGE_TASKS_DUE = "Tasks due within one day:\n";
	private static final String MESSAGE_TASKS_STARTING = "Tasks start within one day:\n";
	
	@Override
	public String execute() throws HandledException, FatalException {
		StringBuffer sb = new StringBuffer();
		String deadlineAlert = alertDeadline();
		String periodicAlert = alertPeriodic();
		if (!deadlineAlert.isEmpty()){
			sb.append(MESSAGE_TASKS_DUE);
			sb.append(deadlineAlert);
		}
		if (!periodicAlert.isEmpty()){
			sb.append(MESSAGE_TASKS_STARTING);
			sb.append(periodicAlert);
		}
		return sb.toString();
	}
	
	private static String alertDeadline() throws HandledException, FatalException{
		ArrayList<DeadlineTask> taskList = getDeadlineList();
		StringBuffer sb = new StringBuffer();
		for (DeadlineTask task:taskList){
			if (task.checkAlert()){
				sb.append(task.toSummary());
			}
		}
		return sb.toString();
	}

	private static String alertPeriodic() throws HandledException, FatalException{
		ArrayList<PeriodicTask> taskList = getPeriodicList();
		StringBuffer sb = new StringBuffer();
		for (PeriodicTask task:taskList){
			if (task.checkAlert()){
				sb.append(task.toSummary());
			}
		}
		return sb.toString();
	}

}
