package cs2103.task;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.fusesource.jansi.Ansi;
import static org.fusesource.jansi.Ansi.*;
import static org.fusesource.jansi.Ansi.Color.*;
import cs2103.exception.HandledException;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.Recur;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.Location;
import net.fortuna.ical4j.model.property.RRule;
import net.fortuna.ical4j.model.property.Status;

public class PeriodicTask extends EventTask {
	private String location;
	private Recur recurrence;

	private static final String STRING_LOCATION = "Location: ";
	private static final String STRING_RECUR = "Recurrence: ";
	
	public PeriodicTask(String taskUID, Status status, Date startTime, Date endTime) throws HandledException {
		super(taskUID, status, startTime, endTime);
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
		this.recurrence = recurrence;
	}
	
	@Override
	protected Task convert(Date[] time) throws HandledException {
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
		FloatingTask newTask = new FloatingTask(this.getTaskUID(), Status.VTODO_NEEDS_ACTION);
		updateNewTask(newTask);
		return newTask;
	}

	private DeadlineTask toDeadline(Date dueTime) throws HandledException {
		DeadlineTask newTask = new DeadlineTask(this.getTaskUID(), Status.VTODO_NEEDS_ACTION, dueTime);
		updateNewTask(newTask);
		return newTask;
	}

	private PeriodicTask toPeriodic(Date startTime, Date endTime) throws HandledException {
		PeriodicTask newTask = new PeriodicTask(this.getTaskUID(), this.getStatus(), startTime, endTime);
		updateNewTask(newTask);
		return newTask;
	}
	
	private void updateNewTask(Task newTask) {
		if (newTask instanceof PeriodicTask) {
			newTask.updateLocation(this.getLocation());
			newTask.updateRecurrence(this.getRecurrence());
		}
		newTask.updateTitle(this.getTitle());
		newTask.updateCreated(this.getCreated());
		newTask.updateDescription(this.getDescription());
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		try {
			PeriodicTask newTask = new PeriodicTask(this.getTaskUID(), this.getStatus(), this.getStartTime(), this.getEndTime());
			updateClone(newTask);
			return newTask;
		} catch (HandledException e) {
			throw new CloneNotSupportedException();
		}
		
	}

	private void updateClone(PeriodicTask newTask) {
		updateNewTask(newTask);
		newTask.updateLastModified(null);
	}

	@Override
	public Ansi toSummary() {
		Ansi returnString = this.addCommonString();
		returnString.a("From: ");
		returnString.a(this.dateToString(this.getStartTime()));
		returnString.a(" to ");
		returnString.a(this.dateToString(this.getEndTime())).reset();
		returnString.a('\n');
		if (this.getRecurrence() != null){
			returnString.a(recurToString(this.getRecurrence())).a('\n');
		}
		return returnString;
	}

	@Override
	public Ansi toDetail() {
		Ansi returnString = this.toSummary();
		formatLocation(returnString);
		formatDescription(returnString);
		return returnString;
	}

	private void formatLocation(Ansi returnString) {
		returnString.a(STRING_LOCATION);
		returnString.fg(CYAN).a(this.getLocation()).a("\n").reset();
	}
	
	private void formatDescription(Ansi returnString) {
		returnString.a(STRING_DESCRIPTION).a(this.getDescription()).reset().a('\n');
	}
	
	static Ansi recurToString(Recur recur){
		Ansi returnString = ansi().a(STRING_RECUR);
		returnString.fg(YELLOW).a(recur.getInterval()).a(' ');
		returnString.a(recur.getFrequency()).reset();
		return returnString;
	}
	
	private static List<String> recurToGoogle(Recur recur){
		if (recur == null){
			return null;
		} else {
			List<String> recurrenceList = new ArrayList<String>();
			StringBuffer sb = new StringBuffer();
			sb.append("RRULE:").append(recur.toString());
			recurrenceList.add(sb.toString());
			return recurrenceList;
		}
	}

	@Override
	public VEvent toVEvent() {
		VEvent vEvent = new VEvent(this.getStartTime(), this.getEndTime(),this.getTitle());
		this.addCommonProperty(vEvent);
		if (this.getRecurrence() != null){
			vEvent.getProperties().add(new RRule(this.getRecurrence()));
		}
		vEvent.getProperties().add(this.getStatus());
		vEvent.getProperties().add(new Location(this.getLocation()));
		return vEvent;
	}
	
	public com.google.api.services.calendar.model.Event toGEvent(){
		com.google.api.services.calendar.model.Event gEvent = new com.google.api.services.calendar.model.Event();
		this.addGEventProperty(gEvent);
		gEvent.setLocation(this.getLocation());
		List<String> recurrenceList = recurToGoogle(this.getRecurrence());
		if (recurrenceList != null) gEvent.setRecurrence(recurrenceList);
		return gEvent;
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
		} else if (time[0] == null){
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
			return containsKeywordInTask(keyword);
		}
	}

	private boolean containsKeywordInTask(String keyword) {
		if (containsKeywordInTitle(keyword)) {
			return true;
		} else if (containsKeywordInDescription(keyword)) {
			return true;
		} else if (containsKeywordInLocation(keyword)) {
			return true;
		}
		return false;
	}

	private boolean containsKeywordInLocation(String keyword) {
		return StringUtils.containsIgnoreCase(this.getLocation(), keyword);
	}

	private boolean containsKeywordInDescription(String keyword) {
		return StringUtils.containsIgnoreCase(this.getDescription(), keyword);
	}

	private boolean containsKeywordInTitle(String keyword) {
		return StringUtils.containsIgnoreCase(this.getTitle(), keyword);
	}
	
	public PeriodicTask updateTimeFromRecur() throws HandledException{
		DateTime now = new DateTime();
		if (this.getRecurrence() != null && this.getRecurrence().getFrequency() != null && this.getEndTime().before(now)){
			Date startTime = this.getRecurrence().getNextDate(this.getStartTime(), now);
			if (startTime == null){
				return null;
			} else {
				Date endTime = new Date(this.getEndTime().getTime() - this.getStartTime().getTime() + startTime.getTime());
				this.updateTime(startTime, endTime);
				this.updateLastModified(null);
				return this;
			}
		} else {
			return null;
		}
	}
}
