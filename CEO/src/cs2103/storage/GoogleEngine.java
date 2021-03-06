//@author A0116713M
package cs2103.storage;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.ocpsoft.prettytime.nlp.PrettyTimeParser;

import net.fortuna.ical4j.model.Recur;
import net.fortuna.ical4j.model.property.Status;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.services.calendar.model.CalendarList;
import com.google.api.services.calendar.model.CalendarListEntry;

import cs2103.exception.HandledException;
import cs2103.task.*;
import cs2103.util.CommonUtil;
import cs2103.util.Logger;

/**
 * Read and write Task objects to Google Calendar and Google Tasks
 */
public class GoogleEngine {
	private static GoogleEngine storage;
	private final GoogleReceiver receiver;
	private final String calendarIdentifier;
	private final Logger logger;
	private com.google.api.services.calendar.Calendar calendar;
	private com.google.api.services.tasks.Tasks tasks;
	private Date taskLastUpdate;
	private Date calendarLastUpdate;
	private static final long DAY_IN_MILLIS = 86400000L;
	private static final int DUE_STRING_LENGTH = 8;
	private static final String DEFAULT_TASKS = "@default";
	private static final String LOG_ADD = "Adding task with UID %1$s to Google";
	private static final String LOG_UPDATE = "Updating task with UID %1$s to Google";
	private static final String LOG_REMOVE = "Removing task with UID %1$s from Google";
	private static final String LOG_INITIALIZE = "Initializing GoogleEngine";
	private static final String RRULE_PREFIX = "RRULE:";
	
	private GoogleEngine() throws HandledException {
		try {
			this.logger = Logger.getInstance();
			this.logger.writeLog(LOG_INITIALIZE);
			this.receiver = new GoogleReceiver();
			this.calendar = this.receiver.getCalendarClient();
			this.tasks = this.receiver.getTasksClient();
			this.calendarIdentifier = this.getIdentifier();
			this.updateLastUpdated();
		} catch (IOException | GeneralSecurityException e) {
			throw new HandledException(HandledException.ExceptionType.LOGIN_FAIL);
		}
	}
	
	/**
	 * @return The default instance of this class
	 * @throws HandledException
	 */
	public static GoogleEngine getInstance() throws HandledException {
		if (storage == null) {
			storage = new GoogleEngine();
		}
		return storage;
	}
	
	/**
	 * delete the task from Google Calendar or Google Tasks if it exists
	 * @param task
	 * @throws IOException
	 * @throws HandledException
	 */
	public void deleteTask(Task task) throws IOException, HandledException {
		CommonUtil.checkNull(task, HandledException.ExceptionType.INVALID_TASK_OBJ);
		this.logger.writeLog(CommonUtil.formatLogString(LOG_REMOVE, task));
		try {
			this.tryToRemove(task);
		} catch (IOException e) {
			try {
				this.reinitialize();
				this.tryToRemove(task);
			} catch (IOException e1) {
				throw e1;
			}
		}
	}
	
	/**
	 * Update the Task in Google Calendar or Google Tasks if it exist. 
	 * @param task
	 * @return Return the update result if available
	 * @throws IOException
	 * @throws HandledException
	 */
	public Task updateTask(Task task) throws IOException, HandledException {
		CommonUtil.checkNull(task, HandledException.ExceptionType.INVALID_TASK_OBJ);
		this.logger.writeLog(CommonUtil.formatLogString(LOG_UPDATE, task));
		try {
			return this.tryToUpdate(task);
		} catch (IOException e) {
			try {
				this.reinitialize();
				return this.tryToUpdate(task);
			} catch (IOException e1) {
				throw e1;
			}
		}
	}

	/**
	 * Insert the Task into either Google Calendar or Google Tasks. If the task exists, an IOExcpetion will be thrown
	 * @param task
	 * @throws HandledException
	 * @throws IOException
	 */
	public Task addTask(Task task) throws HandledException, IOException {
		CommonUtil.checkNull(task, HandledException.ExceptionType.INVALID_TASK_OBJ);
		this.logger.writeLog(CommonUtil.formatLogString(LOG_ADD, task));
		try {
			return this.tryToInsert(task);
		} catch (IOException e) {
			try {
				this.reinitialize();
				return this.tryToInsert(task);
			} catch (IOException e1) {
				throw e1;
			}
		}
	}

	/**
	 * @return Parse the Google Calendar and Google Tasks entries into Task objects and return an ArrayList of them
	 * @throws HandledException
	 */
	public ArrayList<Task> getTaskList() throws HandledException  {
		ArrayList<Task> taskList = new ArrayList<Task>();
		for (com.google.api.services.calendar.model.Event gEvent:this.getEvents()) {
			taskList.add(this.parseGEvent(gEvent));
		}
		for (com.google.api.services.tasks.model.Task gTask:this.getTasks()) {
			taskList.add(this.parseGTask(gTask));
		}
		return taskList;
	}
	
	/**
	 * update the stored last update time of Google Calendar and Google Tasks list
	 * @throws IOException
	 */
	public void updateLastUpdated() throws IOException {
		try {
			this.tryToGetLastUpdated();
		} catch (IOException e) {
			try {
				this.reinitialize();
				this.tryToGetLastUpdated();
			} catch (IOException e1) {
				throw e1;
			}
		}
	}
	
	/**
	 * @param task
	 * @return Return if a sync is necessary, i.e. whether the Google Calendar or Google Tasks list is modified since last sync
	 * @throws IOException
	 * @throws HandledException 
	 */
	public boolean needToSync(Task task) throws IOException, HandledException{
		CommonUtil.checkNull(task, HandledException.ExceptionType.INVALID_TASK_OBJ);
		assert(this.calendarLastUpdate != null);
		assert(this.taskLastUpdate != null);
		Date calendarLastUpdateSaved = this.calendarLastUpdate;
		Date taskLastUpdateSaved = this.taskLastUpdate;
		this.updateLastUpdated();
		if (task instanceof EventTask) {
			return this.calendarLastUpdate.after(calendarLastUpdateSaved);
		} else if (task instanceof ToDoTask) {
			return this.taskLastUpdate.after(taskLastUpdateSaved);
		} else {
			return false;
		}
	}
	
	private Task parseGTask(com.google.api.services.tasks.model.Task gTask) throws HandledException {
		CommonUtil.checkNull(gTask, HandledException.ExceptionType.INVALID_TASK_OBJ);
		String[] descriptionAndDueTime = this.splitDescriptionAndDueTime(gTask); //Work around for Google Tasks storing only date but not time
		String description = descriptionAndDueTime[0];
		Date dueTime = this.readDueTime(gTask.getDue(), descriptionAndDueTime[1]); //Adjust according to time stored in description
		ToDoTask task;
		if (dueTime == null) {
			task = new FloatingTask(gTask.getId(), this.readStatus(gTask));
		} else {
			task = new DeadlineTask(gTask.getId(), this.readStatus(gTask), dueTime);
		}
		assert(task != null);
		task.updateTitle(gTask.getTitle());
		task.updateCreated(this.readLastModified(gTask));
		task.updateCompleted(this.readCompleted(gTask));
		task.updateDescription(description);
		task.updateLastModified(this.readLastModified(gTask));
		return task;
	}
	
	private Task parseGEvent(com.google.api.services.calendar.model.Event gEvent) throws HandledException {
		CommonUtil.checkNull(gEvent, HandledException.ExceptionType.INVALID_TASK_OBJ);
		Date[] time = this.readTime(gEvent);
		assert(time != null);
		PeriodicTask task = new PeriodicTask(gEvent.getId(), this.readStatus(gEvent), time[0], time[1]);
		assert(task != null);
		task.updateTitle(gEvent.getSummary());
		task.updateCreated(this.readCreated(gEvent));
		task.updateLocation(gEvent.getLocation());
		task.updateRecurrence(this.readRecurrence(gEvent));
		task.updateDescription(gEvent.getDescription());
		task.updateLastModified(this.readLastModified(gEvent));
		return task;
	}
	
	private Task tryToInsert(Task task) throws IOException, HandledException {
		assert(task != null);
		if (task.isDeleted()) {
			return null;
		} else {
			Task returnTask = this.executeInsert(task);
			assert(task.getCreated() != null);
			returnTask.updateCreated(task.getCreated());
			return returnTask;
		}
	}
	
	private Task executeInsert(Task task) throws HandledException, IOException {
		if (task instanceof EventTask) {
			com.google.api.services.calendar.model.Event insertEvent = ((EventTask) task).toGEvent();
			return parseGEvent(this.calendar.events().insert(calendarIdentifier, insertEvent).execute());
		} else if (task instanceof ToDoTask) {
			com.google.api.services.tasks.model.Task insertTask = ((ToDoTask) task).toGTask();
			return parseGTask(this.tasks.tasks().insert(DEFAULT_TASKS, insertTask).execute());
		} else {
			throw new HandledException(HandledException.ExceptionType.INVALID_TASK_OBJ);
		}
	}
	
	private void tryToRemove(Task task) throws IOException {
		assert(task != null);
		if (task instanceof EventTask){
			if (!this.checkRemoved((EventTask) task)) {
				this.calendar.events().delete(calendarIdentifier, task.getTaskUID()).execute();
			}
		} else if (task instanceof ToDoTask) {
			if (!this.checkRemoved((ToDoTask) task)){
				this.tasks.tasks().delete(DEFAULT_TASKS, task.getTaskUID()).execute();
			}
		}
	}
	
	private boolean checkRemoved(EventTask task) throws IOException {
		assert(task != null);
		try {
			com.google.api.services.calendar.model.Event gEvent = this.calendar.events().get(calendarIdentifier, task.getTaskUID()).execute();
			return gEvent == null || gEvent.getStatus().equals("cancelled");
		} catch (GoogleJsonResponseException e) {
			return false;
		}
	}
	
	private boolean checkRemoved(ToDoTask task) throws IOException {
		assert(task != null);
		try {
			com.google.api.services.tasks.model.Task gTask = this.tasks.tasks().get(DEFAULT_TASKS, task.getTaskUID()).execute();
			return gTask == null || (gTask.getDeleted() != null && gTask.getDeleted());
		} catch (GoogleJsonResponseException e) {
			return false;
		}
	}
	
	private Task tryToUpdate(Task task) throws IOException, HandledException {
		assert(task != null);
		if (task.isDeleted()){
			this.tryToRemove(task);
			return null;
		} else {
			Task returnTask = this.executeUpdate(task);
			assert(task.getCreated() != null);
			returnTask.updateCreated(task.getCreated());
			return returnTask;
		}
	}
	
	private Task executeUpdate(Task task) throws HandledException, IOException {
		if (task instanceof EventTask) {
			return this.executeUpdate((EventTask) task); 
		} else if (task instanceof ToDoTask) {
			return this.executeUpdate((ToDoTask) task);
		} else {
			throw new HandledException(HandledException.ExceptionType.INVALID_TASK_OBJ);
		}
	}
	
	private Task executeUpdate(EventTask task) throws IOException, HandledException {
		assert(task != null);
		if (this.checkConverting(task)) {
			this.tasks.tasks().delete(DEFAULT_TASKS, task.getTaskUID()).execute();
			return this.tryToInsert(task);
		} else {
			com.google.api.services.calendar.model.Event existing = this.calendar.events().get(calendarIdentifier, task.getTaskUID()).execute();
			if (existing == null) {
				return this.tryToInsert(task);
			} else {
				com.google.api.services.calendar.model.Event updating = task.toGEvent();
				updating.setSequence(this.readSequence(existing));
				return parseGEvent(this.calendar.events().patch(calendarIdentifier, existing.getId(), updating).execute());
			}
		}
	}
	
	private boolean checkConverting(EventTask task) throws IOException {
		assert(task != null);
		try {
			return this.tasks.tasks().get(DEFAULT_TASKS, task.getTaskUID()).execute() != null;
		} catch (GoogleJsonResponseException e) {
			return false;
		}
	}
	
	private Task executeUpdate(ToDoTask task) throws IOException, HandledException {
		assert(task != null);
		if (this.checkConverting(task)) {
			this.calendar.events().delete(calendarIdentifier, task.getTaskUID()).execute();
			return this.tryToInsert(task);
		} else {
			com.google.api.services.tasks.model.Task gTask = this.tasks.tasks().get(DEFAULT_TASKS, task.getTaskUID()).execute();
			if (gTask == null) {
				return this.tryToInsert(task);
			} else {
				com.google.api.services.tasks.model.Task newGTask = task.toGTask();
				newGTask.setDeleted(false);
				return parseGTask(this.tasks.tasks().patch(DEFAULT_TASKS, gTask.getId(), newGTask).execute());
			}
		}
	}
	
	private boolean checkConverting(ToDoTask task) throws IOException {
		assert(task != null);
		try {
			return this.calendar.events().get(calendarIdentifier, task.getTaskUID()).execute() != null;
		} catch (GoogleJsonResponseException e) {
			return false;
		}
	}

	private void tryToGetLastUpdated() throws IOException {
		this.calendarLastUpdate =  new Date(this.calendar.events().list(calendarIdentifier).setShowDeleted(true).execute().getUpdated().getValue());
		this.taskLastUpdate = new Date(this.tasks.tasklists().get(DEFAULT_TASKS).execute().getUpdated().getValue());
	}
	
	private int readSequence(com.google.api.services.calendar.model.Event gEvent) {
		assert(gEvent != null);
		if (gEvent.getSequence() == null) {
			return 2;
		} else {
			return gEvent.getSequence() + 1;
		}
	}
	
	private Date readCompleted(com.google.api.services.tasks.model.Task gTask) {
		assert(gTask != null);
		if (gTask.getCompleted() == null) {
			return null;
		} else {
			return new Date(gTask.getCompleted().getValue());
		}
	}
	
	private Date readLastModified(com.google.api.services.tasks.model.Task gTask) {
		assert(gTask != null);
		if (gTask.getUpdated() == null) {
			return new Date();
		} else {
			return new Date(gTask.getUpdated().getValue());
		}
	}
	
	private Date readLastModified(com.google.api.services.calendar.model.Event gEvent) {
		assert(gEvent != null);
		if (gEvent.getUpdated() == null) {
			return new Date();
		} else {
			return new Date(gEvent.getUpdated().getValue());
		}
	}
	
	private Date readCreated(com.google.api.services.calendar.model.Event gEvent) {
		assert(gEvent != null);
		if (gEvent.getCreated() == null) {
			return new Date();
		} else {
			return new Date(gEvent.getCreated().getValue());
		}
	}
	
	private Date[] readTime(com.google.api.services.calendar.model.Event gEvent) throws HandledException {
		assert(gEvent != null);
		Date[] time = new Date[2];
		CommonUtil.checkNull(gEvent.getStart(), HandledException.ExceptionType.INVALID_TIME);
		com.google.api.client.util.DateTime startTime = this.readEventDateTime(gEvent.getStart());
		CommonUtil.checkNull(startTime, HandledException.ExceptionType.INVALID_TIME);
		time[0] = new Date(startTime.getValue());
		com.google.api.client.util.DateTime endTime = this.readEventDateTime(gEvent.getEnd());
		if (endTime == null) {
			time[1] = new Date(time[0].getTime() + DAY_IN_MILLIS);
		} else {
			time[1] = new Date(endTime.getValue());
		}
		return time;
	}

	private com.google.api.client.util.DateTime readEventDateTime(com.google.api.services.calendar.model.EventDateTime edt) {
		if (edt == null) {
			return null;
		} else {
			if (edt.getDateTime() == null) {
				return edt.getDate();
			} else {
				return edt.getDateTime();
			}
		}
	}
	
	private Date readDueTime(com.google.api.client.util.DateTime dueTime, String supplement) {
		if (dueTime == null) {
			return null;
		} else if (supplement == null) {
			return this.getDefaultDueTime(dueTime.getValue());
		} else {
			return this.adjustDueTime(dueTime.getValue(), supplement);
		}
	}
	
	private Date getDefaultDueTime(long dueTime) {
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(dueTime);
		cal.set(Calendar.HOUR_OF_DAY, 23);
		cal.set(Calendar.MINUTE, 59);
		return new Date(cal.getTimeInMillis());
	}
	
	private Date adjustDueTime(long dueTime, String supplement) {
		Calendar adjusting = Calendar.getInstance();
		Calendar adjustment = this.parseSupplement(supplement);
		adjusting.setTimeInMillis(dueTime);
		if (adjustment != null) {
			adjusting.set(Calendar.HOUR_OF_DAY, adjustment.get(Calendar.HOUR_OF_DAY));
			adjusting.set(Calendar.MINUTE, adjustment.get(Calendar.MINUTE));
		}
		return new Date(adjusting.getTimeInMillis());
	}
	
	private Calendar parseSupplement(String supplement) {
		assert(supplement != null);
		java.util.List<Date> dates = new PrettyTimeParser().parse(supplement);
		if (!dates.isEmpty()){
			Calendar cal = Calendar.getInstance();
			cal.setTimeInMillis(dates.get(0).getTime());
			return cal;
		} else {
			return null;
		}
	}
	
	private Recur readRecurrence(com.google.api.services.calendar.model.Event gEvent) {
		assert(gEvent != null);
		if (gEvent.getRecurrence() == null) {
			return null;
		} else {
			String recurString = getRecurrenceString(gEvent.getRecurrence());
			return parseRecurrence(recurString);
		}
	}
	
	private String getRecurrenceString(List<String> recurrenceList) {
		assert(recurrenceList != null);
		for (String s: recurrenceList){
			if (s != null && s.startsWith(RRULE_PREFIX)) return s.substring(RRULE_PREFIX.length());
		}
		return null;
	}
	
	private static Recur parseRecurrence(String recurString) {
		if (recurString == null){
			return null;
		} else {
			try {
				Recur recur = new Recur(recurString);
				if (recur.getInterval() < 1) recur.setInterval(1);
				return recur;
			} catch (ParseException e) {
				return null;
			}
		}
	}
	
	private Status readStatus(com.google.api.services.calendar.model.Event gEvent) {
		assert(gEvent != null);
		if (gEvent.getStatus() == null) {
			return null;
		} else {
			return new Status(gEvent.getStatus().toUpperCase());
		}
	}
	
	private Status readStatus(com.google.api.services.tasks.model.Task gTask) {
		assert(gTask != null);
		if (gTask.getDeleted() != null && gTask.getDeleted()) {
			return Status.VTODO_CANCELLED;
		} else if (gTask.getStatus() == null) {
			return Status.VTODO_NEEDS_ACTION;
		} else if (gTask.getStatus().equalsIgnoreCase("COMPLETED")) {
			return Status.VTODO_COMPLETED;
		} else {
			return Status.VTODO_NEEDS_ACTION;
		}
	}
	
	private String[] splitDescriptionAndDueTime(com.google.api.services.tasks.model.Task gTask) {
		assert(gTask != null);
		String[] result = new String[2];
		String description = gTask.getNotes();
		if (description == null) {
			return result; 
		}
		Pattern p = Pattern.compile("(\n<Due At: \\d+:\\d+ \\w+>)");
		Matcher m = p.matcher(gTask.getNotes());
		if (m.find()){
			result[0] = description.substring(0, m.start()).trim();
			result[1] = description.substring(m.start() + DUE_STRING_LENGTH , m.end() - 1);
		} else {
			result[0] = description;
		}
		return result;
	}
	
	private List<com.google.api.services.tasks.model.Task> getTasks() throws HandledException {
		try {
			assert(this.tasks != null);
			return this.tasks.tasks().list(DEFAULT_TASKS).setShowDeleted(true).execute().getItems();
		} catch (IOException e) {
			try {
				this.tasks = this.receiver.getTasksClient();
				return this.tasks.tasks().list(DEFAULT_TASKS).setShowDeleted(true).execute().getItems();
			} catch (IOException e1) {
				this.logger.writeErrLog(e1.getMessage(), e1);
				throw new HandledException(HandledException.ExceptionType.SYNC_FAIL);
			}
		}
	}
	
	private List<com.google.api.services.calendar.model.Event> getEvents() throws HandledException {
		try {
			assert(this.calendar != null);
			return this.calendar.events().list(calendarIdentifier).setShowDeleted(true).execute().getItems();
		} catch (IOException e) {
			try {
				this.calendar = this.receiver.getCalendarClient();
				return this.calendar.events().list(calendarIdentifier).setShowDeleted(true).execute().getItems();
			} catch (IOException e1) {
				this.logger.writeErrLog(e1.getMessage(), e1);
				throw new HandledException(HandledException.ExceptionType.SYNC_FAIL);
			}
		}
	}
	
	private void reinitialize() throws IOException {
		this.calendar = this.receiver.getCalendarClient();
		this.tasks = this.receiver.getTasksClient();
	}
	
	private String getIdentifier() throws HandledException{
		try {
			CalendarList feed = this.calendar.calendarList().list().execute();
			CommonUtil.checkNull(feed, HandledException.ExceptionType.SYNC_FAIL);
		    for (CalendarListEntry entry : feed.getItems()) {
		    	if (entry.isPrimary()) return entry.getId();
		    }
		    throw new HandledException(HandledException.ExceptionType.SYNC_FAIL);
		} catch (IOException e) {
			this.logger.writeErrLog(e.getMessage(), e);
			throw new HandledException(HandledException.ExceptionType.SYNC_FAIL);
		}
	}
}
