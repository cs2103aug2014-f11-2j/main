package cs2103;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.TimeZone;

import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.data.CalendarOutputter;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.IndexedComponentList;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.DateTime;
//import net.fortuna.ical4j.model.Recur;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.component.VToDo;
//import net.fortuna.ical4j.model.property.RRule;
import net.fortuna.ical4j.model.ValidationException;
class StorageEngine {
	private ArrayList<Task> taskList;
	private net.fortuna.ical4j.model.Calendar calendar;
	private final static String INCOMPLETE="incomplete";
	private final static String IN_PROCESS="in_process";
	private final static String COMPLETED="completed";
	private final File file;
	public StorageEngine(String configFile){
		String fileName="default.ics";
		this.file=new File(fileName);
	}
	public ArrayList<Task> getTaskList(){
		return taskList;
	}
	@SuppressWarnings("unchecked")
	public void read() throws CEOException{
		try {
			FileInputStream fin = new FileInputStream(file);
			CalendarBuilder builder = new CalendarBuilder();
			this.calendar = builder.build(fin);
			//IndexedComponentList indexedList= new IndexedComponentList(this.calendar.getComponents(), Property.UID);
			taskList = new ArrayList<Task>();
			for (Iterator<VToDo> i= calendar.getComponents(Component.VTODO).iterator(); i.hasNext();){
				VToDo component = i.next();
				taskList.add(parseVToDo(component));
			}
			for (Iterator<VEvent> i= calendar.getComponents(Component.VEVENT).iterator(); i.hasNext();){
				VEvent component = i.next();
				taskList.add(parseVEvent(component));
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void write(){
		try {
			FileOutputStream fout = new FileOutputStream(file);
			CalendarOutputter outputter = new CalendarOutputter();
			outputter.output(this.calendar, fout);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ValidationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	/*private Date stringToDate(String timeString) throws ParseException{
		TimeZone tz;
		if (timeString.endsWith("Z")){
			tz=TimeZone.getTimeZone("UTC");
		}else{
			tz=TimeZone.getDefault();
		}
		timeString=timeString.replaceAll("\\D+","");
		SimpleDateFormat dateFormat;
		if (timeString.length()==14){
			dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
		}else{
			dateFormat = new SimpleDateFormat("yyyyMMdd");
		}
		dateFormat.setTimeZone(tz);
		return dateFormat.parse(timeString);
	}*/
	
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
			task = new FloatingTask(componentUID, componentTitle, parseProgress(component.getStatus()==null?"NEEDS-ACTION":component.getStatus().getValue()));
		}else{
			task = new DeadlineTask(componentUID, componentTitle, component.getDue().getDate());
		}
		task.updateDescription(component.getDescription()==null?"":component.getDescription().getValue());
		task.updateLocation(component.getLocation()==null?"":component.getLocation().getValue());
		return task;
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
	
	public String readTitle(Component component) throws CEOException{
		if (component.getProperty(Property.SUMMARY)!=null){
			return component.getProperty(Property.SUMMARY).getValue();
		}else{
			throw new CEOException("No title error");
		}
	}
	
	private String parseProgress(String componentProgress) throws CEOException{
		if (componentProgress.equals("NEEDS-ACTION")){
			return INCOMPLETE;
		}else if (componentProgress.equals("IN-PROCESS")){
			return IN_PROCESS;
		}else if (componentProgress.equals("COMPLETED")){
			return COMPLETED;
		}else{
			throw new CEOException("Invalid Progress");
		}
	}
}
