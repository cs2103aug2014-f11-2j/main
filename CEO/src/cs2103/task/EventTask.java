package cs2103.task;

import static org.fusesource.jansi.Ansi.ansi;
import static org.fusesource.jansi.Ansi.Color.GREEN;
import static org.fusesource.jansi.Ansi.Color.RED;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.fusesource.jansi.Ansi;

import com.google.api.client.util.Data;

import cs2103.exception.HandledException;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.TimeZone;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.Status;

public abstract class EventTask extends Task {
	private DateTime startTime;
	private DateTime endTime;
	
	private static final long YEAR_IN_MILLIS = 31556952000L;

	public EventTask(String taskUID, Status status, Date startTime, Date endTime) throws HandledException {
		super(taskUID);
		this.updateTime(startTime, endTime);
		this.updateStatus(status);
	}
	
	public DateTime getStartTime(){
		return this.startTime;
	}
	
	public DateTime getEndTime(){
		return this.endTime;
	}
	
	protected void updateTime(Date startTime, Date endTime) throws HandledException{
		if (startTime == null || endTime == null){
			throw new HandledException(HandledException.ExceptionType.INVALID_TIME);
		} else if (startTime.after(endTime)){
			throw new HandledException(HandledException.ExceptionType.END_BEFORE_START);
		} else {
			this.startTime = new DateTime(startTime);
			if (endTime.getTime() - startTime.getTime() > YEAR_IN_MILLIS){
				this.endTime = new DateTime(startTime.getTime() + YEAR_IN_MILLIS);
			} else {
				this.endTime = new DateTime(endTime);
			}
		}
	}
	
	@Override
	public DateTime getCompleted() {
		if (this.getEndTime().before(new DateTime())){
			return this.getEndTime();
		} else {
			return null;
		}
	}
	
	@Override
	public void updateCompleted(Date complete) {
		return;
	}
	
	@Override
	protected void updateStatus(Status status){
		if (status == null){
			this.status = Status.VEVENT_CONFIRMED;
		} else {
			this.status = status;
		}
	}
	
	@Override
	public boolean isDeleted() {
		return this.getStatus().equals(Status.VEVENT_CANCELLED);
	}

	@Override
	public void delete() {
		this.updateStatus(Status.VEVENT_CANCELLED);
	}

	@Override
	public void restore() {
		this.updateStatus(Status.VEVENT_CONFIRMED);
	}
	
	@Override
	public Component toComponent() {
		return this.toVEvent();
	}
	
	protected void addGEventProperty(com.google.api.services.calendar.model.Event gEvent){
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
	
	protected Ansi dateToString(Date date){
		Ansi returnString = ansi().bold();
		DateFormat format = new SimpleDateFormat("yyyy/MM/dd hh:mm a");
		if (this.checkAlert()){
			returnString.fg(RED);
		} else {
			returnString.fg(GREEN);
		}
		returnString.a(format.format(date)).reset();
		return returnString;
	}
	
	protected abstract VEvent toVEvent();
	public abstract com.google.api.services.calendar.model.Event toGEvent();
}
