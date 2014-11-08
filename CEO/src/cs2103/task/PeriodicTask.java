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
import cs2103.util.CommonUtil;
import cs2103.util.Logger;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.Recur;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.Location;
import net.fortuna.ical4j.model.property.RRule;
import net.fortuna.ical4j.model.property.Status;

public class PeriodicTask extends EventTask {
	private String location;
	private Recur recurrence;
	private final Logger logger;

	private static final String STRING_LOCATION = "Location: ";
	private static final String STRING_RECUR = "Recurrence: ";
	private static final String LOG_UPDATEFROMRECUR = "Updating Periodic Task with UID %1$s from Recurrence";
	
	public PeriodicTask(String taskUID, Status status, Date startTime, Date endTime) throws HandledException {
		super(taskUID, status, startTime, endTime);
		this.logger = Logger.getInstance();
	}
	
	/**
	 * @return the location String
	 */
	public String getLocation(){
		return this.location;
	}
	
	/* (non-Javadoc)
	 * @see cs2103.task.Task#updateLocation(java.lang.String)
	 */
	public void updateLocation(String location){
		if (location == null){
			this.location = "";
		} else {
			this.location = location;
		}
	}

	/**
	 * @return the Recurrence object
	 */
	public Recur getRecurrence(){
		return this.recurrence;
	}
	
	/* (non-Javadoc)
	 * @see cs2103.task.Task#updateRecurrence(net.fortuna.ical4j.model.Recur)
	 */
	public void updateRecurrence(Recur recurrence){
		this.recurrence = recurrence;
	}
	
	/* (non-Javadoc)
	 * @see cs2103.task.Task#convert(java.util.Date[])
	 */
	@Override
	protected Task convert(Date[] time) throws HandledException {
		if (isInvalidTime(time)) {
			throw new HandledException(HandledException.ExceptionType.INVALID_TIME);
		} else if (isBothTimeNull(time)){
			return this.toFloating();
		} else if (isFirstTimeNull(time)){
			return this.toDeadline(time[0]);
		} else {
			return this.toPeriodic(time[0], time[1]);
		}
	}

	private boolean isInvalidTime(Date[] time) {
		return time == null;
	}

	private boolean isFirstTimeNull(Date[] time) {
		return time[1] == null;
	}

	private boolean isBothTimeNull(Date[] time) {
		return time[0] == null && isFirstTimeNull(time);
	}
	
	private ToDoTask toFloating() throws HandledException {
		ToDoTask newTask = new FloatingTask(this.getTaskUID(), Status.VTODO_NEEDS_ACTION);
		assert(newTask != null);
		updateNewTask(newTask);
		return newTask;
	}

	private ToDoTask toDeadline(Date dueTime) throws HandledException {
		ToDoTask newTask = new DeadlineTask(this.getTaskUID(), Status.VTODO_NEEDS_ACTION, dueTime);
		assert(newTask != null);
		updateNewTask(newTask);
		return newTask;
	}

	private PeriodicTask toPeriodic(Date startTime, Date endTime) throws HandledException {
		PeriodicTask newTask = new PeriodicTask(this.getTaskUID(), this.getStatus(), startTime, endTime);
		assert(newTask != null);
		updateNewTask(newTask);
		return newTask;
	}
	
	private void updateNewTask(Task newTask) {
		newTask.updateLocation(this.getLocation());
		newTask.updateRecurrence(this.getRecurrence());
		newTask.updateTitle(this.getTitle());
		newTask.updateCreated(this.getCreated());
		newTask.updateDescription(this.getDescription());
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		try {
			PeriodicTask newTask = new PeriodicTask(this.getTaskUID(), this.getStatus(), this.getStartTime(), this.getEndTime());
			assert(newTask != null);
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
		formatStartEndTime(returnString);
		if (hasRecurrence()){
			formatRecurrence(returnString);
		}
		return returnString;
	}

	private boolean hasRecurrence() {
		return this.getRecurrence() != null;
	}

	private void formatStartEndTime(Ansi returnString) {
		returnString.a("From: ");
		returnString.a(this.dateToString(this.getStartTime()));
		returnString.a(" to ");
		returnString.a(this.dateToString(this.getEndTime())).reset();
		returnString.a('\n');
	}

	private Ansi formatRecurrence(Ansi returnString) {
		return returnString.a(recurToString(this.getRecurrence())).a('\n');
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
	
	private static Ansi recurToString(Recur recur){
		Ansi returnString = ansi().a(STRING_RECUR);
		returnString.fg(YELLOW).a(recur.getInterval()).a(' ');
		returnString.a(recur.getFrequency()).reset();
		return returnString;
	}
	
	private static List<String> recurToGoogle(Recur recur){
		if (recur == null){
			return null;
		} else {
			return generateRecurrenceList(recur);
		}
	}

	private static List<String> generateRecurrenceList(Recur recur) {
		List<String> recurrenceList = new ArrayList<String>();
		StringBuffer sb = new StringBuffer();
		sb.append("RRULE:").append(recur.toString());
		recurrenceList.add(sb.toString());
		return recurrenceList;
	}

	@Override
	public VEvent toVEvent() {
		VEvent vEvent = new VEvent(this.getStartTime(), this.getEndTime(),this.getTitle());
		this.addCommonProperty(vEvent);
		if (hasRecurrence()){
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
	
	/**
	 * @return the comparator for sorting
	 */
	public static sortComparator getComparator(){
		return new sortComparator();
	}

	/**
	 * Special class to compare Periodic Tasks
	 */
	private static class sortComparator implements Comparator<PeriodicTask>{
		@Override
		public int compare(PeriodicTask o1, PeriodicTask o2) {
			return o1.getStartTime().compareTo(o2.getStartTime());
		}
	}

	@Override
	public boolean checkPeriod(Date[] time) {
		if (isNullTimePeriod(time)){
			return true;
		} else if (isFirstTimeNull(time)){
			return checkTimeAfterStartTime(time[0]);
		} else {
			return checkStartTimeBetweenTimes(time);
		}
	}
	
	private boolean isNullTimePeriod(Date[] time) {
		if (time == null) {
			return true;
		} else if (time[0] == null) {
			return true;
		} 
		return false;
	}

	/**
	 * Check if startTime is between time[0] and time[1]
	 */
	private boolean checkStartTimeBetweenTimes(Date[] time) {
		return this.getStartTime().after(time[0]) && this.getStartTime().before(time[1]);
	}

	/**
	 * Check if startTime is before time
	 */
	private boolean checkTimeAfterStartTime(Date time) {
		return this.getStartTime().before(time);
	}
	
	@Override
	public boolean matches(String keyword) {
		if (isEmptyKeyword(keyword)){
			return true;
		} else {
			return containsKeywordInTask(keyword);
		}
	}

	private boolean isEmptyKeyword(String keyword) {
		return keyword == null || keyword.isEmpty();
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
	
	/**
	 * Updates new Times for task based on recurrence
	 */
	public PeriodicTask updateTimeFromRecur() throws HandledException {
		DateTime now = new DateTime();
		if (hasRecurrenceAndFrequency() && endTimeBeforeNow(now)){
			Date startTime = calculateStartTimeFromRecur(now);
			if (startTime == null){
				return null;
			} else {
				Date endTime = calculateEndTimeFromRecur(startTime);
				assert(endTime != null);
				this.logger.writeLog(CommonUtil.formatLogString(LOG_UPDATEFROMRECUR, this));
				this.updateTime(startTime, endTime);
				this.updateLastModified(null);
				return this;
			}
		} else {
			return null;
		}
	}

	private net.fortuna.ical4j.model.Date calculateStartTimeFromRecur(DateTime now) {
		assert(now != null);
		return this.getRecurrence().getNextDate(this.getStartTime(), now);
	}

	private Date calculateEndTimeFromRecur(Date startTime) {
		assert(startTime != null);
		return new Date(this.getEndTime().getTime() - this.getStartTime().getTime() + startTime.getTime());
	}

	private boolean endTimeBeforeNow(DateTime now) {
		assert(now != null);
		return this.getEndTime().before(now);
	}

	private boolean hasRecurrenceAndFrequency() {
		return hasRecurrence() && this.getRecurrence().getFrequency() != null;
	}
}