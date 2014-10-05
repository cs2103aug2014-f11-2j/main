package cs2103;

import java.util.Date;

class DeadlineTask extends Task {
	private Date dueTime;
	private boolean complete;
	public DeadlineTask(String taskUID, String title, Date dueTime, boolean complete) throws CEOException {
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
	
	public void updateDueTime(Date dueTime) throws CEOException{
		if (dueTime==null){
			throw new CEOException("Invalid deadline");
		}else{
			this.dueTime=dueTime;
		}
	}

}
