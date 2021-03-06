//@author A0128478R
package cs2103.task;

import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.fusesource.jansi.Ansi;

import com.google.api.client.util.Data;

import static org.fusesource.jansi.Ansi.*;
import static org.fusesource.jansi.Ansi.Color.*;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.Recur;
import net.fortuna.ical4j.model.component.VToDo;
import net.fortuna.ical4j.model.property.Completed;
import net.fortuna.ical4j.model.property.Status;

/**
 *  Contains inherited methods from Task
 *  Extends to concrete Task class FloatingTask and DeadlineTask
 */
public abstract class ToDoTask extends Task {
	private DateTime completed;
	
	public ToDoTask(String taskUID, Status status) {
		super(taskUID);
		this.updateStatus(status);
	}
	
	/* (non-Javadoc)
	 * @see cs2103.task.Task#getCompleted()
	 */
	@Override
	public DateTime getCompleted() {
		return this.completed;
	}
	
	/* (non-Javadoc)
	 * @see cs2103.task.Task#updateCompleted(java.util.Date)
	 */
	@Override
	public void updateCompleted(Date completed) {
		if (completed == null) {
			this.completed = null;
		} else {
			this.completed = new DateTime(completed);
		}
	}
	
	/* (non-Javadoc)
	 * @see cs2103.task.Task#updateStatus(net.fortuna.ical4j.model.property.Status)
	 */
	@Override
	protected void updateStatus(Status status) {
		if (status == null) {
			this.status = Status.VTODO_NEEDS_ACTION;
		} else {
			this.status = status;
		}
	}
	
	/* (non-Javadoc)
	 * @see cs2103.task.Task#updateLocation(java.lang.String)
	 */
	@Override
	public void updateLocation(String location) {
		return;
	}

	/* (non-Javadoc)
	 * @see cs2103.task.Task#updateRecurrence(net.fortuna.ical4j.model.Recur)
	 */
	@Override
	public void updateRecurrence(Recur recurrence) {
		return;
	}
	
	/* (non-Javadoc)
	 * @see cs2103.task.Task#isDeleted()
	 */
	@Override
	public boolean isDeleted() {
		return this.getStatus().equals(Status.VTODO_CANCELLED);
	}

	/* (non-Javadoc)
	 * @see cs2103.task.Task#delete()
	 */
	@Override
	public void delete() {
		this.updateStatus(Status.VTODO_CANCELLED);
	}

	/* (non-Javadoc)
	 * @see cs2103.task.Task#restore()
	 */
	@Override
	public void restore() {
		if (this.getCompleted() == null){
			this.updateStatus(Status.VTODO_NEEDS_ACTION);
		} else {
			this.updateStatus(Status.VTODO_COMPLETED);
		}
	}
	
	/* (non-Javadoc)
	 * @see cs2103.task.Task#toComponent()
	 */
	@Override
	public Component toComponent() {
		return this.toVToDo();
	}
	
	/* (non-Javadoc)
	 * @see cs2103.task.Task#toDetail()
	 */
	@Override
	public Ansi toDetail() {
		Ansi returnString = this.toSummary();
		formatDescription(returnString);
		return returnString;
	}

	private void formatDescription(Ansi returnString) {
		returnString.a(STRING_DESCRIPTION).a(this.getDescription()).a('\n').reset();
	}
	
	protected void addVToDoProperty(VToDo vToDo) {
		assert(vToDo != null);
		this.addCommonProperty(vToDo);
		if (this.isDeleted()) {
			vToDo.getProperties().add(Status.VTODO_CANCELLED);
		} else if (this.getCompleted() == null) {
			vToDo.getProperties().add(Status.VTODO_NEEDS_ACTION);
		} else {
			vToDo.getProperties().add(Status.VTODO_COMPLETED);
			vToDo.getProperties().add(new Completed(this.getCompleted()));
		}
	}
	
	protected void addGTaskProperty(com.google.api.services.tasks.model.Task gTask) {
		assert(gTask != null);
		gTask.setTitle(this.getTitle());
		if (this.getCompleted() == null) {
			gTask.setCompleted(Data.NULL_DATE_TIME);
			gTask.setStatus("needsAction");
		} else {
			gTask.setCompleted(new com.google.api.client.util.DateTime(this.getCompleted().getTime()));
			gTask.setStatus("completed");
		}
	}
	
	protected static Ansi completedToString(DateTime completed) {
		Ansi returnString = ansi();
		formatCompleted(completed, returnString);
		return returnString.reset();
	}

	private static void formatCompleted(DateTime completed, Ansi returnString) {
		if (completed == null) {
			formatCompletedNeedsAction(returnString);
		} else {
			formatCompletedIsCompleted(returnString);
		}
	}

	private static Ansi formatCompletedIsCompleted(Ansi returnString) {
		return returnString.bold().fg(GREEN).a("Completed");
	}

	private static Ansi formatCompletedNeedsAction(Ansi returnString) {
		return returnString.bold().fg(RED).a("Needs Action");
	}
	
	/* (non-Javadoc)
	 * @see cs2103.task.Task#matches(java.lang.String)
	 */
	@Override
	public boolean matches(String keyword) {
		if (isEmptyKeyword(keyword)) {
			return true;
		} else {
			return containsKeywordInTask(keyword);
		}
	}

	private boolean containsKeywordInTask(String keyword) {
		return containsKeywordInTitle(keyword) || containsKeywordInDescription(keyword);
	}

	private boolean containsKeywordInDescription(String keyword) {
		return StringUtils.containsIgnoreCase(this.getDescription(), keyword);
	}

	private boolean containsKeywordInTitle(String keyword) {
		return StringUtils.containsIgnoreCase(this.getTitle(), keyword);
	}
	
	private boolean isEmptyKeyword(String keyword) {
		return keyword == null || keyword.isEmpty();
	}
	
	/**
	 * @return a Google Task object which is needed for Google Sync
	 */
	public abstract com.google.api.services.tasks.model.Task toGTask();
	protected abstract VToDo toVToDo();

	protected void updateNewTask(Task newTask) {
		newTask.updateTitle(this.getTitle());
		newTask.updateCreated(this.getCreated());
		newTask.updateDescription(this.getDescription());
		newTask.updateCompleted(this.getCompleted());
	}

	protected void updateClone(ToDoTask newTask) {
		updateNewTask(newTask);
		newTask.updateLastModified(null);
	}

	protected void formatStatus(Ansi returnString) {
		returnString.a("Status: ").a(completedToString(this.getCompleted()));
	}
}
