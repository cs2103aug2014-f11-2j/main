package cs2103;
import java.io.File;
import java.io.FileInputStream;
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
import edu.emory.mathcs.backport.java.util.Collections;
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
import net.fortuna.ical4j.model.property.ProdId;
import net.fortuna.ical4j.model.property.RRule;
import net.fortuna.ical4j.model.property.Status;
import net.fortuna.ical4j.model.property.Uid;
import net.fortuna.ical4j.model.property.Version;
import net.fortuna.ical4j.model.ValidationException;
import net.fortuna.ical4j.util.CompatibilityHints;

public class StorageEngine {
	private static StorageEngine storage;
	private net.fortuna.ical4j.model.Calendar calendar;
	private IndexedComponentList indexedComponents;
	private final File file;
	private ArrayList<Task> taskList;
	
	private StorageEngine(String dataFile) throws HandledException, FatalException{
		CompatibilityHints.setHintEnabled(CompatibilityHints.KEY_RELAXED_VALIDATION,true);
		if (dataFile == null){
			dataFile = "default.ics";
		}
		this.file = new File(dataFile);
		if (!file.exists() || file.length()==0){
			createNewFile();
		}
		this.taskList = readFromFile();
		storage = this;
	}
	
	public void validate() throws FatalException{
		try {
			calendar.validate();
		} catch (ValidationException e) {
			throw new FatalException(FatalException.ExceptionType.ILLEGAL_FILE);
		}
	}
	
	public static StorageEngine getInstance() throws HandledException, FatalException{
		if (storage == null){
			throw new FatalException(FatalException.ExceptionType.NOT_INITIALIZED);
		} else {
			return storage;
		}
	}
	
	public static void initialize(String dataFile) throws HandledException, FatalException{
		if (storage == null){
			storage = new StorageEngine(dataFile);
		}
	}
	
	public ArrayList<Task> getTaskList(){
		return this.taskList;
	}
	
	private void createNewFile() throws HandledException, FatalException{
		this.calendar = new net.fortuna.ical4j.model.Calendar();
		this.calendar.getProperties().add(new ProdId("-//cs2103-f11-2j//CEO 0.0//EN"));
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
		}catch(IOException e){
			throw new FatalException(FatalException.ExceptionType.READ_ERROR);
		} catch (ParseException | ParserException e) {
			throw new FatalException(FatalException.ExceptionType.ILLEGAL_FILE);
		}
	}

	private void writeToFile() throws HandledException, FatalException{
		try {
			this.validate();
			FileOutputStream fout;
			fout = new FileOutputStream(file);
			CalendarOutputter outputter = new CalendarOutputter();
			outputter.output(this.calendar, fout);
		} catch (IOException | ValidationException e) {
			throw new FatalException(FatalException.ExceptionType.WRITE_ERROR);
		}
	}
	
	public ArrayList<Task> updateTask(Task task) throws HandledException, FatalException{
		Component updating = task.toComponent();
		Component existing = this.indexedComponents.getComponent(task.getTaskUID().getValue());
		if (existing == null){
			calendar.getComponents().add(updating);
		}else{
			calendar.getComponents().remove(existing);
			calendar.getComponents().add(updating);
		}
		writeToFile();
		this.taskList = readFromFile();
		return readFromFile();
	}
	
	public ArrayList<Task> deleteTask(Task task) throws HandledException, FatalException{
		Component existing = this.indexedComponents.getComponent(task.getTaskUID().getValue());
		if (existing == null){
			throw new HandledException(HandledException.ExceptionType.TASK_NOT_EXIST);
		}else{
			calendar.getComponents().remove(existing);
		}
		writeToFile();
		this.taskList = readFromFile();
		return readFromFile();
	}
	
	private Task parseVEvent(VEvent component) throws ParseException, FatalException, HandledException{
		Task task;
		Uid componentUID = component.getUid();
		Date componentCreated = component.getCreated() == null? new Date():component.getCreated().getDateTime();
		String componentTitle = readTitle(component);
		Date componentStartTime;
		Date componentEndTime;
		if (component.getStartDate() == null || component.getEndDate() == null){
			throw new FatalException(FatalException.ExceptionType.ILLEGAL_FILE);
		}else{
			componentStartTime=component.getStartDate().getDate();
			componentEndTime=component.getEndDate().getDate();
		}
		String componentLocation = readLocation(component);
		Recur componentRecurrence = readRecur(component);
		task = new PeriodicTask(componentUID, componentCreated, componentTitle, componentLocation, componentStartTime, componentEndTime, componentRecurrence);
		task.updateDescription(readDescription(component));
		task.updateLastModified(component.getLastModified() == null? new Date():component.getLastModified().getDateTime());
		return task;
	}
	
	private Task parseVToDo(VToDo component) throws ParseException, HandledException, FatalException{
		Task task;
		Uid componentUID = component.getUid();
		Date componentCreated = component.getCreated() == null? new Date():component.getCreated().getDateTime();
		String componentTitle = readTitle(component);
		if (component.getDue() == null){
			task = new FloatingTask(componentUID, componentCreated, componentTitle, readStatusToComplete(component));
		}else{
			task = new DeadlineTask(componentUID, componentCreated, componentTitle, component.getDue().getDate(),readStatusToComplete(component));
		}
		task.updateDescription(readDescription(component));
		task.updateLastModified(component.getLastModified() == null? new Date():component.getLastModified().getDateTime());
		return task;
	}
	
	private String readTitle(Component component) throws FatalException{
		if (component.getProperty(Property.SUMMARY) == null){
			throw new FatalException(FatalException.ExceptionType.ILLEGAL_FILE);
		}else{
			return component.getProperty(Property.SUMMARY).getValue();
		}
	}
	
	private boolean readStatusToComplete(VToDo component){
		if (component.getStatus().equals(Status.VTODO_COMPLETED)){
			return true;
		}else{
			return false;
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
