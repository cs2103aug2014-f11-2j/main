package cs2103;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.Recur;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.Description;
import net.fortuna.ical4j.model.property.Location;
import net.fortuna.ical4j.model.property.RRule;
import net.fortuna.ical4j.model.property.Uid;

public class PeriodicTask extends Task {
	private Date startTime;
	private Date endTime;
	private String location;
	private Recur recurrence;

	private static final String TYPE_PERIODIC = "Periodic";
	private static final String TYPE_RECURRING = "Recurring";
	private static final String STRING_LOCATION = "Location: ";
	private static final String STRING_RECUR = "Recurrence: ";
	
	public PeriodicTask(Uid taskUID, String title, String location, Date startTime, Date endTime, Recur recurrence) throws HandledException {
		super(taskUID, title);
		this.updateTime(startTime, endTime);
		this.updateLocation(location);
		this.updateRecurrence(recurrence);
	}
	
	public Date getStartTime(){
		return this.startTime;
	}
	
	public Date getEndTime(){
		return this.endTime;
	}
	
	public void updateTime(Date startTime, Date endTime) throws HandledException{
		if (startTime == null || endTime == null){
			throw new HandledException(HandledException.ExceptionType.INVALID_TIME);
		} else if (startTime.after(endTime)){
			throw new HandledException(HandledException.ExceptionType.END_BEFORE_START);
		} else {
			this.startTime=startTime;
			this.endTime=endTime;
		}
	}
	
	public String getLocation(){
		return this.location;
	}
	
	
	public void updateLocation(String location){
		this.location=location;
	}

	public Recur getRecurrence(){
		return this.recurrence;
	}
	
	public void updateRecurrence(Recur recurrence){
		this.recurrence=recurrence;
	}

	@Override
	public FloatingTask toFloating() throws HandledException {
		FloatingTask newTask = new FloatingTask(this.getTaskUID(), this.getTitle(), false);
		newTask.updateDescription(this.getDescription());
		return newTask;
	}

	@Override
	public DeadlineTask toDeadline(Date dueTime) throws HandledException {
		DeadlineTask newTask = new DeadlineTask(this.getTaskUID(), this.getTitle(), dueTime, false);
		newTask.updateDescription(this.getDescription());
		return newTask;
	}

	@Override
	public PeriodicTask toPeriodic(Date startTime, Date endTime) throws HandledException {
		PeriodicTask newTask = new PeriodicTask(this.getTaskUID(), this.getTitle(), this.getLocation(), startTime, endTime, this.getRecurrence());
		newTask.updateDescription(this.getDescription());
		return newTask;
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		try {
			PeriodicTask newTask = new PeriodicTask(this.getTaskUID(), this.getTitle(), this.getLocation(), this.getStartTime(), this.getEndTime(), this.getRecurrence());
			newTask.updateDescription(this.getDescription());
			return newTask;
		} catch (HandledException e) {
			throw new CloneNotSupportedException();
		}
		
	}

	@Override
	public void updateComplete(boolean complete) {
		return;
	}

	@Override
	public String toSummary() {
		StringBuffer sb = new StringBuffer();
		sb.append(this.getTaskID()).append(". ").append(this.getTitle()).append("\n");
		sb.append(STRING_TYPE);
		if (this.getRecurrence() == null){
			sb.append(TYPE_PERIODIC);
		} else {
			sb.append(TYPE_RECURRING);
		}
		sb.append("\tFrom: ");
		sb.append(dateToString(this.getStartTime()));
		sb.append(" To ");
		sb.append(dateToString(this.getEndTime()));
		return sb.append("\n").toString();
	}

	@Override
	public String toDetail() {
		StringBuffer sb = new StringBuffer();
		sb.append(this.toSummary());
		if (this.getRecurrence() != null){
			sb.append(STRING_RECUR);
			sb.append(recurToString(this.getRecurrence()));
		}
		sb.append(STRING_LOCATION);
		sb.append(this.getLocation()).append("\n");
		sb.append(STRING_DESCRIPTION);
		sb.append(this.getDescription()).append("\n");
		return sb.toString();
	}
	
	private static String dateToString(Date date){
		DateFormat dateFormat;
		dateFormat = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM, Locale.UK);
		return dateFormat.format(date);
	}
	
	private static String recurToString(Recur recur){
		StringBuffer sb = new StringBuffer();
		sb.append(recur.getInterval()).append(" ");
		sb.append(recur.getFrequency()).append("\n");
		return sb.toString();
	}

	@Override
	public Component toComponent() {
		VEvent component = new VEvent(new DateTime(this.getStartTime()), new DateTime(this.getEndTime()),this.getTitle());
		component.getProperties().add(this.getTaskUID());
		if (this.getRecurrence() != null){
			component.getProperties().add(new RRule(this.getRecurrence()));
		}
		component.getProperties().add(new Description(this.getDescription()));
		component.getProperties().add(new Location(this.getLocation()));
		return component;
	}
}
