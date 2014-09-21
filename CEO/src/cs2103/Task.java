package cs2103;
import java.text.DateFormat;  
import java.util.Date;  
import java.util.GregorianCalendar;

class Task {
	private static enum Progress{
		INCOMPLETE, IN_PROGRESS, COMPLETED
	};
	private final static int START=0;
	private final static int END=1;
	private final static int FLOATING=0;
	private final static int DEADLINE=1;
	private final static int PERIODIC=2;
	private String taskUID;
	private String title;
	private String description;
	private String location;
	private String category;
	private String recurrence;
	private int importance;
	private Progress progress;
	private Date[] time;

	
	public Task(String title, String description, String location, String category, String recurrence,  int importance, Date startTime, Date endTime) throws CEOException{
		if (title!=null){
			this.title=title;
			this.description=description;
			this.location=location;
			this.category=category;
			this.recurrence=recurrence;
			this.importance=importance;
			this.progress=Progress.INCOMPLETE;
			if (startTime!=null && endTime!=null){
				time=new Date[2];
				this.time[START]=startTime;
				this.time[END]=endTime;
			}else if (endTime==null){
				time=new Date[1];
				this.time[START]=startTime;
			}else{
				time=null;
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
		if (time.length==2){
			return PERIODIC;
		}else if (time.length==1){
			return DEADLINE;
		}else{
			return FLOATING;
		}
	}
	
	public Date[] getTime(){
		return this.time;
	}
	
	//To-do: Implement update functions
}
