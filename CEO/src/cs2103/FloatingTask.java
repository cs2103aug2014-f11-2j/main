package cs2103;

import java.util.Date;

import net.fortuna.ical4j.model.Recur;
import net.fortuna.ical4j.model.property.Uid;

class FloatingTask extends Task {
	private boolean complete;
	
	public FloatingTask(Uid taskUID, String title, boolean complete) throws HandledException{
		super(taskUID, title);
		this.updateComplete(complete);
	}
	
	public boolean getComplete(){
		return this.complete;
	}
	
	public void updateComplete(boolean complete){
		this.complete=complete;
	}

	@Override
	public FloatingTask toFloating() throws HandledException {
		try {
			return (FloatingTask) this.clone();
		} catch (CloneNotSupportedException e) {
			throw new HandledException(HandledException.ExceptionType.CLONE_FAILED);
		}
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
			FloatingTask newTask = new FloatingTask(this.getTaskUID(), this.getTitle(), this.getComplete());
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
