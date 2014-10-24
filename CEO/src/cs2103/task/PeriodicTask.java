package cs2103.task;

import java.util.Comparator;
import java.util.Date;

import cs2103.exception.HandledException;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.Recur;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.Location;
import net.fortuna.ical4j.model.property.RRule;
import net.fortuna.ical4j.model.property.Uid;

public class PeriodicTask extends Task {
	private DateTime startTime;
	private DateTime endTime;
	private String location;
	private Recur recurrence;

	private static final String TYPE_PERIODIC = "Periodic";
	private static final String TYPE_RECURRING = "Recurring";
	private static final String STRING_LOCATION = "Location: ";
	private static final String STRING_RECUR = "Recurrence: ";
	
	public PeriodicTask(Uid taskUID, Date created, String title, String location, Date startTime, Date endTime, Recur recurrence) throws HandledException {
		super(taskUID, created, title);
		this.updateTime(startTime, endTime);
		this.updateLocation(location);
		this.updateRecurrence(recurrence);
	}
	
	public DateTime getStartTime(){
		return this.startTime;
	}
	
	public DateTime getEndTime(){
		return this.endTime;
	}
	
	public void updateTime(Date startTime, Date endTime) throws HandledException{
		if (startTime == null || endTime == null){
			throw new HandledException(HandledException.ExceptionType.INVALID_TIME);
		} else if (startTime.after(endTime)){
			throw new HandledException(HandledException.ExceptionType.END_BEFORE_START);
		} else {
			this.startTime = new DateTime(startTime);
			this.endTime = new DateTime(endTime);
		}
	}
	
	public String getLocation(){
		return this.location;
	}
	
	
	public void updateLocation(String location){
		if (location == null){
			this.location = "";
		} else {
			this.location = location;
		}
	}

	public Recur getRecurrence(){
		return this.recurrence;
	}
	
	public void updateRecurrence(Recur recurrence){
		this.recurrence=recurrence;
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
		FloatingTask newTask = new FloatingTask(this.getTaskUID(), this.getCreated(), this.getTitle(), false);
		newTask.updateDescription(this.getDescription());
		return newTask;
	}

	private DeadlineTask toDeadline(Date dueTime) throws HandledException {
		DeadlineTask newTask = new DeadlineTask(this.getTaskUID(), this.getCreated(), this.getTitle(), dueTime, false);
		newTask.updateDescription(this.getDescription());
		return newTask;
	}

	private PeriodicTask toPeriodic(Date startTime, Date endTime) throws HandledException {
		PeriodicTask newTask = new PeriodicTask(this.getTaskUID(), this.getCreated(), this.getTitle(), this.getLocation(), startTime, endTime, this.getRecurrence());
		newTask.updateDescription(this.getDescription());
		return newTask;
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		try {
			PeriodicTask newTask = new PeriodicTask(this.getTaskUID(), this.getCreated(), this.getTitle(), this.getLocation(), this.getStartTime(), this.getEndTime(), this.getRecurrence());
			newTask.updateDescription(this.getDescription());
			return newTask;
		} catch (HandledException e) {
			throw new CloneNotSupportedException();
		}
		
	}
	
	@Override
	public boolean getComplete() {
		return this.getEndTime().before(new DateTime());
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
	
	private static String recurToString(Recur recur){
		StringBuffer sb = new StringBuffer();
		sb.append(recur.getInterval()).append(" ");
		sb.append(recur.getFrequency()).append("\n");
		return sb.toString();
	}

	@Override
	public Component toComponent() {
		VEvent component = new VEvent(this.getStartTime(), this.getEndTime(),this.getTitle());
		this.addCommonProperty(component);
		if (this.getRecurrence() != null){
			component.getProperties().add(new RRule(this.getRecurrence()));
		}
		component.getProperties().add(new Location(this.getLocation()));
		return component;
	}
	
	public static sortComparator getComparator(){
		return new sortComparator();
	}

	public static class sortComparator implements Comparator<PeriodicTask>{
		@Override
		public int compare(PeriodicTask o1, PeriodicTask o2) {
			return o1.getStartTime().compareTo(o2.getStartTime());
		}
	}

	@Override
	public boolean checkPeriod(Date[] time) {
		if (time == null){
			return true;
		} else if (time[0] == null && time[1] == null){
			return true;
		} else if (time[1] == null){
			return this.getStartTime().before(time[0]);
		} else {
			return this.getStartTime().after(time[0]) && this.getStartTime().before(time[1]);
		}
	}
	
	@Override
	public boolean matches(String keyword) {
		if (keyword == null || keyword.isEmpty()){
			return true;
		} else {
			if (this.getTitle().contains(keyword) || this.getDescription().contains(keyword) || this.getLocation().contains(keyword)){
				return true;
			} else {
				return false;
			}
		}
	}
	
	public PeriodicTask updateTimeFromRecur() throws HandledException{
		DateTime now = new DateTime();
		if (this.getRecurrence() != null){
			if (this.getEndTime().before(now)){
				Date startTime = (this.getRecurrence().getNextDate(new DateTime(this.getStartTime()), now));
				Date endTime = new Date(this.getEndTime().getTime() - this.getStartTime().getTime() + startTime.getTime());
				this.updateTime(startTime, endTime);
				return this;
			}
		}
		return null;
	}
}
