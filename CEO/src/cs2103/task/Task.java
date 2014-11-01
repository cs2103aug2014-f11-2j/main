package cs2103.task; 

import java.math.BigInteger;
import java.security.SecureRandom;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.fusesource.jansi.Ansi;
import static org.fusesource.jansi.Ansi.*;
import static org.fusesource.jansi.Ansi.Color.*;
import cs2103.exception.HandledException;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.Recur;
import net.fortuna.ical4j.model.property.Created;
import net.fortuna.ical4j.model.property.Description;
import net.fortuna.ical4j.model.property.LastModified;
import net.fortuna.ical4j.model.property.Status;
import net.fortuna.ical4j.model.property.Uid;

public abstract class Task implements Comparable<Task>, Cloneable{;
	private int taskID;
	private final String taskUID;
	private String title;
	private String description;
	private DateTime created;
	private DateTime lastModified;
	protected Status status;
	protected static final String STRING_DESCRIPTION = "Description: ";
	protected static final long DAY_IN_MILLIS = 86400000L;
	protected static final Ansi DELETED = ansi().fg(MAGENTA).a("(Deleted Task)\n").reset();
	
	public Task(String taskUID) {
		if (taskUID == null){
			this.taskUID = this.generateUid();
		} else {
			this.taskUID = taskUID;
		}
	}
	
	private String generateUid(){
		SecureRandom random = new SecureRandom();
		return new BigInteger(130, random).toString(32);
	}
	
	public int getTaskID(){
		return this.taskID;
	}
	
	public String getTaskUID(){
		return this.taskUID;
	}
	
	public String getTitle(){
		if (this.title == null){
			this.title = "";
		}
		return this.title;
	}
	
	public String getDescription(){
		if (this.description == null){
			this.description = "";
		}
		return this.description;
	}
	
	public DateTime getCreated(){
		if (this.created == null){
			this.created = new DateTime();
		}
		return this.created;
	}
	
	public DateTime getLastModified(){
		if (this.lastModified == null){
			this.updateLastModified(null);
		}
		return this.lastModified;
	}
	
	public void updateTaskID(int id){
		this.taskID = id;
	}
	
	public void updateTitle(String title) {
		this.title = title;
	}
	
	public void updateDescription(String description){
		this.description = description;
	}
	
	public void updateLastModified(Date date){
		if (date == null){
			this.lastModified = new DateTime();
		} else {
			this.lastModified = new DateTime(date);
		}
	}
	
	public void updateCreated(Date date){
		if (date == null){
			this.created = new DateTime();
		} else {
			this.created = new DateTime(date);
		}
	}
	
	@Override
	public int compareTo(Task o) {
		return this.getCreated().compareTo(o.getCreated());
	}
	
	protected void addCommonProperty(Component component){
		component.getProperties().add(new Uid(this.getTaskUID()));
		component.getProperties().add(new Created(this.getCreated()));
		component.getProperties().add(new LastModified(this.getLastModified() == null?new DateTime():this.getLastModified()));
		component.getProperties().add(new Description(this.getDescription()));
	}
	
	protected Ansi addCommonString(){
		Ansi returnString = ansi().fg(YELLOW).a(this.getTaskID()).a(". ").reset();
		returnString.a(this.getTitle()).bold().a('\n').boldOff().reset();
		if (this.isDeleted()) returnString.a(DELETED);
		return returnString;
	}
	
	protected Ansi dateToString(Date date){
		Ansi returnString = ansi();
		DateFormat format = new SimpleDateFormat("yyyy/MM/dd hh:mm a");
		if (this.checkAlert()){
			returnString.fg(RED);
		} else {
			returnString.fg(GREEN);
		}
		returnString.a(format.format(date)).reset();
		return returnString;
	}
	
	@Override
	public boolean equals(Object o){
		if (o == null) return false;
		if (o instanceof Task){
			return this.getTaskUID().equals(((Task) o).getTaskUID());
		} else {
			return false;
		}
	}
	
	public Task update(Date[] time) throws HandledException{
		Task returnTask = this.convert(time);
		returnTask.updateLastModified(null);
		return returnTask;
	}
	
	public abstract void updateCompleted(Date complete);
	public abstract void updateLocation(String location);
	public abstract void updateRecurrence(Recur recurrence);
	protected abstract void updateStatus(Status status);
	public abstract boolean isDeleted();
	public abstract void delete();
	public abstract void restore();
	protected abstract Task convert(Date[] time) throws HandledException;
	@Override
	public abstract Object clone() throws CloneNotSupportedException;
	public abstract Ansi toSummary();
	public abstract Ansi toDetail();
	public abstract Component toComponent();
	public abstract DateTime getCompleted();
	public abstract boolean checkPeriod(Date[] time);
	public abstract boolean matches(String keyword);
	
	public Status getStatus(){
		return this.status;
	}
	
	public boolean checkAlert() {
		Date[] time = new Date[2];
		time[0] = new Date();
		time[1] = new Date(time[0].getTime() + DAY_IN_MILLIS);
		return this.checkPeriod(time);
	}
}
