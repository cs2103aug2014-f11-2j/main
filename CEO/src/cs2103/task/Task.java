package cs2103.task; 

import java.math.BigInteger;
import java.security.SecureRandom;
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

/**
 * Containing methods for the inherited methods in 
 * 				concrete Task classes: DeadlineTask, FloatingTask, and PeriodicTask
 */

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
	
	
	/**
	 * Generated a String taskUID, unique to each Task created
	 */
	public Task(String taskUID) {
		if (taskUID == null){
			this.taskUID = this.generateUid();
		} else {
			this.taskUID = taskUID;
		}
		this.updateCreated(null);
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
	
	/**
	 * Adds property to component necessary for Google Sync
	 */
	protected void addCommonProperty(Component component){
		component.getProperties().add(new Uid(this.getTaskUID()));
		component.getProperties().add(new Created(this.getCreated()));
		component.getProperties().add(new LastModified(addLastModified()));
		component.getProperties().add(new Description(this.getDescription()));
	}

	private DateTime addLastModified() {
		if (this.getLastModified() == null) {
			return new DateTime();
		} else {
			return this.getLastModified();
		}
	}
	
	/**
	 * Generates the output format used by toSummary and toDetail for all Tasks 
	 */
	protected Ansi addCommonString(){
		Ansi returnString = ansi().fg(YELLOW).a(this.getTaskID()).a(". ").reset();
		returnString.bold().a(this.getTitle()).a('\n').boldOff().reset();
		if (this.isDeleted()) returnString.a(DELETED);
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
	
	/**
	 * Changes task updated based on the time
	 */
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
	/**
	 * Revives a task from delete
	 */
	public abstract void restore();
	/**
	 * Used to change one type of task to another type
	 * Called from an Update of the task
	 */
	protected abstract Task convert(Date[] time) throws HandledException;
	@Override
	public abstract Object clone() throws CloneNotSupportedException;
	public abstract Ansi toSummary();
	public abstract Ansi toDetail();
	/**
	 * Generates Google component necessary for Google Sync
	 */
	public abstract Component toComponent();
	public abstract DateTime getCompleted();
	public abstract boolean checkPeriod(Date[] time);
	/**
	 * Checks if keyword is contained anywhere within the task
	 */
	public abstract boolean matches(String keyword);
	
	public Status getStatus(){
		return this.status;
	}
	
	/**
	 * To check whether a Task is in the time frame eligible to be alerted to the user
	 */
	public boolean checkAlert() {
		Date[] time = new Date[2];
		time[0] = new Date();
		time[1] = new Date(time[0].getTime() + DAY_IN_MILLIS);
		return this.checkPeriod(time);
	}
}
