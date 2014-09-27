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
	public ArrayList<Task> taskList;
	private net.fortuna.ical4j.model.Calendar calendar;
	private IndexedComponentList indexedComponents;
	private final File file;
	UidGenerator ug;
	
	public StorageEngine(String configFile){
		CompatibilityHints.setHintEnabled(CompatibilityHints.KEY_RELAXED_VALIDATION,true);
		String fileName="default.ics";
		this.file = new File(fileName);
		try {
			if (!file.exists() || file.length()==0){
				createNewFile();
			}
			ug = new UidGenerator("test");
			read();
		} catch (CEOException | IOException e) {
			e.printStackTrace();
		}
	}
	
	public ArrayList<Task> getTaskList(){
		return taskList;
	}
	
	private void createNewFile() throws CEOException{
		this.calendar = new net.fortuna.ical4j.model.Calendar();
		this.calendar.getProperties().add(new ProdId("-//cs2103-f11-2j//CEO 0.0//EN"));
		this.calendar.getProperties().add(Version.VERSION_2_0);
		this.calendar.getProperties().add(CalScale.GREGORIAN);
		TimeZoneRegistry registry = TimeZoneRegistryFactory.getInstance().createRegistry();
		TimeZone timeZone = registry.getTimeZone(TimeZone.getDefault().getID());
		this.calendar.getComponents().add(timeZone.getVTimeZone());
		write();
	}
	
	@SuppressWarnings("unchecked") 
	private void read() throws CEOException{
		try{	
			FileInputStream fin = new FileInputStream(file);
			CalendarBuilder builder = new CalendarBuilder();
			this.calendar = builder.build(fin);
			this.taskList = new ArrayList<Task>();
			for (Iterator<VToDo> i = this.calendar.getComponents(Component.VTODO).iterator(); i.hasNext();){
				VToDo component = i.next();
				this.taskList.add(parseVToDo(component));
			}
			for (Iterator<VEvent> i = this.calendar.getComponents(Component.VEVENT).iterator(); i.hasNext();){
				VEvent component = i.next();
				this.taskList.add(parseVEvent(component));
			}
			indexedComponents = new IndexedComponentList(calendar.getComponents(), Property.UID);
			Collections.sort(this.taskList);
			int count=0;
			for (Task task:this.taskList){
				count++;
				task.updateTaskID(count);
			}
		}catch(IOException | ParserException | ParseException e){
			throw new CEOException("Read Error");
		}
	}

	private void write() throws CEOException{
		try {
			calendar.validate();
			FileOutputStream fout;
			fout = new FileOutputStream(file);
			CalendarOutputter outputter = new CalendarOutputter();
			outputter.output(this.calendar, fout);
		} catch (IOException | ValidationException e) {
			throw new CEOException("Write Error");
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
		write();
		read();
	}
	
	public void deleteTask(Task task) throws CEOException{
		Component existing = this.indexedComponents.getComponent(task.getTaskUID());
		if (existing == null){
			throw new CEOException("Task not exist");
		}else{
			calendar.getComponents().remove(existing);
		}
		write();
		read();
	}
	
	private Component taskToComponent(Task task) throws CEOException{
		if (task instanceof PeriodicTask){
			return periodicToComponent((PeriodicTask)task);
		}else if (task instanceof DeadlineTask){
			return deadlineToComponent((DeadlineTask)task);
		}else if (task instanceof FloatingTask){
			return floatingToComponent((FloatingTask)task);
		}else{
			throw new CEOException("Invalid task");
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
			throw new CEOException("Period error");
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
			task = new DeadlineTask(componentUID, componentTitle, component.getDue().getDate());
		}
		task.updateDescription(component.getDescription()==null?"":component.getDescription().getValue());
		task.updateLocation(component.getLocation()==null?"":component.getLocation().getValue());
		return task;
	}
	
	public String readTitle(Component component) throws CEOException{
		if (component.getProperty(Property.SUMMARY)!=null){
			return component.getProperty(Property.SUMMARY).getValue();
		}else{
			throw new CEOException("No title error");
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
	
	/*private int parseImportance(String componentImportance){
		if (componentImportance.matches("[0-9]+")){
			return Integer.parseInt(componentImportance);
		}else{
			return 0;
		}
	}*/
	
}
