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
import net.fortuna.ical4j.model.property.ProdId;
import net.fortuna.ical4j.model.property.RRule;
import net.fortuna.ical4j.model.property.Version;
import net.fortuna.ical4j.model.ValidationException;
import net.fortuna.ical4j.util.CompatibilityHints;

/**
 * @author Yuri
 * This class implements StorageInterface
 * Using iCal4j to read and store iCalendar file complying RFC 2445 iCalendar specification
 */
public class StorageEngine implements StorageInterface{
	private static StorageEngine storage;
	private net.fortuna.ical4j.model.Calendar calendar;
	private IndexedComponentList indexedComponents;
	private final File file;
	
	private StorageEngine(File file) throws HandledException, FatalException{
		if (file == null){
			throw new FatalException(FatalException.ExceptionType.ILLEGAL_FILE);
		}
		CompatibilityHints.setHintEnabled(CompatibilityHints.KEY_RELAXED_VALIDATION, true);
		this.file = file;
		readFromFile();
	}
	
	/**
	 * @param file
	 * @return Return the default instance of StorageEngine for accessing the content in given file
	 * @throws HandledException
	 * @throws FatalException
	 */
	public static StorageEngine getInstance(File file) throws HandledException, FatalException{
		if (storage == null){
			storage = new StorageEngine(file);
		}
		return storage;
	}
	
	private void createNewFile() throws HandledException, FatalException{
		this.calendar = new net.fortuna.ical4j.model.Calendar();
		this.calendar.getProperties().add(new ProdId("-//cs2103-f11-2j//CEO 0.4//EN"));
		this.calendar.getProperties().add(Version.VERSION_2_0);
		this.calendar.getProperties().add(CalScale.GREGORIAN);
		TimeZoneRegistry registry = TimeZoneRegistryFactory.getInstance().createRegistry();
		TimeZone timeZone = registry.getTimeZone(TimeZone.getDefault().getID());
		this.calendar.getComponents().add(timeZone.getVTimeZone());
		writeToFile();
	}
	
	@SuppressWarnings("unchecked") 
	private ArrayList<Task> readFromFile() throws FatalException, HandledException{
		try{
			if (!file.exists() || file.length() == 0) {
				this.file.delete();
				this.createNewFile();
			}
			FileInputStream fin = new FileInputStream(file);
			CalendarBuilder builder = new CalendarBuilder();
			this.calendar = builder.build(fin);
			ArrayList<Task> taskList = new ArrayList<Task>();
			for (Iterator<VToDo> i = this.calendar.getComponents(Component.VTODO).iterator(); i.hasNext();){
				VToDo component = i.next();
				taskList.add(parseVToDo(component));
			}
			for (Iterator<VEvent> i = this.calendar.getComponents(Component.VEVENT).iterator(); i.hasNext();){
				VEvent component = i.next();
				taskList.add(parseVEvent(component));
			}
			indexedComponents = new IndexedComponentList(calendar.getComponents(), Property.UID);
			return sortTaskList(taskList);
		} catch (FileNotFoundException e){
			this.createNewFile();
			return this.readFromFile();
		} catch(IOException e){
			throw new FatalException(FatalException.ExceptionType.READ_ERROR);
		} catch (ParseException | ParserException e) {
			throw new FatalException(FatalException.ExceptionType.ILLEGAL_FILE);
		}
	}

	private void writeToFile() throws HandledException, FatalException{
		try {
			calendar.validate();
			FileOutputStream fout;
			fout = new FileOutputStream(file);
			CalendarOutputter outputter = new CalendarOutputter();
			outputter.output(this.calendar, fout);
		} catch (FileNotFoundException e){
			this.createNewFile();
			this.writeToFile();
		} catch (IOException | ValidationException e) {
			throw new FatalException(FatalException.ExceptionType.WRITE_ERROR);
		}
	}
	
	/**
	 * @param Task
	 * @return Add the Task object into file if a Task with the same UID does not exist, update if it does
	 * @throws HandledException
	 * @throws FatalException
	 */
	@Override
	public void updateTask(Task task) throws HandledException, FatalException{
		if (task != null){
			Component updating = task.toComponent();
			Component existing = this.indexedComponents.getComponent(task.getTaskUID());
			if (existing == null){
				calendar.getComponents().add(updating);
			}else{
				calendar.getComponents().remove(existing);
				calendar.getComponents().add(updating);
			}
			writeToFile();
		}
	}
	
	/**
	 * @param Task
	 * @return Delete the Task from the file if it does exist
	 * @throws HandledException
	 * @throws FatalException
	 */
	@Override
	public void deleteTask(Task task) throws HandledException, FatalException{
		if (task != null){
			Component existing = this.indexedComponents.getComponent(task.getTaskUID());
			if (existing == null){
				throw new HandledException(HandledException.ExceptionType.TASK_NOT_EXIST);
			}else{
				calendar.getComponents().remove(existing);
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
	public ArrayList<Task> getTaskList() throws FatalException, HandledException{
		return this.readFromFile();
	}
	
	private Task parseVEvent(VEvent component) throws ParseException, FatalException, HandledException{
		String componentUID = this.readUid(component);
		Date componentCreated = this.readCreated(component);
		Date[] componentPeriod = this.readPeriod(component);
		PeriodicTask task = new PeriodicTask(componentUID, component.getStatus(), componentPeriod[0], componentPeriod[1]);
		task.updateCreated(componentCreated);
		task.updateTitle(this.readTitle(component));
		task.updateLocation(this.readLocation(component));
		task.updateRecurrence(this.readRecur(component));
		task.updateDescription(readDescription(component));
		task.updateLastModified(component.getLastModified() == null? new Date():component.getLastModified().getDateTime());
		return task;
	}
	
	private Task parseVToDo(VToDo component) throws ParseException, HandledException, FatalException{
		ToDoTask task;
		String componentUID = this.readUid(component);
		if (component.getDue() == null){
			task = new FloatingTask(componentUID, component.getStatus());
		}else{
			task = new DeadlineTask(componentUID, component.getStatus(), component.getDue().getDate());
		}
		task.updateTitle(this.readTitle(component));
		task.updateCreated(this.readCreated(component));
		task.updateDescription(readDescription(component));
		task.updateCompleted(this.readCompleted(component));
		task.updateLastModified(component.getLastModified() == null?null:component.getLastModified().getDateTime());
		return task;
	}
	
	private Date[] readPeriod(VEvent component) throws FatalException{
		if (component.getStartDate() == null || component.getEndDate() == null){
			throw new FatalException(FatalException.ExceptionType.ILLEGAL_FILE);
		}else{
			Date[] period = new Date[2];
			period[0] = component.getStartDate().getDate();
			period[1] = component.getEndDate().getDate();
			return period;
		}
	}
	
	private Date readCreated(Component component) {
		if (component.getProperty(Property.CREATED) == null){
			return null;
		} else {
			return ((Created) component.getProperty(Property.CREATED)).getDateTime();
		}
	}
	
	private String readUid(Component component){
		if (component.getProperty(Property.UID) == null){
			return null;
		}else{
			return component.getProperty(Property.UID).getValue();
		}
	}
	
	private String readTitle(Component component) {
		if (component.getProperty(Property.SUMMARY) == null){
			return "";
		}else{
			return component.getProperty(Property.SUMMARY).getValue();
		}
	}
	
	private Date readCompleted(VToDo component){
		if (component.getDateCompleted() == null){
			return null;
		}else{
			return component.getDateCompleted().getDateTime();
		}
	}
	
	private Recur readRecur(VEvent component){
		if (component.getProperty(Property.RRULE) == null){
			return null;
		}else{
			RRule rule = (RRule) component.getProperty(Property.RRULE);
			return rule.getRecur();
		}
	}
	
	private String readDescription(Component component){
		if (component.getProperty(Property.DESCRIPTION) == null){
			return "";
		}else{
			return component.getProperty(Property.DESCRIPTION).getValue();
		}
	}
	
	private String readLocation(VEvent component){
		if (component.getLocation() == null){
			return "";
		}else{
			return component.getLocation().getValue();
		}
	}
	
	private ArrayList<Task> sortTaskList(ArrayList<Task> taskList){
		Collections.sort(taskList);
		int count=0;
		for (Task task:taskList){
			count++;
			task.updateTaskID(count);
		}
		return taskList;
	}
}
