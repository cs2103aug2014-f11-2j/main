//@author A0116713M
package cs2103.storage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

import cs2103.exception.FatalException;
import cs2103.exception.HandledException;
import cs2103.task.DeadlineTask;
import cs2103.task.FloatingTask;
import cs2103.task.PeriodicTask;
import cs2103.task.Task;
import cs2103.task.ToDoTask;
import cs2103.util.CommonUtil;
import cs2103.util.Logger;

import java.util.Collections;

import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.data.CalendarOutputter;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.IndexedComponentList;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.Recur;
import net.fortuna.ical4j.model.TimeZone;
import net.fortuna.ical4j.model.TimeZoneRegistry;
import net.fortuna.ical4j.model.TimeZoneRegistryFactory;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.component.VToDo;
import net.fortuna.ical4j.model.property.CalScale;
import net.fortuna.ical4j.model.property.Created;
import net.fortuna.ical4j.model.property.LastModified;
import net.fortuna.ical4j.model.property.ProdId;
import net.fortuna.ical4j.model.property.RRule;
import net.fortuna.ical4j.model.property.Version;
import net.fortuna.ical4j.model.ValidationException;
import net.fortuna.ical4j.util.CompatibilityHints;

/**
 * This class implements StorageInterface
 * Using iCal4j to read and store iCalendar file complying RFC 2445 iCalendar specification
 */
public class StorageEngine implements StorageInterface{
	private net.fortuna.ical4j.model.Calendar calendar;
	private IndexedComponentList indexedComponents;
	private final File file;
	private final Logger logger;
	private static final String LOG_INITIALIZE = "Initializing StorageEngine";
	private static final String LOG_NEWFILE = "Creating new storage file";
	private static final String LOG_ADD = "Adding task with UID %1$s to file";
	private static final String LOG_UPDATE = "Updating task with UID %1$s to file";
	private static final String LOG_REMOVE = "Removing task with UID %1$s from file";
	
	public StorageEngine(File file) throws HandledException, FatalException {
		this.logger = Logger.getInstance();
		this.logger.writeLog(LOG_INITIALIZE);
		if (file == null) {
			throw new FatalException(FatalException.ExceptionType.ILLEGAL_FILE);
		}
		CompatibilityHints.setHintEnabled(CompatibilityHints.KEY_RELAXED_VALIDATION, true);
		this.file = file;
		readFromFile();
	}
	
	private void createNewFile() throws HandledException, FatalException {
		this.logger.writeLog(LOG_NEWFILE);
		this.calendar = new net.fortuna.ical4j.model.Calendar();
		this.calendar.getProperties().add(new ProdId("-//cs2103-f11-2j//CEO 0.4//EN"));
		this.calendar.getProperties().add(Version.VERSION_2_0);
		this.calendar.getProperties().add(CalScale.GREGORIAN);
		TimeZoneRegistry registry = TimeZoneRegistryFactory.getInstance().createRegistry();
		TimeZone timeZone = registry.getTimeZone(TimeZone.getDefault().getID());
		this.calendar.getComponents().add(timeZone.getVTimeZone());
		writeToFile();
	}
	
	private ArrayList<Task> readFromFile() throws FatalException, HandledException {
		try{
			FileInputStream fin = new FileInputStream(this.file);
			CalendarBuilder builder = new CalendarBuilder();
			this.calendar = builder.build(fin);
			this.indexedComponents = new IndexedComponentList(this.calendar.getComponents(), Property.UID);
			return this.parseTaskList();
		} catch (FileNotFoundException e) {
			this.createNewFile();
			return this.readFromFile();
		} catch(IOException e) {
			throw new FatalException(FatalException.ExceptionType.READ_ERROR);
		} catch (ParseException | ParserException e) {
			throw new FatalException(FatalException.ExceptionType.ILLEGAL_FILE);
		}
	}
	
	@SuppressWarnings("unchecked") 
	private ArrayList<Task> parseTaskList() throws ParseException, HandledException, FatalException {
		assert(this.calendar != null);
		ArrayList<Task> taskList = new ArrayList<Task>();
		for (Iterator<VToDo> i = this.calendar.getComponents(Component.VTODO).iterator(); i.hasNext();) {
			VToDo component = i.next();
			taskList.add(parseVToDo(component));
		}
		for (Iterator<VEvent> i = this.calendar.getComponents(Component.VEVENT).iterator(); i.hasNext();) {
			VEvent component = i.next();
			taskList.add(parseVEvent(component));
		}
		return sortTaskList(taskList);
	}

	private void writeToFile() throws HandledException, FatalException {
		try {
			this.calendar.validate();
			FileOutputStream fout;
			fout = new FileOutputStream(this.file);
			CalendarOutputter outputter = new CalendarOutputter();
			outputter.output(this.calendar, fout);
		} catch (FileNotFoundException e) {
			this.createNewFile();
			this.writeToFile();
		} catch (IOException | ValidationException e) {
			throw new FatalException(FatalException.ExceptionType.WRITE_ERROR);
		}
	}
	
	/**
	 * Add the Task object into file if a Task with the same UID does not exist, update if it does
	 * @param task
	 * @throws HandledException
	 * @throws FatalException
	 */
	@Override
	public void updateTask(Task task) throws HandledException, FatalException {
		if (task != null){
			assert(this.indexedComponents != null);
			Component updating = task.toComponent();
			Component existing = this.indexedComponents.getComponent(task.getTaskUID());
			if (existing == null) {
				this.logger.writeLog(CommonUtil.formatLogString(LOG_ADD, task));
				this.calendar.getComponents().add(updating);
			} else {
				this.logger.writeLog(CommonUtil.formatLogString(LOG_UPDATE, task));
				this.calendar.getComponents().remove(existing);
				this.calendar.getComponents().add(updating);
			}
			writeToFile();
		}
	}
	
	/**
	 * Delete the Task from the file if it does exist
	 * @param task
	 * @throws HandledException
	 * @throws FatalException
	 */
	@Override
	public void deleteTask(Task task) throws HandledException, FatalException {
		if (task != null) {
			assert(this.indexedComponents != null);
			this.logger.writeLog(CommonUtil.formatLogString(LOG_REMOVE, task));
			Component existing = this.indexedComponents.getComponent(task.getTaskUID());
			if (existing == null) {
				throw new HandledException(HandledException.ExceptionType.TASK_NOT_EXIST);
			} else {
				this.calendar.getComponents().remove(existing);
			}
			writeToFile();
		}
	}

	/**
	 * @return The most up-to-date Task list
	 * @throws HandledException
	 * @throws FatalException
	 */
	@Override
	public ArrayList<Task> getTaskList() throws FatalException, HandledException {
		return this.readFromFile();
	}
	
	private Task parseVEvent(VEvent component) throws ParseException, FatalException, HandledException {
		assert(component != null);
		String componentUID = this.readUid(component);
		Date[] componentPeriod = this.readPeriod(component);
		PeriodicTask task = new PeriodicTask(componentUID, component.getStatus(), componentPeriod[0], componentPeriod[1]);
		task.updateTitle(this.readTitle(component));
		task.updateCreated(this.readCreated(component));
		task.updateLocation(this.readLocation(component));
		task.updateRecurrence(this.readRecur(component));
		task.updateDescription(this.readDescription(component));
		task.updateLastModified(this.readLastModified(component));
		return task;
	}
	
	private Task parseVToDo(VToDo component) throws ParseException, HandledException, FatalException {
		assert(component != null);
		ToDoTask task;
		String componentUID = this.readUid(component);
		if (component.getDue() == null) {
			task = new FloatingTask(componentUID, component.getStatus());
		}else{
			task = new DeadlineTask(componentUID, component.getStatus(), component.getDue().getDate());
		}
		task.updateTitle(this.readTitle(component));
		task.updateCreated(this.readCreated(component));
		task.updateCompleted(this.readCompleted(component));
		task.updateDescription(this.readDescription(component));
		task.updateLastModified(this.readLastModified(component));
		return task;
	}
	
	private Date[] readPeriod(VEvent component) throws FatalException {
		assert(component != null);
		if (component.getStartDate() == null || component.getEndDate() == null) {
			throw new FatalException(FatalException.ExceptionType.ILLEGAL_FILE);
		}else{
			Date[] period = new Date[2];
			period[0] = component.getStartDate().getDate();
			period[1] = component.getEndDate().getDate();
			return period;
		}
	}
	
	private Date readCreated(Component component) {
		assert(component != null);
		if (component.getProperty(Property.CREATED) == null) {
			return null;
		} else {
			return ((Created) component.getProperty(Property.CREATED)).getDateTime();
		}
	}
	
	private Date readLastModified(Component component) {
		assert(component != null);
		if (component.getProperty(Property.LAST_MODIFIED) == null) {
			return null;
		} else {
			return ((LastModified) component.getProperty(Property.LAST_MODIFIED)).getDateTime();
		}
	}
	
	private String readUid(Component component) {
		assert(component != null);
		if (component.getProperty(Property.UID) == null) {
			return null;
		}else{
			return component.getProperty(Property.UID).getValue();
		}
	}
	
	private String readTitle(Component component) {
		assert(component != null);
		if (component.getProperty(Property.SUMMARY) == null) {
			return "";
		}else{
			return component.getProperty(Property.SUMMARY).getValue();
		}
	}
	
	private Date readCompleted(VToDo component) {
		assert(component != null);
		if (component.getDateCompleted() == null) {
			return null;
		}else{
			return component.getDateCompleted().getDateTime();
		}
	}
	
	private Recur readRecur(VEvent component) {
		assert(component != null);
		if (component.getProperty(Property.RRULE) == null) {
			return null;
		}else{
			RRule rule = (RRule) component.getProperty(Property.RRULE);
			return rule.getRecur();
		}
	}
	
	private String readDescription(Component component) {
		assert(component != null);
		if (component.getProperty(Property.DESCRIPTION) == null) {
			return "";
		}else{
			return component.getProperty(Property.DESCRIPTION).getValue();
		}
	}
	
	private String readLocation(VEvent component) {
		assert(component != null);
		if (component.getLocation() == null) {
			return "";
		}else{
			return component.getLocation().getValue();
		}
	}
	
	private ArrayList<Task> sortTaskList(ArrayList<Task> taskList) {
		assert(taskList != null);
		Collections.sort(taskList);
		int count=0;
		for (Task task:taskList) {
			count++;
			task.updateTaskID(count);
		}
		return taskList;
	}
}
