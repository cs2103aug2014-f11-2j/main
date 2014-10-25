package cs2103.storage;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.fortuna.ical4j.model.Recur;
import net.fortuna.ical4j.model.property.Status;
import net.fortuna.ical4j.model.property.Uid;

import com.google.api.services.calendar.model.CalendarList;
import com.google.api.services.calendar.model.CalendarListEntry;

import cs2103.exception.FatalException;
import cs2103.exception.HandledException;
import cs2103.task.*;
import cs2103.util.CommonUtil;

public class GoogleEngine implements StorageInterface {
	private final GoogleReceiver receiver;
	private final String calendarIdentifier;
	private com.google.api.services.calendar.Calendar calendar;
	private com.google.api.services.tasks.Tasks tasks;
	private static final long HOUR_IN_MILLIS = 3600000L;
	private static final String DEFAULT_TASKS = "@default";
	
	public GoogleEngine() throws HandledException{
		try {
			this.receiver = new GoogleReceiver();
			this.calendar = this.receiver.getCalendarClient();
			this.tasks = this.receiver.getTasksClient();
			this.calendarIdentifier = this.getIdentifier();
		} catch (IOException | GeneralSecurityException e) {
			throw new HandledException(HandledException.ExceptionType.LOGIN_FAIL);
		}
	}
	
	@Override
	public void deleteTask(Task task) throws HandledException, FatalException {
		try {
			tryToRemove(task);
		} catch (IOException e) {
			try {
				this.calendar = this.receiver.getCalendarClient();
				this.tasks = this.receiver.getTasksClient();
				tryToRemove(task);
			} catch (IOException e1) {
				throw new HandledException(HandledException.ExceptionType.SYNC_FAIL);
			}
		}
	}

	@Override
	public void updateTask(Task task) throws HandledException, FatalException {

	}

	@Override
	public void addTask(Task task) throws HandledException, FatalException {
		try {
			tryToInsert(task);
		} catch (IOException e) {
			try {
				this.calendar = this.receiver.getCalendarClient();
				this.tasks = this.receiver.getTasksClient();
				tryToInsert(task);
			} catch (IOException e1) {
				throw new HandledException(HandledException.ExceptionType.SYNC_FAIL);
			}
		}
	}

	@Override
	public ArrayList<Task> getTaskList() throws FatalException,	HandledException {
		ArrayList<Task> taskList = new ArrayList<Task>();
		for (com.google.api.services.calendar.model.Event gEvent:this.getEvents()){
			
			taskList.add(this.parseGEvent(gEvent));
		}
		for (com.google.api.services.tasks.model.Task gTask:this.getTasks()){
			if (!gTask.getDeleted()){
				taskList.add(this.parseGTask(gTask));
			}
		}
		return taskList;
	}
	
	private Task parseGTask(com.google.api.services.tasks.model.Task gTask) throws HandledException{
		CommonUtil.checkNull(gTask, HandledException.ExceptionType.INVALID_TASK_OBJ);
		Task task;
		if (gTask.getDue() == null){
			task = new FloatingTask(new Uid(gTask.getId()), new Date(), this.readStatus(gTask), gTask.getTitle(), this.readCompleted(gTask));
		} else {
			task = new DeadlineTask(new Uid(gTask.getId()), new Date(), this.readStatus(gTask), gTask.getTitle(), new Date(gTask.getDue().getValue()), this.readCompleted(gTask));
		}
		task.updateDescription(gTask.getNotes());
		task.updateLastModified(this.readLastModified(gTask));
		return task;
	}
	
	private Task parseGEvent(com.google.api.services.calendar.model.Event gEvent) throws HandledException{
		CommonUtil.checkNull(gEvent, HandledException.ExceptionType.INVALID_TASK_OBJ);
		Date[] time = this.readTime(gEvent);
		Task task = new PeriodicTask(new Uid(gEvent.getId()), this.readCreated(gEvent), this.readStatus(gEvent), gEvent.getSummary(), gEvent.getLocation(), time[0], time[1], this.readRecurrence(gEvent));
		task.updateDescription(gEvent.getDescription());
		task.updateLastModified(readLastModified(gEvent));
		return task;
	}
	
	private void tryToInsert(Task task) throws IOException{
		if (task instanceof PeriodicTask){
			this.calendar.events().insert(calendarIdentifier, ((PeriodicTask) task).toGEvent()).execute();
		} else if (task instanceof DeadlineTask){
			this.tasks.tasks().insert(DEFAULT_TASKS, ((DeadlineTask) task).toGTask()).execute();
		} else if (task instanceof FloatingTask){
			this.tasks.tasks().insert(DEFAULT_TASKS, ((FloatingTask) task).toGTask()).execute();
		}
	}
	
	private void tryToRemove(Task task) throws IOException{
		if (task instanceof PeriodicTask){
			this.calendar.events().delete(calendarIdentifier, task.getTaskUID().getValue());
		} else if (task instanceof DeadlineTask || task instanceof FloatingTask){
			this.tasks.tasks().delete(DEFAULT_TASKS, task.getTaskUID().getValue());
		}
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
			time[0] = new Date(gEvent.getStart().getDateTime().getValue());
			if (gEvent.getEnd() == null){
				time[1] = new Date(time[0].getTime() + HOUR_IN_MILLIS);
			} else {
				time[1] = new Date(gEvent.getStart().getDateTime().getValue());
			}
		}
		return time;
	}
	
	private Recur readRecurrence(com.google.api.services.calendar.model.Event gEvent){
		if (gEvent.getRecurrence() == null){
			return null;
		} else {
			String recurString = null;
			for (String s: gEvent.getRecurrence()){
				if (s.contains("RRULE")){
					recurString = s;
					break;
				}
			}
			if (recurString == null){
				return null;
			} else {
				try {
					return new Recur(recurString);
				} catch (ParseException e) {
					return null;
				}
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
		if (gTask.getDeleted()){
			return Status.VTODO_CANCELLED;
		} else if (gTask.getStatus() == null){
			return null;
		} else {
			return new Status(gTask.getStatus().toUpperCase());
		}
	}
	
	private List<com.google.api.services.tasks.model.Task> getTasks() throws HandledException{
		try {
			return this.tasks.tasks().list("@default").execute().getItems();
		} catch (IOException e) {
			try {
				this.tasks = this.receiver.getTasksClient();
				return this.tasks.tasks().list(DEFAULT_TASKS).execute().getItems();
			} catch (IOException e1) {
				throw new HandledException(HandledException.ExceptionType.SYNC_FAIL);
			}
		}
	}
	
	private List<com.google.api.services.calendar.model.Event> getEvents() throws HandledException{
		try {
			return this.calendar.events().list(calendarIdentifier).execute().getItems();
		} catch (IOException e) {
			try {
				this.calendar = this.receiver.getCalendarClient();
				return this.calendar.events().list(calendarIdentifier).setShowDeleted(true).execute().getItems();
			} catch (IOException e1) {
				throw new HandledException(HandledException.ExceptionType.SYNC_FAIL);
			}
		}
	}
	
	private String getIdentifier() throws HandledException{
		try {
			CalendarList feed = this.calendar.calendarList().list().execute();
			if (feed.getItems() != null) {
		    	for (CalendarListEntry entry : feed.getItems()) {
		    		if (entry.isPrimary()){
		    			return entry.getId();
		    		}
		    	}
		    }
		    throw new HandledException(HandledException.ExceptionType.SYNC_FAIL);
		} catch (IOException e) {
			throw new HandledException(HandledException.ExceptionType.SYNC_FAIL);
		}
	}
}
