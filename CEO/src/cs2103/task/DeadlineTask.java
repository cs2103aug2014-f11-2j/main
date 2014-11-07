package cs2103.task;

import java.util.Comparator;
import java.util.Date;

import org.fusesource.jansi.Ansi;

import cs2103.exception.HandledException;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.component.VToDo;
import net.fortuna.ical4j.model.property.Status;

public class DeadlineTask extends ToDoTask {
	private DateTime dueTime;
	
	public DeadlineTask(String taskUID, Status status, Date dueTime) throws HandledException {
		super(taskUID, status);
		this.updateDueTime(dueTime);
	}
	
	public DateTime getDueTime(){
		return this.dueTime;
	}
	
	private void updateDueTime(Date dueTime) throws HandledException{
		if (dueTime == null){
			throw new HandledException(HandledException.ExceptionType.INVALID_TIME);
		} else {
			this.dueTime = new DateTime(dueTime);
		}
	}

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
		return isSecondTimeNull(time);
	}

	private boolean isBothTimeNull(Date[] time) {
		return time[0] == null && isFirstTimeNull(time);
	}
	
	private ToDoTask toFloating() throws HandledException {
		ToDoTask newTask = new FloatingTask(this.getTaskUID(), Status.VTODO_NEEDS_ACTION);
		updateNewTask(newTask);
		return newTask;
	}

	private ToDoTask toDeadline(Date dueTime) throws HandledException {
		ToDoTask newTask = new DeadlineTask(this.getTaskUID(), this.getStatus(), dueTime);
		updateNewTask(newTask);
		return newTask;
	}

	private PeriodicTask toPeriodic(Date startTime, Date endTime) throws HandledException {
		PeriodicTask newTask = new PeriodicTask(this.getTaskUID(), Status.VEVENT_CONFIRMED, startTime, endTime);
		updateNewTask(newTask);
		return newTask;
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		try {
			ToDoTask newTask = new DeadlineTask(this.getTaskUID(), this.getStatus(), this.getDueTime());
			updateClone(newTask);
			return newTask;
		} catch (HandledException e) {
			throw new CloneNotSupportedException();
		}
	}

	@Override
	public Ansi toSummary() {
		Ansi returnString = this.addCommonString();
		formatStatus(returnString);
		formatDueTime(returnString);
		return returnString.a('\n');
	}

	private void formatDueTime(Ansi returnString) {
		returnString.a("\tDue At: ").a(this.dateToString(this.getDueTime()));
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
		if (isNullTimePeriod(time)){
			return true;
		} else if (isSecondTimeNull(time)){
			return checkTimeBeforeDueTime(time);
		} else {
			return checkDueTimeBetweenTimes(time);
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
	
	private boolean isSecondTimeNull(Date[] time) {
		return time[1] == null;
	}

	/**
	 * Check if dueTime is between time[0] and time[1]
	 */
	private boolean checkDueTimeBetweenTimes(Date[] time) {
		return this.getDueTime().after(time[0]) && this.getDueTime().before(time[1]);
	}

	/**
	 * Check if dueTime is before time
	 */
	private boolean checkTimeBeforeDueTime(Date[] time) {
		return this.getDueTime().before(time[0]);
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
