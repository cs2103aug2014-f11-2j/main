package cs2103;

import java.util.Date;

//import net.fortuna.ical4j.model.Recur;

public class PeriodicTask extends Task {
	private Date startTime;
	private Date endTime;
	//private Recur recurrence;
	
	public PeriodicTask(String taskUID, String title, Date startTime, Date endTime) throws CEOException {
		super(taskUID, title);
		this.updateTime(startTime, endTime);
	}
	public Date getStartTime(){
		return this.startTime;
	}
	
	public Date getEndTime(){
		return this.endTime;
	}
	
	public void updateTime(Date startTime, Date endTime) throws CEOException{
		if (startTime==null || endTime==null){
			throw new CEOException("Invalid Period");
		}else if (startTime.after(endTime)){
			throw new CEOException("Invalid Period");
		}else{
			this.startTime=startTime;
			this.endTime=endTime;
		}
	}
	
	/*public Recur getRecurrance(){
		return this.recurrence;
	}*/
	
	/*public void updateRecurrence(Recur recurrence){
		this.recurrence=recurrence;
	}*/
}
