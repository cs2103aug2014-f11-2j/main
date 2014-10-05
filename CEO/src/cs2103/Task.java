package cs2103; 

import net.fortuna.ical4j.model.property.Uid;

class Task implements Comparable<Task>{;
	private int taskID;
	private Uid taskUID;
	private String title;
	private String description;


	public Task(Uid taskUID, String title) throws CEOException{
		if (title!=null){
			this.taskUID=taskUID;
			this.title=title;
		}else{
			throw new CEOException("No Title Error");
		}
	}
	
	public int getTaskID(){
		return this.taskID;
	}
	
	public Uid getTaskUID(){
		return this.taskUID;
	}
	
	public String getTitle(){
		return this.title;
	}
	
	public String getDescription(){
		return this.description;
	}
	
	public void updateTaskID(int id){
		this.taskID=id;
	}
	
	public void updateTaskUID(Uid taskUID) throws CEOException{
		if (taskUID==null){
			throw new CEOException("Invalid UID");
		}else{
			this.taskUID=taskUID;
		}
	}
	
	public void updateTitle(String title){
		this.title=title;
	}
	
	public void updateDescription(String description){
		if (description!=null){
			this.description=description;
		}
	}

	@Override
	public int compareTo(Task o) {
		if (this.taskUID==null){
			return -1;
		}else{
			return this.taskUID.getValue().compareTo(o.taskUID.getValue());
		}
	}
}
