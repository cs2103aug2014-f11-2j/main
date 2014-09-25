package cs2103;
import java.util.Date;  

class Task {
	private final static int START=0;
	private final static int END=1;
	@SuppressWarnings("unused")
	private final static int FLOATING=0;
	private final static int DEADLINE=1;
	private final static int PERIODIC=2;
	private final static String TYPE_FLOATING="floating";
	private final static String TYPE_DEADLINE="deadline";
	private final static String TYPE_PERIODIC="periodic";
	private final static int INCOMPLETE=0;
	@SuppressWarnings("unused")
	private final static int IN_PROGRESS=1;
	private final static int COMPLETED=2;
	private String taskUID;
	private String title;
	private String description;
	private String location;
	private String category;
	private String recurrence;
	private int importance;
	private String type;
	private int progress;
	private Date[] time;

	
	public Task(String title, String description, String location, String category, String recurrence,  int importance, Date startTime, Date endTime) throws CEOException{
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
	
	public String getRecurrance(){
		return this.recurrence;
	}
	
	public String getType(){
		return this.type;
	}
	
	public int getImportance(){
		return this.importance;
	}
	
	public int getProgress(){
		return this.progress;
	}

	public Date[] getTime(){
		return this.time;
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
	
	public void updateRecurrence(String recurrence){
		this.recurrence=recurrence;
	}
	
	public void updateImportance(int importance){
		this.importance=importance;
	}
	
	public void updateProgress(int progress) throws CEOException{
		if (progress > COMPLETED || progress < INCOMPLETE){
			throw new CEOException("Invalid Progress");
		}else{
			this.progress=progress;
		}
	}
	
	public void updateTime(Date startTime, Date endTime){
		if (startTime==null && endTime==null){
			this.time=null;
			this.type=TYPE_FLOATING;
		}else if (endTime==null){
			this.time=new Date[DEADLINE];
			this.time[START]=startTime;
			this.type=TYPE_DEADLINE;
		}else{
			this.time=new Date[PERIODIC];
			this.time[START]=startTime;
			this.time[END]=endTime;
			this.type=TYPE_PERIODIC;
		}
	}
	
}
