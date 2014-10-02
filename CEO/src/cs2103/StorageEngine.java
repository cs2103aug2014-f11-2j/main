package cs2103;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

import edu.emory.mathcs.backport.java.util.Collections;
import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.data.CalendarOutputter;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.IndexedComponentList;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.TimeZone;
import net.fortuna.ical4j.model.TimeZoneRegistry;
import net.fortuna.ical4j.model.TimeZoneRegistryFactory;
//import net.fortuna.ical4j.model.Recur;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.component.VToDo;
import net.fortuna.ical4j.model.property.CalScale;
import net.fortuna.ical4j.model.property.Description;
import net.fortuna.ical4j.model.property.Location;
import net.fortuna.ical4j.model.property.ProdId;
import net.fortuna.ical4j.model.property.Status;
import net.fortuna.ical4j.model.property.Uid;
import net.fortuna.ical4j.model.property.Version;
//import net.fortuna.ical4j.model.property.RRule;
import net.fortuna.ical4j.model.ValidationException;
import net.fortuna.ical4j.util.CompatibilityHints;
import net.fortuna.ical4j.util.UidGenerator;

class StorageEngine {
	private ArrayList<Task> taskList;
	private net.fortuna.ical4j.model.Calendar calendar;
	private IndexedComponentList indexedComponents;
	private final File file;
	UidGenerator ug;
	
	public StorageEngine(String dataFile){
		CompatibilityHints.setHintEnabled(CompatibilityHints.KEY_RELAXED_VALIDATION,true);
		if (dataFile==null){
			dataFile="default.ics";
		}
		this.file = new File(dataFile);
		try {
			if (!file.exists() || file.length()==0){
				createNewFile();
			}
			ug = new UidGenerator("test");
			this.taskList = read();
		} catch (CEOException | IOException e) {
			e.printStackTrace();
		}
	}
	
	public ArrayList<Task> getTaskList(){
		return this.taskList;
	}
	
	public Task getTaskByID(int taskID) throws CEOException{
		if (taskID > this.taskList.size() || taskID < 1){
			throw new CEOException(CEOException.INVALID_TASKID);
		}else{
			return this.taskList.get(taskID-1);
		}
	}
	
	private void createNewFile() throws CEOException{
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
	private ArrayList<Task> read() throws CEOException{
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
			Collections.sort(taskList);
			int count=0;
			for (Task task:taskList){
				count++;
				task.updateTaskID(count);
			}
			return taskList;
		}catch(IOException e){
			throw new CEOException(CEOException.READ_ERROR);
		} catch (ParseException | ParserException e) {
			throw new CEOException(CEOException.ILLEGAL_FILE);
		}
	}

	private void writeToFile() throws CEOException{
		try {
			calendar.validate();
			FileOutputStream fout;
			fout = new FileOutputStream(file);
			CalendarOutputter outputter = new CalendarOutputter();
			outputter.output(this.calendar, fout);
		} catch (IOException | ValidationException e) {
			throw new CEOException(CEOException.WRITE_ERROR);
		}
	}
	
	public void updateTask(Task task) throws CEOException{
		Component updating = taskToComponent(task);
		Component existing = this.indexedComponents.getComponent(updating.getProperty(Property.UID).getValue());
		if (existing == null){
			calendar.getComponents().add(updating);
		}else{
			calendar.getComponents().remove(existing);
			calendar.getComponents().add(updating);
		}
		syncWithFile();
	}
	
	public void deleteTask(Task task) throws CEOException{
		Component existing = this.indexedComponents.getComponent(task.getTaskUID());
		if (existing == null){
			throw new CEOException(CEOException.TASK_NOT_EXIST);
		}else{
			calendar.getComponents().remove(existing);
		}
		syncWithFile();
	}
	
	private void syncWithFile() throws CEOException{
		writeToFile();
		this.taskList = read();
	}
	
	private Component taskToComponent(Task task) throws CEOException{
		if (task instanceof PeriodicTask){
			return periodicToComponent((PeriodicTask)task);
		}else if (task instanceof DeadlineTask){
			return deadlineToComponent((DeadlineTask)task);
		}else if (task instanceof FloatingTask){
			return floatingToComponent((FloatingTask)task);
		}else{
			throw new CEOException(CEOException.INVALID_TASK_OBJ);
		}
	}
	
	private Component floatingToComponent(FloatingTask task) {
		VToDo component = new VToDo(new net.fortuna.ical4j.model.DateTime(new Date()), task.getTitle());
		if (task.getTaskUID()==null){
			component.getProperties().add(ug.generateUid());
		}else{
			component.getProperties().add(new Uid(task.getTaskUID()));
		}
		component.getProperties().add(new Description(task.getDescription()));
		component.getProperties().add(new Location(task.getLocation()));
		component.getProperties().add(new Status(completeToStatus(task.getComplete())));
		return component;
	}
	
	private Component deadlineToComponent(DeadlineTask task) {
		VToDo component = new VToDo(new net.fortuna.ical4j.model.DateTime(task.getDueTime()), new net.fortuna.ical4j.model.DateTime(task.getDueTime()),task.getTitle());
		if (task.getTaskUID()==null){
			component.getProperties().add(ug.generateUid());
		}else{
			component.getProperties().add(new Uid(task.getTaskUID()));
		}
		component.getProperties().add(new Description(task.getDescription()));
		component.getProperties().add(new Location(task.getLocation()));
		component.getProperties().add(new Status(completeToStatus(task.getComplete())));
		return component;
	}
	
	private Component periodicToComponent(PeriodicTask task) {
		VEvent component = new VEvent(new net.fortuna.ical4j.model.DateTime(task.getStartTime()), new net.fortuna.ical4j.model.DateTime(task.getEndTime()),task.getTitle());
		if (task.getTaskUID()==null){
			component.getProperties().add(ug.generateUid());
		}else{
			component.getProperties().add(new Uid(task.getTaskUID()));
		}
		component.getProperties().add(new Description(task.getDescription()));
		component.getProperties().add(new Location(task.getLocation()));
		return component;
	}
	
	private Task parseVEvent(VEvent component) throws CEOException, ParseException{
		String componentUID = component.getUid().getValue();
		String componentTitle = readTitle(component);
		Date componentStartTime;
		Date componentEndTime;
		if (component.getStartDate()==null||component.getEndDate()==null){
			throw new CEOException(CEOException.ILLEGAL_FILE);
		}else{
			componentStartTime=component.getStartDate().getDate();
			componentEndTime=component.getEndDate().getDate();
		}
		Task task = new PeriodicTask(componentUID, componentTitle, componentStartTime, componentEndTime);
		task.updateDescription(component.getDescription()==null?"":component.getDescription().getValue());
		task.updateLocation(component.getLocation()==null?"":component.getLocation().getValue());
		//Recur componentReccurence = getRecur(component);
		return task;
	}
	
	private Task parseVToDo(VToDo component) throws CEOException, ParseException{
		String componentUID = component.getUid().getValue();
		String componentTitle = readTitle(component);
		Task task;
		if (component.getDue()==null){
			task = new FloatingTask(componentUID, componentTitle, component.getStatus()==null?false:statusToComplete(component.getStatus().getValue()));
		}else{
			task = new DeadlineTask(componentUID, componentTitle, component.getDue().getDate(),component.getStatus()==null?false:statusToComplete(component.getStatus().getValue()));
		}
		task.updateDescription(component.getDescription()==null?"":component.getDescription().getValue());
		task.updateLocation(component.getLocation()==null?"":component.getLocation().getValue());
		return task;
	}
	
	public String readTitle(Component component) throws CEOException{
		if (component.getProperty(Property.SUMMARY)!=null){
			return component.getProperty(Property.SUMMARY).getValue();
		}else{
			throw new CEOException(CEOException.ILLEGAL_FILE);
		}
	}
	
	public String completeToStatus(boolean complete){
		return complete?"COMPLETED":"NEEDS-ACTION";
	}
	
	public boolean statusToComplete(String status){
		if (status.equals("COMPLETED")){
			return true;
		}else{
			return false;
		}
	}
	
	/*private Recur getRecur(Component component){
		if (component.getProperty(Property.RRULE)==null){
			return null;
		}else{
			RRule rule = (RRule) component.getProperty(Property.RRULE);
			return rule.getRecur();
		}
	}*/
	
}
