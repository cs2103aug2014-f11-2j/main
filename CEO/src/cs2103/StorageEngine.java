package cs2103;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.data.CalendarOutputter;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.ValidationException;
class StorageEngine {
	private ArrayList<Task> taskList;
	private net.fortuna.ical4j.model.Calendar calendar;
	private final File file;
	public StorageEngine(String configFile){
		String fileName="default.ics";
		this.file=new File(fileName);
	}
	public ArrayList<Task> getTaskList(){
		return taskList;
	}
	public void read(){
		try {
			FileInputStream fin = new FileInputStream(file);
			CalendarBuilder builder = new CalendarBuilder();
			this.calendar = builder.build(fin);
			this.calendar.getComponents();
			
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
