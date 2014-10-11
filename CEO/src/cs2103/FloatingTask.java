package cs2103;

import net.fortuna.ical4j.model.property.Uid;

class FloatingTask extends Task {
	private boolean complete;
	
	public FloatingTask(Uid taskUID, String title, boolean complete) throws CEOException{
		super(taskUID, title);
		this.updateComplete(complete);
	}
	
	public boolean getComplete(){
		return this.complete;
	}
	
	public void updateComplete(boolean complete){
		this.complete=complete;
	}
}
