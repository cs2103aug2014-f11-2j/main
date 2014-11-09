//@author A0128478R
package cs2103.task; 

import java.math.BigInteger;
import java.security.SecureRandom;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

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
	
	public Task(String taskUID) {
		if (taskUID == null){
			this.taskUID = this.generateUid();
		} else {
			this.taskUID = taskUID;
		}
		this.updateCreated(null);
	}
	
	/**
	 * Generated a String taskUID, unique to each Task created
	 */
	private String generateUid(){
		SecureRandom random = new SecureRandom();
		return new BigInteger(130, random).toString(32);
	}
	
	/**
	 * @return the temporary Task ID
	 */
	public int getTaskID(){
		return this.taskID;
	}
	
	/**
	 * @return the unique identifying String of the task object
	 */
	public String getTaskUID(){
		return this.taskUID;
	}
	
	/**
	 * @return the Title of the Task
	 */
	public String getTitle(){
		if (this.title == null){
			this.title = "";
		}
		return this.title;
	}
	
	/**
	 * @return the Description of the Task
	 */
	public String getDescription(){
		if (this.description == null){
			this.description = "";
		}
		return this.description;
	}
	
	/**
	 * @return the created time of the Task
	 */
	public DateTime getCreated(){
		return this.created;
	}
	
	/**
	 * @return the last modified time of the Task
	 */
	public DateTime getLastModified(){
		if (this.lastModified == null){
			this.updateLastModified(null);
		}
		return this.lastModified;
	}
	
	/**
	 * @param a temporary task id
	 */
	public void updateTaskID(int id){
		this.taskID = id;
	}
	
	/**
	 * @param title
	 */
	public void updateTitle(String title) {
		this.title = title;
	}
	
	/**
	 * @param description
	 */
	public void updateDescription(String description){
		this.description = description;
	}
	
	/**
	 * @param the last modified time
	 */
	public void updateLastModified(Date date){
		if (date == null){
			this.lastModified = new DateTime();
		} else {
			this.lastModified = new DateTime(date);
		}
	}
	
	/**
	 * @param the created time
	 */
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
	 * @param a Date array contains time information
	 * @return Updated task
	 * @throws HandledException
	 */
	public Task updateNewTask(Date[] time) throws HandledException{
		Task returnTask = this.convert(time);
		assert(returnTask != null);
		returnTask.updateLastModified(null);
		return returnTask;
	}
	
	/**
	 * @param time completed
	 */
	public abstract void updateCompleted(Date complete);
	/**
	 * @param location
	 */
	public abstract void updateLocation(String location);
	/**
	 * @param recurrence
	 */
	public abstract void updateRecurrence(Recur recurrence);
	/**
	 * @param status as in RFC 2445 iCalendar specification
	 */
	protected abstract void updateStatus(Status status);
	/**
	 * @return return whether this task is deleted
	 */
	public abstract boolean isDeleted();
	/**
	 * Indicate this Task is deleted
	 */
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
	/**
	 * @return Summary Ansi String to be printed
	 */
	public abstract Ansi toSummary();
	/**
	 * @return Detail Ansi String to be printed
	 */
	public abstract Ansi toDetail();
	/**
	 * @return convert to iCal4j component object to store
	 */
	public abstract Component toComponent();
	/**
	 * @return return the time the task is completed
	 */
	public abstract DateTime getCompleted();
	/**
	 * @param a Date array specify the time period for checking
	 * @return if this task is within the period
	 */
	public abstract boolean checkPeriod(Date[] time);
	/**
	 * Checks if keyword is contained anywhere within the task
	 */
	public abstract boolean matches(String keyword);
	
	/**
	 * @return status as in RFC 2445 iCalendar specification
	 */
	public Status getStatus(){
		return this.status;
	}

	protected Ansi dateToString(Date date){
		Ansi returnString = ansi().bold();
		DateFormat format = new SimpleDateFormat("yyyy/MM/dd hh:mm a", Locale.US);
		if (this.checkAlert()){
			returnString.fg(RED);
		} else {
			returnString.fg(GREEN);
		}
		returnString.a(format.format(date)).reset();
		return returnString;
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
