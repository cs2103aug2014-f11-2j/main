package cs2103.task;

import java.util.Comparator;
import java.util.Date;

import org.apache.commons.lang.StringUtils;

import cs2103.exception.HandledException;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.component.VToDo;
import net.fortuna.ical4j.model.property.Status;

public class DeadlineTask extends ToDoTask {
	private DateTime dueTime;
	private static final String TYPE_DEADLINE = "Deadline";
	
	public DeadlineTask(String taskUID, Date created, Status status, String title, Date dueTime, Date completed) throws HandledException {
		super(taskUID, created, status, title, completed);
		this.updateDueTime(dueTime);
	}
	
	public DateTime getDueTime(){
		return this.dueTime;
	}
	
	public void updateDueTime(Date dueTime) throws HandledException{
		if (dueTime == null){
			throw new HandledException(HandledException.ExceptionType.INVALID_TIME);
		}else{
			this.dueTime = new DateTime(dueTime);
		}
	}

	@Override
	public Task convert(Date[] time) throws HandledException {
		if (time == null) throw new HandledException(HandledException.ExceptionType.INVALID_TIME);
		if (time[0] == null && time[1] == null){
			return this.toFloating();
		} else if (time[1] == null){
			return this.toDeadline(time[0]);
		} else {
			return this.toPeriodic(time[0], time[1]);
		}
	}
	
	private FloatingTask toFloating() throws HandledException {
		FloatingTask newTask = new FloatingTask(this.getTaskUID(), this.getCreated(), Status.VTODO_NEEDS_ACTION, this.getTitle(), this.getCompleted());
		newTask.updateDescription(this.getDescription());
		return newTask;
	}

	private DeadlineTask toDeadline(Date dueTime) throws HandledException {
		DeadlineTask newTask = new DeadlineTask(this.getTaskUID(), this.getCreated(), this.getStatus(), this.getTitle(), dueTime, this.getCompleted());
		newTask.updateDescription(this.getDescription());
		return newTask;
	}

	private PeriodicTask toPeriodic(Date startTime, Date endTime) throws HandledException {
		PeriodicTask newTask = new PeriodicTask(this.getTaskUID(), this.getCreated(), Status.VEVENT_CONFIRMED, this.getTitle(), null, startTime, endTime, null);
		newTask.updateDescription(this.getDescription());
		return newTask;
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		try {
			DeadlineTask newTask = new DeadlineTask(this.getTaskUID(), this.getCreated(), this.getStatus(), this.getTitle(), this.getDueTime(), this.getCompleted());
			newTask.updateDescription(this.getDescription());
			return newTask;
		} catch (HandledException e) {
			throw new CloneNotSupportedException();
		}
	}

	@Override
	public String toSummary() {
		StringBuffer sb = new StringBuffer();
		sb.append(this.getTaskID()).append(". ").append(this.getTitle()).append("\n");
		if (this.isDeleted()) sb.append(DELETED);
		sb.append(STRING_TYPE);
		sb.append(TYPE_DEADLINE);
		sb.append("\tStatus: ");
		sb.append(completedToString(this.getCompleted()));
		sb.append("\tDue At: ");
		sb.append(dateToString(this.getDueTime()));
		return sb.append("\n").toString();
	}

	@Override
	public String toDetail() {
		StringBuffer sb = new StringBuffer();
		sb.append(this.toSummary());
		sb.append(STRING_DESCRIPTION);
		sb.append(this.getDescription());
		return sb.append("\n").toString();
	}
	
	public static sortComparator getComparator(){
		return new sortComparator();
	}
	
	private static class sortComparator implements Comparator<DeadlineTask>{
		@Override
		public int compare(DeadlineTask o1, DeadlineTask o2) {
			return o1.getDueTime().compareTo(o2.getDueTime());
		}
	}

	@Override
	public boolean checkPeriod(Date[] time) {
		if (time == null){
			return true;
		} else if (time[0] == null){
			return true;
		} else if (time[1] == null){
			return this.getDueTime().before(time[0]);
		} else {
			return this.getDueTime().after(time[0]) && this.getDueTime().before(time[1]);
		}
	}

	@Override
	public boolean matches(String keyword) {
		if (keyword == null || keyword.isEmpty()){
			return true;
		} else {
			if (StringUtils.containsIgnoreCase(this.getTitle(), keyword) || StringUtils.containsIgnoreCase(this.getDescription(), keyword)){
				return true;
			} else {
				return false;
			}
		}
	}

	@Override
	protected VToDo toVToDo() {
		VToDo vToDo = new VToDo(this.getCreated(), this.getDueTime(), this.getTitle());
		this.addVToDoProperty(vToDo);
		return vToDo;
	}
	
	@Override
	public com.google.api.services.tasks.model.Task toGTask(){
		com.google.api.services.tasks.model.Task gTask = new com.google.api.services.tasks.model.Task();
		this.addGTaskProperty(gTask);
		gTask.setDue(new com.google.api.client.util.DateTime(this.getDueTime().getTime()));
		return gTask;
	}
}
