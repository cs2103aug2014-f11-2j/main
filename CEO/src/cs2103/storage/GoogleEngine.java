package cs2103.storage;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;

import com.google.api.services.calendar.model.CalendarList;
import com.google.api.services.calendar.model.CalendarListEntry;

import cs2103.exception.FatalException;
import cs2103.exception.HandledException;
import cs2103.task.Task;

public class GoogleEngine implements StorageInterface {
	private final GoogleReceiver receiver;
	private final String calendarIdentifier;
	private com.google.api.services.calendar.Calendar calendar;
	private com.google.api.services.tasks.Tasks tasks;
	
	public GoogleEngine() throws HandledException{
		try {
			this.receiver = new GoogleReceiver();
			this.calendar = this.receiver.getCalendarClient();
			this.tasks = this.receiver.getTasksClient();
			this.calendarIdentifier = getIdentifier(this.calendar);
		} catch (IOException | GeneralSecurityException e) {
			throw new HandledException(HandledException.ExceptionType.LOGIN_FAIL);
		}
	}
	
	@Override
	public void deleteTask(Task task) throws HandledException, FatalException {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateTask(Task task) throws HandledException, FatalException {
		// TODO Auto-generated method stub

	}

	@Override
	public ArrayList<Task> getTaskList() throws FatalException,	HandledException {
		
		return null;
	}
	
	private Task parseTask(com.google.api.services.tasks.model.Task gTask){
		return null;
	}
	
	private List<com.google.api.services.tasks.model.Task> getTasks() throws HandledException{
		try {
			return this.tasks.tasks().list("@default").execute().getItems();
		} catch (IOException e) {
			try {
				this.tasks = this.receiver.getTasksClient();
				return this.tasks.tasks().list("@default").execute().getItems();
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
				return this.calendar.events().list(calendarIdentifier).execute().getItems();
			} catch (IOException e1) {
				throw new HandledException(HandledException.ExceptionType.SYNC_FAIL);
			}
		}
	}
	
	private static String getIdentifier(com.google.api.services.calendar.Calendar calendar) throws HandledException{
		try {
			CalendarList feed = calendar.calendarList().list().execute();
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
