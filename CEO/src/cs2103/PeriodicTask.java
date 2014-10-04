package cs2103;

import java.util.Date;

//import net.fortuna.ical4j.model.Recur;

public class PeriodicTask extends Task {
	private Date startTime;
	private Date endTime;
	private String location;
	//private Recur recurrence;
	
	public PeriodicTask(String taskUID, String title, Date startTime, Date endTime, String location) throws CEOException {
		super(taskUID, title);
		this.updateTime(startTime, endTime);
		this.updateLocation(location);
	}
	public Date getStartTime(){
		return this.startTime;
	}
	
	public Date getEndTime(){
		return this.endTime;
	}
	
	public void updateTime(Date startTime, Date endTime) throws CEOException{
		if (startTime==null || endTime==null){
			throw new CEOException(CEOException.INVALID_TIME);
		}else if (startTime.after(endTime)){
			throw new CEOException(CEOException.INVALID_TIME);
		}else{
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

	
	/*public Recur getRecurrance(){
		return this.recurrence;
	}*/
	
	/*public void updateRecurrence(Recur recurrence){
		this.recurrence=recurrence;
	}*/
}
