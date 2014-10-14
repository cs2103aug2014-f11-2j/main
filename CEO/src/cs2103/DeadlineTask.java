package cs2103;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.Recur;
import net.fortuna.ical4j.model.component.VToDo;
import net.fortuna.ical4j.model.property.Description;
import net.fortuna.ical4j.model.property.Status;
import net.fortuna.ical4j.model.property.Uid;

class DeadlineTask extends Task {
	private Date dueTime;
	private boolean complete;
	private static final String TYPE_DEADLINE = "Deadline";
	
	public DeadlineTask(Uid taskUID, String title, Date dueTime, boolean complete) throws HandledException {
		super(taskUID, title);
		this.updateDueTime(dueTime);;
		this.updateComplete(complete);
	}
	
	public boolean getComplete(){
		return this.complete;
	}
	
	public void updateComplete(boolean complete){
		this.complete=complete;
	}
	
	public Date getDueTime(){
		return this.dueTime;
	}
	
	public void updateDueTime(Date dueTime) throws HandledException{
		if (dueTime == null){
			throw new HandledException(HandledException.ExceptionType.INVALID_TIME);
		}else{
			this.dueTime=dueTime;
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
		FloatingTask newTask = new FloatingTask(this.getTaskUID(), this.getTitle(), false);
		newTask.updateDescription(this.getDescription());
		return newTask;
	}

	private DeadlineTask toDeadline(Date dueTime) throws HandledException {
		DeadlineTask newTask = new DeadlineTask(this.getTaskUID(), this.getTitle(), dueTime, this.getComplete());
		newTask.updateDescription(this.getDescription());
		return newTask;
	}

	private PeriodicTask toPeriodic(Date startTime, Date endTime) throws HandledException {
		PeriodicTask newTask = new PeriodicTask(this.getTaskUID(), this.getTitle(), null, startTime, endTime, null);
		newTask.updateDescription(this.getDescription());
		return newTask;
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		try {
			DeadlineTask newTask = new DeadlineTask(this.getTaskUID(), this.getTitle(), this.getDueTime(), this.getComplete());
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
		sb.append(TYPE_DEADLINE);
		sb.append("\tStatus: ");
		sb.append(completeToString(this.getComplete()));
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
	
	private static String dateToString(Date date){
		DateFormat dateFormat;
		dateFormat = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM, Locale.UK);
		return dateFormat.format(date);
	}
	
	private static String completeToString(boolean complete){
		return complete?"Completed":"Needs Action";
	}

	@Override
	public Component toComponent() {
		VToDo component = new VToDo(new DateTime(this.getDueTime()), new DateTime(this.getDueTime()), this.getTitle());
		component.getProperties().add(this.getTaskUID());
		component.getProperties().add(new Description(this.getDescription()));
		component.getProperties().add(completeToStatus(this.getComplete()));
		return component;
	}
	
	private static Status completeToStatus(boolean complete){
		return complete?Status.VTODO_COMPLETED:Status.VTODO_NEEDS_ACTION;
	}
}
