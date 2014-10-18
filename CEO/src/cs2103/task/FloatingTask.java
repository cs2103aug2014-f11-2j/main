package cs2103.task;

import java.util.Date;

import cs2103.exception.HandledException;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.Recur;
import net.fortuna.ical4j.model.component.VToDo;
import net.fortuna.ical4j.model.property.Status;
import net.fortuna.ical4j.model.property.Uid;

public class FloatingTask extends Task {
	private boolean complete;
	private static final String TYPE_FLOATING = "Floating";
	
	public FloatingTask(Uid taskUID, Date created, String title, boolean complete) throws HandledException{
		super(taskUID, created, title);
		this.updateComplete(complete);
	}
	
	public boolean getComplete(){
		return this.complete;
	}
	
	public void updateComplete(boolean complete){
		this.complete=complete;
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
		try {
			return (FloatingTask) this.clone();
		} catch (CloneNotSupportedException e) {
			throw new HandledException(HandledException.ExceptionType.CLONE_FAILED);
		}
	}

	private DeadlineTask toDeadline(Date dueTime) throws HandledException {
		DeadlineTask newTask = new DeadlineTask(this.getTaskUID(), this.getCreated(), this.getTitle(), dueTime, this.getComplete());
		newTask.updateDescription(this.getDescription());
		return newTask;
	}

	private PeriodicTask toPeriodic(Date startTime, Date endTime) throws HandledException {
		PeriodicTask newTask = new PeriodicTask(this.getTaskUID(), this.getCreated(), this.getTitle(), null, startTime, endTime, null);
		newTask.updateDescription(this.getDescription());
		return newTask;
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		try {
			FloatingTask newTask = new FloatingTask(this.getTaskUID(), this.getCreated(), this.getTitle(), this.getComplete());
			newTask.updateDescription(this.getDescription());
			return newTask;
		} catch (HandledException e) {
			throw new CloneNotSupportedException();
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
	public String toSummary() {
		StringBuffer sb = new StringBuffer();
		sb.append(this.getTaskID()).append(". ").append(this.getTitle()).append("\n");
		sb.append(STRING_TYPE);
		sb.append(TYPE_FLOATING);
		sb.append("\tStatus: ");
		sb.append(completeToString(this.getComplete()));
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
	
	private static String completeToString(boolean complete){
		return complete?"Completed":"Needs Action";
	}

	@Override
	public Component toComponent() {
		VToDo component = new VToDo(new DateTime(new Date()), this.getTitle());
		this.addCommonProperty(component);
		component.getProperties().add(completeToStatus(this.getComplete()));
		return component;
	}
	
	private static Status completeToStatus(boolean complete){
		return complete?Status.VTODO_COMPLETED:Status.VTODO_NEEDS_ACTION;
	}

	@Override
	public boolean checkPeriod(Date[] time) {
		return false;
	}
	
	@Override
	public boolean matches(String keyword) {
		if (keyword == null || keyword.isEmpty()){
			return true;
		} else {
			if (this.getTitle().contains(keyword) || this.getDescription().contains(keyword)){
				return true;
			} else {
				return false;
			}
		}
	}
}
