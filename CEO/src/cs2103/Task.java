package cs2103; 

class Task {;
	/*private final static String TYPE_FLOATING="floating";
	private final static String TYPE_DEADLINE="deadline";
	private final static String TYPE_PERIODIC="periodic";*/
	private String taskUID;
	//private String type;
	private String title;
	private String description;
	private String location;

	public Task(String taskUID, String title) throws CEOException{
		if (title!=null){
			this.taskUID=taskUID;
			this.title=title;
		}else{
			throw new CEOException("No Title Error");
		}
	}
	
	public String getTaskUID(){
		return this.taskUID;
	}
	
	public String getTitle(){
		return this.title;
	}
	
	public String getDescription(){
		return this.description;
	}
	
	public String getLocation(){
		return this.location;
	}
	
	public void updateTaskUID(String taskUID) throws CEOException{
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
	
	public void updateLocation(String location){
		this.location=location;
	}
	

	
	
	
	
	
}
