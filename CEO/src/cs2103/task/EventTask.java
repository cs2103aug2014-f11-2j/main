package cs2103.task;

import java.util.Date;

import com.google.api.client.util.Data;

import cs2103.exception.HandledException;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.TimeZone;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.Status;

/**
 *  Contains inherited methods from Task
 *  Extends to concrete Task class PeriodicTask
 */
public abstract class EventTask extends Task {
	private DateTime startTime;
	private DateTime endTime;
	
	private static final long YEAR_IN_MILLIS = 31556952000L;

	public EventTask(String taskUID, Status status, Date startTime, Date endTime) throws HandledException {
		super(taskUID);
		this.updateTime(startTime, endTime);
		this.updateStatus(status);
	}
	
	/**
	 * @return the start time as in RFC2445 iCalendar specification
	 */
	public DateTime getStartTime(){
		return this.startTime;
	}
	
	/**
	 * @return the end time as in RFC2445 iCalendar specification
	 */
	public DateTime getEndTime(){
		return this.endTime;
	}
	
	protected void updateTime(Date startTime, Date endTime) throws HandledException{
		checkInvalidTimes(startTime, endTime);
		this.startTime = new DateTime(startTime);
		if (isAntique(startTime, endTime)){
			this.endTime = new DateTime(startTime.getTime() + YEAR_IN_MILLIS);
		} else {
			this.endTime = new DateTime(endTime);
		}
		
	}

	/**
	 * Checks if time specified is below the range allowed for DateTime class
	 */
	private boolean isAntique(Date startTime, Date endTime) {
		return endTime.getTime() - startTime.getTime() > YEAR_IN_MILLIS;
	}

	private void checkInvalidTimes(Date startTime, Date endTime) throws HandledException {
		if (checkTimeNull(startTime, endTime)) {
			throw new HandledException(HandledException.ExceptionType.INVALID_TIME);
		} else if (startTime.after(endTime)) {
			throw new HandledException(HandledException.ExceptionType.END_BEFORE_START);
		}
	}

	private boolean checkTimeNull(Date startTime, Date endTime) {
		return startTime == null || endTime == null;
	}
	
	/* (non-Javadoc)
	 * @see cs2103.task.Task#getCompleted()
	 */
	@Override
	public DateTime getCompleted() {
		if (checkEndBeforeNow()){
			return this.getEndTime();
		} else {
			return null;
		}
	}

	private boolean checkEndBeforeNow() {
		return this.getEndTime().before(new DateTime());
	}
	
	/* (non-Javadoc)
	 * @see cs2103.task.Task#updateCompleted(java.util.Date)
	 */
	@Override
	public void updateCompleted(Date complete) {
		return;
	}
	
	/* (non-Javadoc)
	 * @see cs2103.task.Task#updateStatus(net.fortuna.ical4j.model.property.Status)
	 */
	@Override
	protected void updateStatus(Status status){
		if (status == null){
			this.status = Status.VEVENT_CONFIRMED;
		} else {
			this.status = status;
		}
	}
	
	/* (non-Javadoc)
	 * @see cs2103.task.Task#isDeleted()
	 */
	@Override
	public boolean isDeleted() {
		return this.getStatus().equals(Status.VEVENT_CANCELLED);
	}

	/* (non-Javadoc)
	 * @see cs2103.task.Task#delete()
	 */
	@Override
	public void delete() {
		this.updateStatus(Status.VEVENT_CANCELLED);
	}

	/* (non-Javadoc)
	 * @see cs2103.task.Task#restore()
	 */
	@Override
	public void restore() {
		this.updateStatus(Status.VEVENT_CONFIRMED);
	}
	
	/* (non-Javadoc)
	 * @see cs2103.task.Task#toComponent()
	 */
	@Override
	public Component toComponent() {
		return this.toVEvent();
	}
	
	protected void addGEventProperty(com.google.api.services.calendar.model.Event gEvent){
		assert(gEvent != null);
		gEvent.setSummary(this.getTitle());
		gEvent.setDescription(this.getDescription());
		gEvent.setCreated(new com.google.api.client.util.DateTime(this.getCreated().getTime()));
		gEvent.setStatus("confirmed");
		gEvent.setStart(dateTimeToEventDateTime(this.getStartTime()));
		gEvent.setEnd(dateTimeToEventDateTime(this.getEndTime()));
	}
	
	private static com.google.api.services.calendar.model.EventDateTime dateTimeToEventDateTime(DateTime time){
		com.google.api.services.calendar.model.EventDateTime eventDateTime = new com.google.api.services.calendar.model.EventDateTime();
		eventDateTime.setTimeZone(TimeZone.getDefault().getID());
		eventDateTime.setDate(Data.NULL_DATE_TIME);
		eventDateTime.setDateTime(new com.google.api.client.util.DateTime(time.getTime()));
		return eventDateTime;
	}
	
	/**
	 * @return generate a VEvent object for iCal4j
	 */
	protected abstract VEvent toVEvent();
	/**
	 * @return generate a Google Calendar event which is needed for Google Sync
	 */
	public abstract com.google.api.services.calendar.model.Event toGEvent();
}
