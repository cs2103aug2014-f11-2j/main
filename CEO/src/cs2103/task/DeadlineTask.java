package cs2103.task;

import static org.fusesource.jansi.Ansi.ansi;
import static org.fusesource.jansi.Ansi.Color.GREEN;
import static org.fusesource.jansi.Ansi.Color.RED;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
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
	
	public void updateDueTime(Date dueTime) throws HandledException{
		if (dueTime == null){
			throw new HandledException(HandledException.ExceptionType.INVALID_TIME);
		} else {
			this.dueTime = new DateTime(dueTime);
		}
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
		newTask.updateTitle(this.getTitle());
		newTask.updateCreated(this.getCreated());
		newTask.updateCompleted(this.getCompleted());
		newTask.updateDescription(this.getDescription());
		return newTask;
	}

	private DeadlineTask toDeadline(Date dueTime) throws HandledException {
		DeadlineTask newTask = new DeadlineTask(this.getTaskUID(), this.getStatus(), dueTime);
		newTask.updateTitle(this.getTitle());
		newTask.updateCreated(this.getCreated());
		newTask.updateCompleted(this.getCompleted());
		newTask.updateDescription(this.getDescription());
		return newTask;
	}

	private PeriodicTask toPeriodic(Date startTime, Date endTime) throws HandledException {
		PeriodicTask newTask = new PeriodicTask(this.getTaskUID(), Status.VEVENT_CONFIRMED, startTime, endTime);
		newTask.updateTitle(this.getTitle());
		newTask.updateCreated(this.getCreated());
		newTask.updateDescription(this.getDescription());
		return newTask;
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		try {
			DeadlineTask newTask = new DeadlineTask(this.getTaskUID(), this.getStatus(), this.getDueTime());
			newTask.updateTitle(this.getTitle());
			newTask.updateCreated(this.getCreated());
			newTask.updateCompleted(this.getCompleted());
			newTask.updateDescription(this.getDescription());
			newTask.updateLastModified(null);
			return newTask;
		} catch (HandledException e) {
			throw new CloneNotSupportedException();
		}
	}

	@Override
	public Ansi toSummary() {
		Ansi returnString = this.addCommonString();
		returnString.a("Status: ").a(completedToString(this.getCompleted()));
		returnString.a("\tDue At: ").a(this.dateToString(this.getDueTime()));
		return returnString.a('\n');
	}
	
	protected Ansi dateToString(Date date){
		Ansi returnString = ansi();
		DateFormat format = new SimpleDateFormat("yyyy/MM/dd");
		if (this.checkAlert()){
			returnString.fg(RED);
		} else {
			returnString.fg(GREEN);
		}
		returnString.a(format.format(date)).reset();
		return returnString;
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
		} else if (time[0] == null){
			return true;
		} else if (time[1] == null){
			return this.getDueTime().before(time[0]);
		} else {
			return this.getDueTime().after(time[0]) && this.getDueTime().before(time[1]);
		}
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
