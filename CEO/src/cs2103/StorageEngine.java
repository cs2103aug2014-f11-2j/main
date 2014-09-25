package cs2103;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.TimeZone;

import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.data.CalendarOutputter;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.IndexedComponentList;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.property.DateProperty;
import net.fortuna.ical4j.model.ValidationException;
class StorageEngine {
	private ArrayList<Task> taskList;
	private net.fortuna.ical4j.model.Calendar calendar;
	private final static String TODO="VTODO";
	private final static String EVENT="VEVENT";
	private final static String TYPE_FLOATING="floating";
	private final static String TYPE_DEADLINE="deadline";
	private final static String TYPE_PERIODIC="periodic";
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
			for (Iterator<Component> i= calendar.getComponents().iterator(); i.hasNext();){
				Component component = i.next();
				String componentType = determineComponentType(component);
				String componentTitle = readTitle(component);
				String componentDescription = readProperty(component, Property.DESCRIPTION);
				String componentLocation = readProperty(component,Property.LOCATION);
				String componentCategory = readProperty(component,Property.CATEGORIES);
				String componentReccurence = readProperty(component,Property.RRULE);
				int componentImportance = parseImportance(readProperty(component,Property.PRIORITY));
				if (componentType.equals(TYPE_PERIODIC)){
					Date componentStartTime = stringToDate(readProperty(component, Property.DTSTART));
					Date componentEndTime = stringToDate(readProperty(component, Property.DTEND));
				}else if (componentType.equals(TYPE_DEADLINE)){
					Date componentDeadline = stringToDate(readProperty(component, Property.DUE));
				}else{
					
				}
			    for (Iterator<Property> j = component.getProperties().iterator(); j.hasNext();) {
			        Property property = (Property) j.next();
			        System.out.println("Property [" + property.getName() + ", " + property.getValue() + "]");
			    }
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
	public String readProperty(Component component, String propertyType){
		if (component.getProperty(propertyType)!=null){
			return component.getProperty(propertyType).getValue();
		}else{
			return "";
		}
	}
	public String readTitle(Component component) throws CEOException{
		if (component.getProperty(Property.SUMMARY)!=null){
			return component.getProperty(Property.SUMMARY).getValue();
		}else{
			throw new CEOException("No title error");
		}
	}
	public String determineComponentType(Component component) throws CEOException{
		String componentName = component.getName();
		if (componentName.equals(EVENT)){
			return TYPE_PERIODIC;
		}else if (componentName.equals(TODO)){
			if (component.getProperty(Property.DUE)==null){
				return TYPE_DEADLINE;
			}else{
				return TYPE_FLOATING;
			}
		}else{
			throw new CEOException("Invalid type error");
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
	
	private Date stringToDate(String timeString) throws ParseException{
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
	}
	
	private int parseImportance(String componentImportance){
		if (componentImportance.matches("[0-9]+")){
			return Integer.parseInt(componentImportance);
		}else{
			return 0;
		}
	}
}
