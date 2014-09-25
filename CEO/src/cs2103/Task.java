package cs2103;
import java.util.Date;  

import net.fortuna.ical4j.model.Recur;

class Task {;
	private final static String TYPE_FLOATING="floating";
	private final static String TYPE_DEADLINE="deadline";
	private final static String TYPE_PERIODIC="periodic";
	private final static String INCOMPLETE="incomplete";
	private final static String IN_PROCESS="in_process";
	private final static String COMPLETED="completed";
	private String taskUID;
	private String type;
	private String title;
	private String description;
	private String location;
	private String category;
	private Recur recurrence;
	private int importance;
	private String progress;
	private Date startTime;
	private Date endTime;

	
	public Task(String taskUID, String title, String description, String location, String category, Recur recurrence,  int importance, Date startTime, Date endTime) throws CEOException{
		if (title!=null){
			this.title=title;
			this.description=description;
			this.location=location;
			this.category=category;
			this.recurrence=recurrence;
			this.importance=importance;
			this.progress=INCOMPLETE;
			updateTime(startTime, endTime);
		}else{
			throw new CEOException("No Title Error");
		}
	}
	
	public String getTaskUID(){
		//get the TaskUID from storage component
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
	
	public String getCategory(){
		return this.category;
	}
	
	public Recur getRecurrance(){
		return this.recurrence;
	}
	
	public String getType(){
		return this.type;
	}
	
	public int getImportance(){
		return this.importance;
	}
	
	public String getProgress(){
		return this.progress;
	}

	public Date getStartTime(){
		return this.startTime;
	}
	
	public Date getEndTime(){
		return this.endTime;
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
	
	public void updateCategory(String category){
		this.category=category;
	}
	
	public void updateRecurrence(Recur recurrence){
		this.recurrence=recurrence;
	}
	
	public void updateImportance(int importance){
		this.importance=importance;
	}
	
	public void updateProgress(String progress) throws CEOException{
		if (progress.equals(INCOMPLETE) || progress.equals(IN_PROCESS) || progress.equals(COMPLETED)){
			this.progress=progress;
		}else{
			throw new CEOException("Invalid Progress");
		}
	}
	
	public void updateTime(Date startTime, Date endTime){
		this.startTime=startTime;
		this.endTime=endTime;
		if (startTime==null && endTime==null){
			this.type=TYPE_FLOATING;
		}else if (endTime==null){
			this.type=TYPE_DEADLINE;
		}else{
			this.type=TYPE_PERIODIC;
		}
	}
	
}
