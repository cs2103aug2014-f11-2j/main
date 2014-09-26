package cs2103;

import java.util.Date;

class DeadlineTask extends Task {
	private Date dueTime;
	public DeadlineTask(String taskUID, String title, Date dueTime) throws CEOException {
		super(taskUID, title);
		this.updateDueTime(dueTime);;
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
