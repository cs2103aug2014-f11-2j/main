package cs2103.task;

import java.util.Comparator;
import java.util.Date;

import org.apache.commons.lang.StringUtils;

import cs2103.exception.HandledException;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.Recur;
import net.fortuna.ical4j.model.component.VToDo;
import net.fortuna.ical4j.model.property.Completed;
import net.fortuna.ical4j.model.property.Status;
import net.fortuna.ical4j.model.property.Uid;

public class DeadlineTask extends Task {
	private DateTime dueTime;
	private DateTime completed;
	private static final String TYPE_DEADLINE = "Deadline";
	
	public DeadlineTask(Uid taskUID, Date created, Status status, String title, Date dueTime, Date completed) throws HandledException {
		super(taskUID, created, title);
		this.updateDueTime(dueTime);;
		this.updateCompleted(completed);
		this.updateStatus(status);
	}
	
	@Override
	public DateTime getCompleted(){
		return this.completed;
	}
	
	@Override
	public void updateCompleted(Date completed){
		if (completed == null){
			this.completed = null;
		} else {
			this.completed = new DateTime(completed);
		}
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
	protected void updateStatus(Status status){
		if (status == null){
			this.status = Status.VTODO_NEEDS_ACTION;
		} else {
			this.status = status;
		}
	}

	@Override
	public void updateLocation(String location) {
		return;
	}

	@Override
	public void updateRecurrence(Recur recurrence) {
		return;
	}
	
	@Override
	public boolean isDeleted() {
		return this.getStatus().equals(Status.VTODO_CANCELLED);
	}

	@Override
	public void delete() {
		this.updateStatus(Status.VTODO_CANCELLED);
	}

	@Override
	public void restore() {
		if (this.getCompleted() == null){
			this.updateStatus(Status.VTODO_NEEDS_ACTION);
		} else {
			this.updateStatus(Status.VTODO_COMPLETED);
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

	@Override
	public Component toComponent() {
		VToDo component = new VToDo(this.getDueTime(), this.getDueTime(), this.getTitle());
		this.addCommonProperty(component);
		if (this.isDeleted()){
			component.getProperties().add(Status.VTODO_CANCELLED);
		} else {
			if (this.getCompleted() == null){
				component.getProperties().add(Status.VTODO_NEEDS_ACTION);
			} else {
				component.getProperties().add(Status.VTODO_COMPLETED);
				component.getProperties().add(new Completed(this.getCompleted()));
			}
		}
		return component;
	}
	
	public com.google.api.services.tasks.model.Task toGTask(){
		com.google.api.services.tasks.model.Task gTask = new com.google.api.services.tasks.model.Task();
		gTask.setTitle(this.getTitle());
		gTask.setId(this.getTaskUID().getValue());
		gTask.setDue(new com.google.api.client.util.DateTime(this.getDueTime().getTime()));
		if (this.getCompleted() != null) gTask.setCompleted(new com.google.api.client.util.DateTime(this.getLastModified().getTime()));
		gTask.setNotes(this.getDescription());
		gTask.setUpdated(new com.google.api.client.util.DateTime(this.getLastModified().getTime()));
		return gTask;
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
		} else if (time[0] == null && time[1] == null){
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
}
