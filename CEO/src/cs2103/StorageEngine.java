package cs2103;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.data.CalendarOutputter;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.IndexedComponentList;
import net.fortuna.ical4j.model.Property;
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
	public void read(){
		try {
			FileInputStream fin = new FileInputStream(file);
			CalendarBuilder builder = new CalendarBuilder();
			this.calendar = builder.build(fin);
			//IndexedComponentList indexedList= new IndexedComponentList(this.calendar.getComponents(), Property.UID);
			for (Iterator<Component> i= calendar.getComponents().iterator(); i.hasNext();){
				Component component = i.next();
				System.out.println(determineComponentType(component));
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
		}
	}
	public String determineComponentType(Component component){
		String componentName = component.getName();
		if (componentName.equals(TODO)){
			return TYPE_FLOATING;
		}else if (componentName.equals(EVENT)){
			String startTime=component.getProperty(Property.DTSTART).getValue();
			String endTime=component.getProperty(Property.DTEND).getValue();
			if (startTime.equals(endTime)){
				return TYPE_DEADLINE;
			}else{
				return TYPE_PERIODIC;
			}
		}else{
			return null;
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
}
