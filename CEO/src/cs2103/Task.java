package cs2103; 

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;

import net.fortuna.ical4j.model.Recur;
import net.fortuna.ical4j.model.property.Uid;
import net.fortuna.ical4j.util.SimpleHostInfo;
import net.fortuna.ical4j.util.UidGenerator;

abstract class Task implements Comparable<Task>, Cloneable{;
	private int taskID;
	private Uid taskUID;
	private String title;
	private String description;

	public Task(Uid taskUID, String title) throws CEOException{
		if (title == null || title.equals("")){
			throw new CEOException(CEOException.NO_TITLE);
		} else {
			if (taskUID == null){
				taskUID = generateUid();
			} else {
				this.taskUID=taskUID;
			}
			this.title=title;
		}
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
		return this.description;
	}
	
	public void updateTaskID(int id){
		this.taskID=id;
	}
	
	public void updateTitle(String title){
		this.title=title;
	}
	
	public void updateDescription(String description){
		if (description != null){
			this.description=description;
		}
	}

	@Override
	public int compareTo(Task o) {
		if (this.taskUID == null){
			return -1;
		}else{
			return this.taskUID.getValue().compareTo(o.taskUID.getValue());
		}
	}
	
	private static Uid generateUid() throws CEOException{
		try {
			UidGenerator ug = new UidGenerator(new SimpleHostInfo("gmail.com"), InetAddress.getLocalHost().getHostName().toString());
			return ug.generateUid();
		} catch (UnknownHostException e) {
			throw new CEOException(CEOException.UNEXPECTED_ERR);
		}
	}
	
	public abstract void updateComplete(boolean complete);
	public abstract void updateLocation(String location);
	public abstract void updateRecurrence(Recur recurrence);
	public abstract FloatingTask toFloating() throws CEOException;
	public abstract DeadlineTask toDeadline(Date dueTime) throws CEOException;
	public abstract PeriodicTask toPeriodic(Date startTime, Date endTime) throws CEOException;
	@Override
	public abstract Object clone() throws CloneNotSupportedException;
}
