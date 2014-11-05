package cs2103.task;

import java.util.Date;

import org.fusesource.jansi.Ansi;
import cs2103.exception.HandledException;
import net.fortuna.ical4j.model.component.VToDo;
import net.fortuna.ical4j.model.property.Status;

public class FloatingTask extends ToDoTask {
	
	public FloatingTask(String taskUID, Status status) {
		super(taskUID, status);
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
		FloatingTask newTask = new FloatingTask(this.getTaskUID(), this.getStatus());
		updateNewTask(newTask);
		return newTask;
	}

	private void updateNewTask(Task newTask) {
		newTask.updateTitle(this.getTitle());
		newTask.updateCreated(this.getCreated());
		newTask.updateDescription(this.getDescription());
		if (!(newTask instanceof PeriodicTask)) {
			newTask.updateCompleted(this.getCompleted());
		}
	}

	private DeadlineTask toDeadline(Date dueTime) throws HandledException {
		DeadlineTask newTask = new DeadlineTask(this.getTaskUID(), Status.VTODO_NEEDS_ACTION, dueTime);
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
		FloatingTask newTask = new FloatingTask(this.getTaskUID(), this.getStatus());
		updateNewTask(newTask);
		newTask.updateLastModified(null);
		return newTask;
	}

	@Override
	public Ansi toSummary() {
		Ansi returnString = this.addCommonString();
		formatStatus(returnString);
		return returnString;
	}

	private void formatStatus(Ansi returnString) {
		returnString.a("Status: ").a(completedToString(this.getCompleted())).a('\n');
	}
	
	@Override
	public boolean checkPeriod(Date[] time) {
		return false;
	}

	@Override
	protected VToDo toVToDo() {
		VToDo vToDo = new VToDo(this.getCreated(), this.getTitle());
		this.addVToDoProperty(vToDo);
		return vToDo;
	}
	
	@Override
	public com.google.api.services.tasks.model.Task toGTask(){
		com.google.api.services.tasks.model.Task gTask = new com.google.api.services.tasks.model.Task();
		this.addGTaskProperty(gTask);
		return gTask;
	}
}
