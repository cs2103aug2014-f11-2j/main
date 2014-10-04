package cs2103;

import java.util.Date;

import net.fortuna.ical4j.model.Recur;

class RecurringTask extends PeriodicTask {
	private Recur recurrence;
	public RecurringTask(String taskUID, String title, Date startTime, Date endTime, String location, Recur recurrence) throws CEOException {
		super(taskUID, title, startTime, endTime, location);
		updateRecurrence(recurrence);
	}
	
	public Recur getRecurrence(){
		return this.recurrence;
	}
	
	public void updateRecurrence(Recur recurrence) throws CEOException{
		if (recurrence == null){
			throw new CEOException(CEOException.INVALID_RECUR);
		}else{
			this.recurrence=recurrence;
		}
	}
	
}
