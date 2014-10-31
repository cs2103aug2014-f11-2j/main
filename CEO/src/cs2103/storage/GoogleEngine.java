package cs2103.storage;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.fortuna.ical4j.model.Recur;
import net.fortuna.ical4j.model.property.Status;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.services.calendar.model.CalendarList;
import com.google.api.services.calendar.model.CalendarListEntry;

import cs2103.exception.ErrorLogging;
import cs2103.exception.HandledException;
import cs2103.task.*;
import cs2103.util.CommonUtil;

public class GoogleEngine{
	private static GoogleEngine storage;
	private final GoogleReceiver receiver;
	private final String calendarIdentifier;
	private com.google.api.services.calendar.Calendar calendar;
	private com.google.api.services.tasks.Tasks tasks;
	private Date taskLastUpdate;
	private Date calendarLastUpdate;
	private static final long HOUR_IN_MILLIS = 3600000L;
	private static final long DAY_IN_MILLIS = 86400000L;
	private static final String DEFAULT_TASKS = "@default";
	
	private GoogleEngine() throws HandledException{
		try {
			this.receiver = new GoogleReceiver();
			this.calendar = this.receiver.getCalendarClient();
			this.tasks = this.receiver.getTasksClient();
			this.calendarIdentifier = this.getIdentifier();
			this.updateLastUpdated();
		} catch (IOException | GeneralSecurityException e) {
			throw new HandledException(HandledException.ExceptionType.LOGIN_FAIL);
		}
	}
	
	public static GoogleEngine getInstance() throws HandledException{
		if (storage == null){
			storage = new GoogleEngine();
		}
		return storage;
	}
	
	public void deleteTask(Task task) throws IOException, HandledException {
		CommonUtil.checkNull(task, HandledException.ExceptionType.INVALID_TASK_OBJ);
		try {
			tryToRemove(task);
		} catch (IOException e) {
			try {
				this.calendar = this.receiver.getCalendarClient();
				this.tasks = this.receiver.getTasksClient();
				tryToRemove(task);
			} catch (IOException e1) {
				throw e1;
			}
		}
	}
	
	public Task updateTask(Task task) throws IOException, HandledException{
		CommonUtil.checkNull(task, HandledException.ExceptionType.INVALID_TASK_OBJ);
		try {
			return tryToUpdate(task);
		} catch (IOException e) {
			try {
				this.calendar = this.receiver.getCalendarClient();
				this.tasks = this.receiver.getTasksClient();
				return tryToUpdate(task);
			} catch (IOException e1) {
				throw e1;
			}
		}
	}

	public Task addTask(Task task) throws HandledException, IOException {
		CommonUtil.checkNull(task, HandledException.ExceptionType.INVALID_TASK_OBJ);
		try {
			return tryToInsert(task);
		} catch (IOException e) {
			try {
				this.calendar = this.receiver.getCalendarClient();
				this.tasks = this.receiver.getTasksClient();
				return tryToInsert(task);
			} catch (IOException e1) {
				throw e1;
			}
		}
	}

	public ArrayList<Task> getTaskList() throws HandledException  {
		ArrayList<Task> taskList = new ArrayList<Task>();
		for (com.google.api.services.calendar.model.Event gEvent:this.getEvents()){
			taskList.add(this.parseGEvent(gEvent));
		}
		for (com.google.api.services.tasks.model.Task gTask:this.getTasks()){
			taskList.add(this.parseGTask(gTask));
		}
		return taskList;
	}
	
	public void updateLastUpdated() throws IOException{
		try {
			this.tryToGetLastUpdated();
		} catch (IOException e) {
			try {
				this.calendar = this.receiver.getCalendarClient();
				this.tasks = this.receiver.getTasksClient();
				this.tryToGetLastUpdated();
			} catch (IOException e1) {
				throw e1;
			}
		}
	}
	
	public boolean needToSync(Task task) throws IOException{
		Date calendarLastUpdateSaved = this.calendarLastUpdate;
		Date taskLastUpdateSaved = this.taskLastUpdate;
		this.updateLastUpdated();
		if (task instanceof EventTask){
			return this.calendarLastUpdate.after(calendarLastUpdateSaved);
		} else if (task instanceof ToDoTask){
			return this.taskLastUpdate.after(taskLastUpdateSaved);
		} else {
			return false;
		}
	}
	
	private Task parseGTask(com.google.api.services.tasks.model.Task gTask) throws HandledException{
		CommonUtil.checkNull(gTask, HandledException.ExceptionType.INVALID_TASK_OBJ);
		Task task;
		if (gTask.getDue() == null){
			task = new FloatingTask(gTask.getId(), this.readLastModified(gTask), this.readStatus(gTask), gTask.getTitle(), this.readCompleted(gTask));
		} else {
			task = new DeadlineTask(gTask.getId(), this.readLastModified(gTask), this.readStatus(gTask), gTask.getTitle(), new Date(gTask.getDue().getValue()), this.readCompleted(gTask));
		}
		task.updateDescription(gTask.getNotes());
		task.updateLastModified(this.readLastModified(gTask));
		return task;
	}
	
	private Task parseGEvent(com.google.api.services.calendar.model.Event gEvent) throws HandledException{
		CommonUtil.checkNull(gEvent, HandledException.ExceptionType.INVALID_TASK_OBJ);
		Date[] time = this.readTime(gEvent);
		Task task = new PeriodicTask(gEvent.getId(), this.readCreated(gEvent), this.readStatus(gEvent), gEvent.getSummary(), gEvent.getLocation(), time[0], time[1], this.readRecurrence(gEvent));
		task.updateDescription(gEvent.getDescription());
		task.updateLastModified(readLastModified(gEvent));
		return task;
	}
	
	private Task tryToInsert(Task task) throws IOException, HandledException{
		if (!task.getStatus().equals(Status.VTODO_CANCELLED)){
			Task returnTask;
			if (task instanceof EventTask){
				returnTask = parseGEvent(this.calendar.events().insert(calendarIdentifier, ((EventTask) task).toGEvent()).execute());
			} else if (task instanceof ToDoTask){
				returnTask = parseGTask(this.tasks.tasks().insert(DEFAULT_TASKS, ((ToDoTask) task).toGTask()).execute());
			} else {
				return null;
			}
			returnTask.updateCreated(task.getCreated());
			return returnTask;
		}
		return null;
	}
	
	private void tryToRemove(Task task) throws IOException{
		if (task instanceof EventTask){
			if (this.calendar.events().get(calendarIdentifier, task.getTaskUID()).execute() != null){
				this.calendar.events().delete(calendarIdentifier, task.getTaskUID()).execute();
			}
		} else if (task instanceof ToDoTask){
			if (this.tasks.tasks().get(DEFAULT_TASKS, task.getTaskUID()).execute() != null){
				this.tasks.tasks().delete(DEFAULT_TASKS, task.getTaskUID()).execute();
			}
		}
	}
	
	private Task tryToUpdate(Task task) throws IOException, HandledException{
		if (task.isDeleted()){
			tryToRemove(task);
			return null;
		} else {
			Task returnTask;
			if (task instanceof EventTask){
				returnTask = this.executeUpdate((EventTask) task); 
			} else if (task instanceof ToDoTask){
				returnTask = this.executeUpdate((ToDoTask) task);
			} else {
				return null;
			}
			returnTask.updateCreated(task.getCreated());
			return returnTask;
		}
	}
	
	private Task executeUpdate(EventTask task) throws IOException, HandledException{
		boolean converting = false;
		try{
			converting = this.tasks.tasks().get(DEFAULT_TASKS, task.getTaskUID()).execute() != null;
		} catch (GoogleJsonResponseException e){
			converting = false;
		}
		if (converting){
			this.tasks.tasks().delete(DEFAULT_TASKS, task.getTaskUID()).execute();
			return this.tryToInsert(task);
		} else {
			com.google.api.services.calendar.model.Event existing = this.calendar.events().get(calendarIdentifier, task.getTaskUID()).execute();
			if (existing != null){
				com.google.api.services.calendar.model.Event updating = task.toGEvent();
				int sequence;
				if (existing.getSequence() == null){
					sequence = 1;
				} else {
					sequence = existing.getSequence();
				}
				updating.setSequence(sequence);
				return parseGEvent(this.calendar.events().patch(calendarIdentifier, task.getTaskUID(), updating).execute());
			} else {
				return this.tryToInsert(task);
			}
		}
	}
	
	private Task executeUpdate(ToDoTask task) throws IOException, HandledException{
		boolean converting = false;
		try{
			converting = this.calendar.events().get(calendarIdentifier, task.getTaskUID()).execute() != null;
		} catch (GoogleJsonResponseException e){
			converting = false;
		}
		if (converting){
			this.calendar.events().delete(calendarIdentifier, task.getTaskUID()).execute();
			return this.tryToInsert(task);
		} else {
			com.google.api.services.tasks.model.Task gTask = this.tasks.tasks().get(DEFAULT_TASKS, task.getTaskUID()).execute();
			if (gTask != null){
				com.google.api.services.tasks.model.Task newGTask = task.toGTask();
				newGTask.setDeleted(false);
				return parseGTask(this.tasks.tasks().patch(DEFAULT_TASKS, gTask.getId(), newGTask).execute());
			} else {
				return this.tryToInsert(task);
			}
		}
	}

	private void tryToGetLastUpdated() throws IOException{
		this.calendarLastUpdate =  new Date(this.calendar.events().list(calendarIdentifier).setShowDeleted(true).execute().getUpdated().getValue());
		this.taskLastUpdate = new Date(this.tasks.tasklists().get(DEFAULT_TASKS).execute().getUpdated().getValue());
	}
	
	private Date readCompleted(com.google.api.services.tasks.model.Task gTask){
		if (gTask.getCompleted() == null){
			return null;
		} else {
			return new Date(gTask.getCompleted().getValue());
		}
	}
	
	private Date readLastModified(com.google.api.services.tasks.model.Task gTask){
		if (gTask.getUpdated() == null){
			return new Date();
		} else {
			return new Date(gTask.getUpdated().getValue());
		}
	}
	
	private Date readLastModified(com.google.api.services.calendar.model.Event gEvent){
		if (gEvent.getUpdated() == null){
			return new Date();
		} else {
			return new Date(gEvent.getUpdated().getValue());
		}
	}
	
	private Date readCreated(com.google.api.services.calendar.model.Event gEvent){
		if (gEvent.getCreated() == null){
			return new Date();
		} else {
			return new Date(gEvent.getCreated().getValue());
		}
	}
	
	private Date[] readTime(com.google.api.services.calendar.model.Event gEvent) throws HandledException{
		Date[] time = new Date[2];
		if (gEvent.getStart() == null){
			throw new HandledException(HandledException.ExceptionType.INVALID_TIME);
		} else {
			if (gEvent.getStart().getDateTime() == null){
				if (gEvent.getStart().getDate() == null){
					throw new HandledException(HandledException.ExceptionType.INVALID_TIME);
				} else {
					time[0] = new Date(gEvent.getStart().getDate().getValue());
					if (gEvent.getEnd() == null){
						time[1] = new Date(time[0].getTime() + DAY_IN_MILLIS);
					} else {
						if (gEvent.getEnd().getDateTime() == null){
							if (gEvent.getEnd().getDate() == null){
								time[1] = new Date(time[0].getTime() + DAY_IN_MILLIS);
							} else {
								time[1] = new Date(gEvent.getEnd().getDate().getValue());
							}
						} else {
							time[1] = new Date(gEvent.getEnd().getDateTime().getValue());
						}
					}
				}
			} else {
				time[0] = new Date(gEvent.getStart().getDateTime().getValue());
				if (gEvent.getEnd() == null){
					time[1] = new Date(time[0].getTime() + HOUR_IN_MILLIS);
				} else {
					time[1] = new Date(gEvent.getStart().getDateTime().getValue());
				}
			}
		}
		return time;
	}
	
	private Recur readRecurrence(com.google.api.services.calendar.model.Event gEvent){
		if (gEvent.getRecurrence() == null){
			return null;
		} else {
			String recurString = getRecurrenceString(gEvent.getRecurrence());
			return parseRecurrence(recurString);
		}
	}
	
	private static String getRecurrenceString(List<String> recurrenceList){
		for (String s: recurrenceList){
			if (s != null && s.startsWith("RRULE:")) return s.substring(6);
		}
		return null;
	}
	
	private static Recur parseRecurrence(String recurString){
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
	
	private Status readStatus(com.google.api.services.calendar.model.Event gEvent){
		if (gEvent.getStatus() == null){
			return null;
		} else {
			return new Status(gEvent.getStatus().toUpperCase());
		}
	}
	
	private Status readStatus(com.google.api.services.tasks.model.Task gTask){
		if (gTask.getDeleted() != null && gTask.getDeleted()){
			return Status.VTODO_CANCELLED;
		} else if (gTask.getStatus() == null){
			return Status.VTODO_NEEDS_ACTION;
		} else if (gTask.getStatus().equalsIgnoreCase("COMPLETED")){
			return Status.VTODO_COMPLETED;
		} else {
			return Status.VTODO_NEEDS_ACTION;
		}
	}
	
	private List<com.google.api.services.tasks.model.Task> getTasks() throws HandledException{
		try {
			return this.tasks.tasks().list(DEFAULT_TASKS).setShowDeleted(true).execute().getItems();
		} catch (IOException e) {
			try {
				this.tasks = this.receiver.getTasksClient();
				return this.tasks.tasks().list(DEFAULT_TASKS).setShowDeleted(true).execute().getItems();
			} catch (IOException e1) {
				ErrorLogging.getInstance().writeToLog(e1.getMessage(), e1);
				throw new HandledException(HandledException.ExceptionType.SYNC_FAIL);
			}
		}
	}
	
	private List<com.google.api.services.calendar.model.Event> getEvents() throws HandledException{
		try {
			return this.calendar.events().list(calendarIdentifier).setShowDeleted(true).execute().getItems();
		} catch (IOException e) {
			try {
				this.calendar = this.receiver.getCalendarClient();
				return this.calendar.events().list(calendarIdentifier).setShowDeleted(true).execute().getItems();
			} catch (IOException e1) {
				ErrorLogging.getInstance().writeToLog(e1.getMessage(), e1);
				throw new HandledException(HandledException.ExceptionType.SYNC_FAIL);
			}
		}
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
			ErrorLogging.getInstance().writeToLog(e.getMessage(), e);
			throw new HandledException(HandledException.ExceptionType.SYNC_FAIL);
		}
	}
}
