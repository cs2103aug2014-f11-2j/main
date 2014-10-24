package cs2103.task; 

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

import cs2103.exception.HandledException;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.Recur;
import net.fortuna.ical4j.model.property.Created;
import net.fortuna.ical4j.model.property.Description;
import net.fortuna.ical4j.model.property.LastModified;
import net.fortuna.ical4j.model.property.Uid;
import net.fortuna.ical4j.util.SimpleHostInfo;
import net.fortuna.ical4j.util.UidGenerator;

public abstract class Task implements Comparable<Task>, Cloneable{;
	private int taskID;
	private final Uid taskUID;
	private String title;
	private String description;
	private DateTime created;
	private DateTime lastModified;
	protected static final String STRING_TYPE = "Type: ";
	protected static final String STRING_DESCRIPTION = "Description: ";
	protected static final long DAY_IN_MILLIS = 86400000L;
	
	public Task(Uid taskUID, Date created, String title) throws HandledException{
		if (title == null) throw new HandledException(HandledException.ExceptionType.NO_TITLE);
		this.updateTitle(title);
		if (taskUID == null){
			this.taskUID = generateUid();
		} else {
			this.taskUID = taskUID;
		}
		if (created == null){
			this.created = new DateTime(new Date());
		} else {
			this.created = new DateTime(created);
		}
		this.lastModified = new DateTime(new Date());
	}
	
	public int getTaskID(){
		return this.taskID;
	}
	
	public Uid getTaskUID(){
		return this.taskUID;
	}
	
	public String getTitle(){
		return this.title;
	}
	
	public String getDescription(){
		if (this.description == null){
			this.description = "";
		}
		return this.description;
	}
	
	public DateTime getCreated(){
		return this.created;
	}
	
	public DateTime getLastModified(){
		return this.lastModified;
	}
	
	public void updateTaskID(int id){
		this.taskID=id;
	}
	
	public void updateTitle(String title) throws HandledException{
		if (title != null){
			if (title.isEmpty()){
				throw new HandledException(HandledException.ExceptionType.NO_TITLE);
			} else {
				this.title = title;
			}
		}
	}
	
	public void updateDescription(String description){
		if (description == null){
			this.description = "";
		} else {
			this.description = description;
		}
	}
	
	public void updateLastModified(Date date){
		if (date == null){
			this.lastModified = new DateTime(new Date());
		} else {
			this.lastModified = new DateTime(date);
		}
	}
	
	@Override
	public int compareTo(Task o) {
		return this.getCreated().compareTo(o.getCreated());
	}
	
	protected void addCommonProperty(Component component){
		component.getProperties().add(this.getTaskUID());
		component.getProperties().add(new Created(this.getCreated()));
		component.getProperties().add(new LastModified(this.getLastModified()));
		component.getProperties().add(new Description(this.getDescription()));
	}
	
	protected static String dateToString(Date date){
		DateFormat dateFormat;
		dateFormat = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM, Locale.UK);
		return dateFormat.format(date);
	}
	
	private static Uid generateUid() throws HandledException{
		try {
			UidGenerator ug = new UidGenerator(new SimpleHostInfo("gmail.com"), InetAddress.getLocalHost().getHostName().toString());
			return ug.generateUid();
		} catch (UnknownHostException e) {
			throw new HandledException(HandledException.ExceptionType.NETWORK_ERR);
		}
	}
	
	@Override
	public boolean equals(Object o){
		if (o == null) return false;
		if (o instanceof Task){
			return this.getTaskUID().getValue().equals(((Task) o).getTaskUID().getValue());
		} else {
			return false;
		}
	}
	
	public abstract void updateComplete(boolean complete);
	public abstract void updateLocation(String location);
	public abstract void updateRecurrence(Recur recurrence);
	public abstract Task convert(Date[] time) throws HandledException;
	@Override
	public abstract Object clone() throws CloneNotSupportedException;
	public abstract String toSummary();
	public abstract String toDetail();
	public abstract Component toComponent();
	public abstract boolean checkPeriod(Date[] time);
	public abstract boolean matches(String keyword);
	public boolean checkAlert() {
		Date[] time = new Date[2];
		time[0] = new Date();
		time[1] = new Date(time[0].getTime() + DAY_IN_MILLIS);
		return this.checkPeriod(time);
	}
}
