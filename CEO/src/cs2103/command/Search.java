//@author A0112673L
package cs2103.command;

import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.Queue;

import org.fusesource.jansi.Ansi;

import cs2103.exception.FatalException;
import cs2103.exception.HandledException;
import cs2103.parameters.Complete;
import cs2103.parameters.Keyword;
import cs2103.parameters.TaskType;
import cs2103.parameters.Time;
import cs2103.storage.TaskList;
import cs2103.task.Task;
import cs2103.util.CommonUtil;
import cs2103.util.Logger;

public class Search extends QueryCommand {
	private static final String LOG_SEARCH = "Executing Search: Parameters: keyword: %1$s\tTaskType: %2$s\tcompleted: %3$s\ttime: %4$s";
	private static final String TIME_FORMAT = "StartTime: %1$s\tEndTime: %2$s";
	/**
	 * Creates an instance of Search from user input
	 * @param command
	 * @throws HandledException
	 */
	public Search(String command) throws HandledException {
		CommonUtil.checkNull(command, HandledException.ExceptionType.INVALID_CMD);
		Queue<String> parameterQueue = separateCommand(command);
		if (!command.startsWith("-")) {
			this.parameterList.addParameter(Keyword.parse(parameterQueue.poll()));
		}
		Map<String, String> parameterMap = separateParameters(parameterQueue);
		this.parameterList.addParameter(TaskType.parse(getParameterString(parameterMap, TaskType.allowedLiteral)));
		this.parameterList.addParameter(Time.parse(getParameterString(parameterMap, Time.allowedLiteral)));
		this.parameterList.addParameter(Complete.parse(getParameterString(parameterMap, Complete.allowedLiteral)));
	}
	
	@Override
	public Ansi execute() throws HandledException, FatalException {
		Logger.getInstance().writeLog(this.formatLogString());
		ArrayList<Task> searchList = getInitialList(this.parameterList.getTaskType());
		assert(searchList != null);
		if (this.parameterList.getKeyword() != null) {
			searchList = filterKeyword(searchList, parameterList.getKeyword().getValue());
		}
		if (this.parameterList.getTime() != null) {
			searchList = filterTime(searchList, parameterList.getTime().getValue());
		}
		if (this.parameterList.getComplete() != null) {
			searchList = filterComplete(searchList, parameterList.getComplete().getValue());
		}
		return parseListResponse(searchList);
	}
	
	/**
	 * @param taskType
	 * @return ArrayList of Task objects filtered by taskType
	 * @throws HandledException
	 * @throws FatalException
	 */
	private ArrayList<Task> getInitialList(TaskType taskType) throws HandledException, FatalException {
		if (taskType == null) {
			return TaskList.getInstance().getAllList();
		} else {
			switch (taskType.getValue()) {
			case FLOATING:
				return toTaskList(TaskList.getInstance().getFloatingList());
			case DEADLINE:
				return toTaskList(TaskList.getInstance().getDeadlineList());
			case PERIODIC:
				return toTaskList(TaskList.getInstance().getPeriodicList());
			case TRASH:
				return TaskList.getInstance().getTrashList();
			case ALL:
			case INVALID:
			default:
				return TaskList.getInstance().getAllList();
			}
		}
	}
	
	/**
	 * @param tasks
	 * @return ArrayList of Tasks from ArrayList of child classes of Task
	 */
	private static <T extends Task> ArrayList<Task> toTaskList(ArrayList<T> tasks) {
		ArrayList<Task> returnList = new ArrayList<Task>();
		for (Task task:tasks) {
			returnList.add(task);
		}
		return returnList;
	}
	
	/**
	 * @param searchList
	 * @param time
	 * @return ArrayList of tasks that are not floating
	 */
	private static ArrayList<Task> filterTime(ArrayList<Task> searchList, Date[] time) {
		ArrayList<Task> returnList = new ArrayList<Task>();
		if (searchList != null) {
			for (Task task:searchList) {
				if (task.checkPeriod(time)){
					returnList.add(task);
				}
			}
		}
		return returnList;
	}
	
	/**
	 * @param searchList
	 * @param complete
	 * @return ArrayList of Task that are completed
	 */
	private static ArrayList<Task> filterComplete(ArrayList<Task> searchList, boolean complete) {
		ArrayList<Task> returnList = new ArrayList<Task>();
		if (searchList != null) {
			for (Task task:searchList) {
				if ((task.getCompleted() != null) == complete) {
					returnList.add(task);
				}
			}
		}
		return returnList;
	}
	
	/**
	 * @param searchList
	 * @param keywordString
	 * @return ArrayList of Task that contain keywordString
	 */
	private static ArrayList<Task> filterKeyword(ArrayList<Task> searchList, String keywordString) {
		ArrayList<Task> returnList = new ArrayList<Task>();
		if (searchList != null) {
			for (Task task:searchList) {
				if (task.matches(keywordString)) {
					returnList.add(task);
				}
			}
		}
		return returnList;
	}
	
	private String readKeyword() throws HandledException {
		if (this.parameterList.getKeyword() == null) {
			return "null";
		} else {
			return this.parameterList.getKeyword().getValue();
		}
	}
	
	private String readTaskType() throws HandledException {
		if (this.parameterList.getTaskType() == null) {
			return "null";
		} else {
			return this.parameterList.getTaskType().getValue().toString();
		}
	}
	
	private String readComplete() throws HandledException {
		if (this.parameterList.getComplete() == null) {
			return "null";
		} else {
			if(this.parameterList.getComplete().getValue()){
				return "true";
			} else {
				return "false";
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
		return String.format(LOG_SEARCH, this.readKeyword(), this.readTaskType(), this.readComplete(), this.readTime());
	}
}
