package cs2103.task;

import java.util.Date;

import org.fusesource.jansi.Ansi;

import cs2103.exception.HandledException;
import cs2103.util.Logger;
import net.fortuna.ical4j.model.component.VToDo;
import net.fortuna.ical4j.model.property.Status;

public class FloatingTask extends ToDoTask {
	private final Logger logger;
	
	public FloatingTask(String taskUID, Status status) {
		super(taskUID, status);
		this.logger = Logger.getInstance();
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
		ToDoTask newTask = new FloatingTask(this.getTaskUID(), this.getStatus());
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
		PeriodicTask newTask = new PeriodicTask(this.getTaskUID(), Status.VEVENT_CONFIRMED, startTime, endTime);
		assert(newTask != null);
		updateNewTask(newTask);
		return newTask;
	}

	/* (non-Javadoc)
	 * @see cs2103.task.Task#clone()
	 */
	@Override
	public Object clone() throws CloneNotSupportedException {
		ToDoTask newTask = new FloatingTask(this.getTaskUID(), this.getStatus());
		assert(newTask != null);
		updateClone(newTask);
		return newTask;
	}

	/* (non-Javadoc)
	 * @see cs2103.task.Task#toSummary()
	 */
	@Override
	public Ansi toSummary() {
		Ansi returnString = this.addCommonString();
		this.formatStatus(returnString);
		return returnString.a('\n');
	}

	/* (non-Javadoc)
	 * @see cs2103.task.Task#checkPeriod(java.util.Date[])
	 */
	@Override
	public boolean checkPeriod(Date[] time) {
		return false;
	}

	/* (non-Javadoc)
	 * @see cs2103.task.ToDoTask#toVToDo()
	 */
	@Override
	protected VToDo toVToDo() {
		VToDo vToDo = new VToDo(this.getCreated(), this.getTitle());
		this.addVToDoProperty(vToDo);
		return vToDo;
	}
	
	/* (non-Javadoc)
	 * @see cs2103.task.ToDoTask#toGTask()
	 */
	@Override
	public com.google.api.services.tasks.model.Task toGTask() {
		com.google.api.services.tasks.model.Task gTask = new com.google.api.services.tasks.model.Task();
		this.addGTaskProperty(gTask);
		gTask.setNotes(this.getDescription());
		return gTask;
	}
}
