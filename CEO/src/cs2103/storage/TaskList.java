//@author A0110906R
package cs2103.storage;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import static org.fusesource.jansi.Ansi.*;
import static org.fusesource.jansi.Ansi.Color.*;
import cs2103.exception.FatalException;
import cs2103.exception.HandledException;
import cs2103.parameters.Option;
import cs2103.task.DeadlineTask;
import cs2103.task.FloatingTask;
import cs2103.task.PeriodicTask;
import cs2103.task.Task;
import cs2103.util.CommonUtil;
import cs2103.util.Logger;

import java.util.Collections;

/**
 * Compiles the tasks into lists
 */
public class TaskList {
	private static TaskList taskList;
	private StorageInterface storage;
	private final File dataFile;
	private GoogleEngine google;
	private ArrayList<Task> tasks;
	private final Logger logger;
	private static final String SYNCING = "Syncing with Google, please wait for a while\n";
	private static final String COMMIT_ERROR = "Some error occurred when commit changes to Google";
	private static final String SYNC_FROM_GOOGLE = "Downloaded %1$d tasks from Google\n";
	private static final String SYNC_TO_GOOGLE = "Uploaded %1$d tasks to Google\n";
	private static final String SYNC_FAIL = "Unable to sync your data with Google, Google Sync is disabled";
	private static final String LOG_ADD = "Trying to insert a Task with UID %1$s into the list";
	private static final String LOG_UPDATE = "Trying to update a Task with UID %1$s in the list";
	private static final String LOG_REMOVE = "Tring to remove task with UID %1$s from the list";
	private static final String LOG_INITIALIZE = "Initializing TaskList";
	
	private TaskList(Option option) throws FatalException, HandledException {
		this.dataFile = new File("CEOStore.ics");
		this.logger = Logger.getInstance();
		this.logger.writeLog(LOG_INITIALIZE);
		switch(option.getValue()) {
		default:
		case SYNC:
		case DEFAULT:
			try {
				this.google = GoogleEngine.getInstance();
			} catch (HandledException e) {
				CommonUtil.printErrMsg(e.getErrorMsg());
				this.google = null;
			}
		case NOSYNC:
			this.storage = new StorageEngine(this.dataFile);
			break;
		case TEST:
			this.storage = new StorageStub();
			break;
		}
		this.tasks = this.storage.getTaskList();
		this.syncWithGoogle();
	}
	
	/**
	 * Method returns different instances of the TaskList class
	 * based on the Option arguments.
	 * 
	 * @param option
	 * @return An instance of the TaskList class
	 * @throws HandledException
	 * @throws FatalException
	 */
	public static TaskList getInstance(Option option) throws HandledException, FatalException {
		if (taskList == null) {
			taskList = new TaskList(option);
		}
		return taskList;
	}
	
	/**
	 * Method returns an instance of the TaskList class.
	 * 
	 * @return An instance of the TaskList class
	 * @throws FatalException
	 */
	public static TaskList getInstance() throws FatalException {
		if (taskList == null) {
			throw new FatalException(FatalException.ExceptionType.NOT_INITIALIZED);
		} else {
			return taskList;
		}
	}
	
	/**
	 * Method returns an ArrayList of periodic tasks.
	 * 
	 * @return an ArrayList of PeriodicTask objects
	 * @throws HandledException
	 * @throws FatalException
	 */
	public ArrayList<PeriodicTask> getPeriodicList() throws HandledException, FatalException {
		ArrayList<PeriodicTask> returnList = new ArrayList<PeriodicTask>();
		for (Task task:this.getAllList()) {
			if (task instanceof PeriodicTask) {
				returnList.add((PeriodicTask) task);
			}
		}
		Collections.sort(returnList, PeriodicTask.getComparator());
		return returnList;
	}
	
	/**
	 * Method returns an ArrayList of Deadline tasks.
	 * 
	 * @return an ArrayList of DeadlineTask objects
	 * @throws HandledException
	 * @throws FatalException
	 */
	public ArrayList<DeadlineTask> getDeadlineList() throws HandledException, FatalException {
		ArrayList<DeadlineTask> returnList = new ArrayList<DeadlineTask>();
		for (Task task:this.getAllList()) {
			if (task instanceof DeadlineTask) {
				returnList.add((DeadlineTask) task);
			}
		}
		Collections.sort(returnList, DeadlineTask.getComparator());
		return returnList;
	}
	
	/**
	 * Method returns an ArrayList of floating tasks.
	 * 
	 * @return an ArrayList of FloatingTask objects
	 * @throws HandledException
	 * @throws FatalException
	 */
	public ArrayList<FloatingTask> getFloatingList() throws HandledException, FatalException {
		ArrayList<FloatingTask> returnList = new ArrayList<FloatingTask>();
		for (Task task:this.getAllList()) {
			if (task instanceof FloatingTask) {
				returnList.add((FloatingTask) task);
			}
		}
		return returnList;
	}
	
	/**
	 * Method returns an ArrayList of deleted tasks.
	 * 
	 * @return an ArrayList of Task objects
	 */
	public ArrayList<Task> getTrashList() {
		return this.filterList(this.tasks, true);
	}
	
	/**
	 * Method returns an ArrayList of default tasks.
	 * 
	 * @return an ArrayList of Task objects.
	 * @throws HandledException
	 */
	public ArrayList<Task> getDefaultList() throws HandledException {
		ArrayList<Task> returnList = new ArrayList<Task>();
		for (Task task:this.getAllList()) {
			if (task.getCompleted() == null) {
				returnList.add(task);
			}
		}
		return returnList;
	}
	
	/**
	 * Method returns an ArrayList of all task types.
	 * 
	 * @return an ArrayList of Task objects
	 * @throws HandledException
	 */
	public ArrayList<Task> getAllList() throws HandledException {
		return this.filterList(this.tasks, false);
	}
	
	/**
	 * Method returns a Task object based on the integer argument.
	 * 
	 * @param taskID
	 * @return a Task object based on the taskID parameter
	 * @throws HandledException
	 */
	public Task getTaskByID(int taskID) throws HandledException {
		if (taskID > this.tasks.size() || taskID < 1) {
			throw new HandledException(HandledException.ExceptionType.INVALID_TASKID);
		} else {
			return this.tasks.get(taskID - 1);
		}
	}
	
	/**
	 * Method adds the Task argument to the storage component.
	 * 
	 * @param task
	 * @return a Task object based on the Task argument
	 * @throws HandledException
	 * @throws FatalException
	 */
	public Task addTask(Task task) throws HandledException, FatalException {
		this.logger.writeLog(CommonUtil.formatLogString(LOG_ADD, task));
		this.storage.updateTask(task);
		this.tasks = this.storage.getTaskList();
		Task returnTask = this.getTaskByTask(task);
		if (this.google != null) {
			Task added = this.commitAddToGoogle(task);
			this.tasks = this.storage.getTaskList();
			if (added != null) {
				returnTask = this.getTaskByTask(added);
			}
		}
		return returnTask;
	}
	
	/**
	 * Updates the Task object in the storage based on the Task argument.
	 * 
	 * @param task
	 * @return a Task object based on the Task argument
	 * @throws HandledException
	 * @throws FatalException
	 */
	public Task updateTask(Task task) throws HandledException, FatalException {
		this.logger.writeLog(CommonUtil.formatLogString(LOG_UPDATE, task));
		this.storage.updateTask(task);
		this.tasks = this.storage.getTaskList();
		Task returnTask = this.getTaskByTask(task);
		if (this.google != null) {
			Task updated = this.commitUpdateToGoogle(task);
			this.tasks = this.storage.getTaskList();
			if (updated != null) {
				returnTask = this.getTaskByTask(updated);
			}
		}
		return returnTask;
	}
	
	/**
	 * Deletes a Task object from storage based on the Task argument.
	 * 
	 * @param task
	 * @throws HandledException
	 * @throws FatalException
	 */
	public void deleteTask(Task task) throws HandledException, FatalException {
		this.logger.writeLog(CommonUtil.formatLogString(LOG_REMOVE, task));
		this.storage.deleteTask(task);
		this.tasks = this.storage.getTaskList();
		if (this.google != null) {
			this.commitDeleteToGoogle(task);
			this.tasks = this.storage.getTaskList();
		}
	}
	
	/**
	 * Returns a true value if the system manages to sync with google,
	 * false otherwise.
	 * 
	 * @return a boolean value based on whether the system is able to sync with google
	 * @throws HandledException
	 */
	public boolean manualSync() throws HandledException {
		if (this.google == null) {
			this.google = GoogleEngine.getInstance();
		}
		return this.syncWithGoogle();
	}
	
	/**
	 * Disables the system's connection with google.
	 */
	public void disableSync() {
		this.google = null;
	}
	
	public void emptyTestList() throws FatalException, HandledException {
		if (this.storage instanceof StorageStub) {
			this.storage = new StorageStub();
		}
	}
	
	private void commitDeleteToGoogle(Task task) throws HandledException {
		assert(this.google != null);
		try {
			if (this.google.needToSync(task)) {
				this.syncWithGoogle();
			} else {
				this.google.deleteTask(task);
				this.google.updateLastUpdated();
			}
		} catch (IOException e) {
			this.logger.writeErrLog(COMMIT_ERROR, e);
			CommonUtil.printErrMsg(COMMIT_ERROR);
		}
	}
	
	private Task commitUpdateToGoogle(Task task) throws HandledException, FatalException {
		assert(this.google != null);
		try {
			if (this.google.needToSync(task)) {
				this.syncWithGoogle();
			} else {
				return getCommitUpdateResult(task);
			}
		} catch (IOException e) {
			this.logger.writeErrLog(COMMIT_ERROR, e);
			CommonUtil.printErrMsg(COMMIT_ERROR);
		}
		return null;
	}
	
	private Task getCommitUpdateResult(Task task) throws HandledException, FatalException, IOException {
		assert(this.google != null);
		if (task.isDeleted()) {
			this.google.deleteTask(task);
			return null;
		} else {
			Task updating = this.google.updateTask(task);
			this.google.updateLastUpdated();
			this.updateUidInList(task, updating);
			return updating;
		}
	}

	private Task commitAddToGoogle(Task task) throws HandledException, FatalException {
		assert(this.google != null);
		try {
			if (this.google.needToSync(task)) {
				this.syncWithGoogle();
			} else {
				return getCommitAddResult(task);
			}
		} catch (IOException e) {
			this.logger.writeErrLog(COMMIT_ERROR, e);
			CommonUtil.printErrMsg(COMMIT_ERROR);
		}
		return null;
	}
	
	private Task getCommitAddResult(Task task) throws HandledException, FatalException, IOException {
		assert(this.google != null);
		Task returnTask = this.google.addTask(task);
		this.updateUidInList(task, returnTask);
		this.google.updateLastUpdated();
		return returnTask;
	}
	
	private boolean syncWithGoogle() {
		if (this.google == null) return false;
		CommonUtil.print(SYNCING);
		this.logger.writeLog(SYNCING);
		try {
			ArrayList<Task> googleList = this.google.getTaskList();
			String sync_from_google = String.format(SYNC_FROM_GOOGLE, syncFromGoogle(googleList));
			CommonUtil.print(ansi().fg(YELLOW).a(sync_from_google).reset());
			String sync_to_google = String.format(SYNC_TO_GOOGLE, syncToGoogle(googleList));
			CommonUtil.print(ansi().fg(YELLOW).a(sync_to_google).reset());
			this.tasks = this.storage.getTaskList();
			this.google.updateLastUpdated();
			return true;
		} catch (IOException | FatalException | HandledException e) {
			if (e instanceof IOException) {
				this.logger.writeErrLog(COMMIT_ERROR, e);
			}
			CommonUtil.printErrMsg(SYNC_FAIL);
			this.google = null;
			return false;
		}
	}
	
	private int syncFromGoogle(ArrayList<Task> googleList) throws HandledException, FatalException {
		assert(this.tasks != null);
		int count = 0;
		for (Task remoteTask:googleList) {
			Task localTask = this.getTaskByTask(remoteTask, this.tasks);
			if (localTask == null) {
				if (!remoteTask.isDeleted()) {
					this.storage.updateTask(remoteTask);
					count++;
				}
			} else if (localTask.getLastModified().before(remoteTask.getLastModified())) {
				remoteTask.updateCreated(localTask.getCreated());
				this.storage.updateTask(remoteTask);
				count++;
			}
		}
		return count;
	}
	
	private int syncToGoogle(ArrayList<Task> googleList) throws HandledException, FatalException{
		assert(this.tasks != null);
		int count = 0;
		for (Task localTask:this.tasks) {
			Task remoteTask = this.getTaskByTask(localTask, googleList);
			Task updating = null;
			if (remoteTask == null) {
				updating = this.commitSyncToGoogle(localTask, true);
				count++;
			} else {
				if (remoteTask.getLastModified().before(localTask.getLastModified())) {
					updating = this.commitSyncToGoogle(localTask, false);
					count++;
				}
			}
			this.updateUidInList(localTask, updating);
		}
		return count;
	}
	
	private Task commitSyncToGoogle(Task task, boolean newFlag) throws HandledException {
		assert(this.google != null);
		try {
			if (newFlag) {
				return this.google.addTask(task);
			} else {
				return this.google.updateTask(task);
			}
		} catch (IOException e) {
			this.logger.writeErrLog(COMMIT_ERROR, e);
			return null;
		}
	}
	
	private void updateUidInList(Task oldTask, Task newTask) throws HandledException, FatalException {
		if (newTask == null) return;
		if (oldTask.equals(newTask)) {
			this.storage.updateTask(newTask);
		} else {
			this.storage.deleteTask(oldTask);
			this.storage.updateTask(newTask);
		}
	}
	
	private ArrayList<Task> filterList(ArrayList<Task> taskList, boolean deleted) {
		ArrayList<Task> returnList = new ArrayList<Task>();
		for (Task task:taskList) {
			if (task.isDeleted() == deleted) {
				returnList.add(task);
			}
		}
		return returnList;
	}
	
	private Task getTaskByTask(Task task) {
		assert(this.tasks != null);
		return this.getTaskByTask(task, this.tasks);
	}
	
	private Task getTaskByTask(Task task, ArrayList<Task> taskList) {
		for (Task existingTask:taskList) {
			if (existingTask.equals(task)) {
				return existingTask;
			}
		}
		return null;
	}
}
