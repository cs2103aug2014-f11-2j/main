package cs2103;

import java.util.Date;

class DeadlineTask extends FloatingTask {
	private Date dueTime;
	public DeadlineTask(String taskUID, String title, Date dueTime, boolean complete) throws CEOException {
		super(taskUID, title, complete);
		this.updateDueTime(dueTime);;
		this.updateComplete(complete);
	}
	
	public Date getDueTime(){
		return this.dueTime;
	}
	
	public void updateDueTime(Date dueTime) throws CEOException{
		if (dueTime==null){
			throw new CEOException("Invalid deadline");
		}else{
			this.dueTime=dueTime;
		}
	}

}
