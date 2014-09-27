package cs2103;

class FloatingTask extends Task {
	private boolean complete;
	
	public FloatingTask(String taskUID, String title, boolean complete) throws CEOException{
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
