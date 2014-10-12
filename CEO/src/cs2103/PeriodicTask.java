package cs2103;

import java.util.Date;

import net.fortuna.ical4j.model.Recur;
import net.fortuna.ical4j.model.property.Uid;

public class PeriodicTask extends Task {
	private Date startTime;
	private Date endTime;
	private String location;
	private Recur recurrence;
	
	public PeriodicTask(Uid taskUID, String title, String location, Date startTime, Date endTime, Recur recurrence) throws CEOException {
		super(taskUID, title);
		this.updateTime(startTime, endTime);
		this.updateLocation(location);
		this.updateRecurrence(recurrence);
	}
	
	public Date getStartTime(){
		return this.startTime;
	}
	
	public Date getEndTime(){
		return this.endTime;
	}
	
	public void updateTime(Date startTime, Date endTime) throws CEOException{
		if (startTime == null || endTime == null){
			throw new CEOException(CEOException.INVALID_TIME);
		} else if (startTime.after(endTime)){
			throw new CEOException(CEOException.INVALID_TIME);
		} else {
			this.startTime=startTime;
			this.endTime=endTime;
		}
	}
	
	public String getLocation(){
		return this.location;
	}
	
	
	public void updateLocation(String location){
		this.location=location;
	}

	public Recur getRecurrence(){
		return this.recurrence;
	}
	
	public void updateRecurrence(Recur recurrence){
		this.recurrence=recurrence;
	}

	@Override
	public FloatingTask toFloating() throws CEOException {
		FloatingTask newTask = new FloatingTask(this.getTaskUID(), this.getTitle(), false);
		newTask.updateDescription(this.getDescription());
		return newTask;
	}

	@Override
	public DeadlineTask toDeadline(Date dueTime) throws CEOException {
		DeadlineTask newTask = new DeadlineTask(this.getTaskUID(), this.getTitle(), dueTime, false);
		newTask.updateDescription(this.getDescription());
		return newTask;
	}

	@Override
	public PeriodicTask toPeriodic(Date startTime, Date endTime) throws CEOException {
		PeriodicTask newTask = new PeriodicTask(this.getTaskUID(), this.getTitle(), this.getLocation(), startTime, endTime, this.getRecurrence());
		newTask.updateDescription(this.getDescription());
		return newTask;
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		try {
			PeriodicTask newTask = new PeriodicTask(this.getTaskUID(), this.getTitle(), this.getLocation(), this.getStartTime(), this.getEndTime(), this.getRecurrence());
			newTask.updateDescription(this.getDescription());
			return newTask;
		} catch (CEOException e) {
			throw new CloneNotSupportedException();
		}
		
	}

	@Override
	public void updateComplete(boolean complete) {
		return;
	}
	
}
