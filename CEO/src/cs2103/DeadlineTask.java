package cs2103;

import java.util.Date;

import net.fortuna.ical4j.model.Recur;
import net.fortuna.ical4j.model.property.Uid;

class DeadlineTask extends Task {
	private Date dueTime;
	private boolean complete;
	
	public DeadlineTask(Uid taskUID, String title, Date dueTime, boolean complete) throws HandledException {
		super(taskUID, title);
		this.updateDueTime(dueTime);;
		this.updateComplete(complete);
	}
	
	public boolean getComplete(){
		return this.complete;
	}
	
	public void updateComplete(boolean complete){
		this.complete=complete;
	}
	
	public Date getDueTime(){
		return this.dueTime;
	}
	
	public void updateDueTime(Date dueTime) throws HandledException{
		if (dueTime == null){
			throw new HandledException(HandledException.ExceptionType.INVALID_TIME);
		}else{
			this.dueTime=dueTime;
		}
	}

	@Override
	public FloatingTask toFloating() throws HandledException {
		FloatingTask newTask = new FloatingTask(this.getTaskUID(), this.getTitle(), false);
		newTask.updateDescription(this.getDescription());
		return newTask;
	}

	@Override
	public DeadlineTask toDeadline(Date dueTime) throws HandledException {
		DeadlineTask newTask = new DeadlineTask(this.getTaskUID(), this.getTitle(), dueTime, this.getComplete());
		newTask.updateDescription(this.getDescription());
		return newTask;
	}

	@Override
	public PeriodicTask toPeriodic(Date startTime, Date endTime) throws HandledException {
		PeriodicTask newTask = new PeriodicTask(this.getTaskUID(), this.getTitle(), null, startTime, endTime, null);
		newTask.updateDescription(this.getDescription());
		return newTask;
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		try {
			DeadlineTask newTask = new DeadlineTask(this.getTaskUID(), this.getTitle(), this.getDueTime(), this.getComplete());
			newTask.updateDescription(this.getDescription());
			return newTask;
		} catch (HandledException e) {
			throw new CloneNotSupportedException();
		}
	}

	@Override
	public void updateLocation(String location) {
		return;
	}

	@Override
	public void updateRecurrence(Recur recurrence) {
		return;
	}

}
