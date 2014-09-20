package cs2103;
import java.text.DateFormat;  
import java.util.Date;  
import java.util.GregorianCalendar;

class Task {
	private static enum Progress{
		INCOMPLETE, IN_PROGRESS, COMPLETED
	};
	private static enum Type{
		FLOATING, DEADLINE, PERIOD
	};
	private String taskUID;
	private String title;
	private String description;
	private String location;
	private String category;
	private String recurrence;
	private int importance;
	private Progress progress;
	private Type type;
	private Date start;
	private Date end;
	
	public Task(String title, String description, String location, String category, String recurrence,  int importance, String startTime, String endTime) throws CEOException{
		if (title!=null){
			this.title=title;
			this.description=description;
			this.location=location;
			this.category=category;
			this.recurrence=recurrence;
			this.importance=importance;
			this.progress=Progress.INCOMPLETE;
			if (startTime==null && endTime==null){
				this.type=Type.FLOATING;
			}else if (endTime==null){
				this.type=Type.DEADLINE;
				//Parse time
			}else{
				this.type=Type.PERIOD;
				//Parse time
			}
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
	
	public String getRecurrance(){
		return this.recurrence;
	}
	
	public int getImportance(){
		return this.importance;
	}
	
	public int getProgress(){
		switch(this.progress){
		case INCOMPLETE:
			return 0;
		case IN_PROGRESS:
			return 1;
		case COMPLETED:
			return 2;
		default:
			return -1;
		}
	}
	
	public int getType(){
		switch(this.type){
		case FLOATING:
			return 0;
		case DEADLINE:
			return 1;
		case PERIOD:
			return 2;
		default:
			return -1;
		}
	}
	
	public String getTime(){
		switch(this.type){
		case FLOATING:
			return null;
		case DEADLINE:
			return null; //To output time string
		case PERIOD:
			return null; //To output combined time string
		default:
			return null;
		}
	}
	
	//To-do: Implement update functions
}
