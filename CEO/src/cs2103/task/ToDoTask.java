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
	
	@Override
	public DateTime getCompleted(){
		return this.completed;
	}
	
	@Override
	public void updateCompleted(Date completed){
		if (completed == null){
			this.completed = null;
		} else {
			this.completed = new DateTime(completed);
		}
	}
	
	@Override
	protected void updateStatus(Status status){
		if (status == null){
			this.status = Status.VTODO_NEEDS_ACTION;
		} else {
			this.status = status;
		}
	}
	
	@Override
	public void updateLocation(String location) {
		return;
	}

	@Override
	public void updateRecurrence(Recur recurrence) {
		return;
	}
	
	@Override
	public boolean isDeleted() {
		return this.getStatus().equals(Status.VTODO_CANCELLED);
	}

	@Override
	public void delete() {
		this.updateStatus(Status.VTODO_CANCELLED);
	}

	@Override
	public void restore() {
		if (this.getCompleted() == null){
			this.updateStatus(Status.VTODO_NEEDS_ACTION);
		} else {
			this.updateStatus(Status.VTODO_COMPLETED);
		}
	}
	
	@Override
	public Component toComponent() {
		return this.toVToDo();
	}
	
	@Override
	public Ansi toDetail() {
		Ansi returnString = this.toSummary();
		formatDescription(returnString);
		return returnString;
	}

	private void formatDescription(Ansi returnString) {
		returnString.a(STRING_DESCRIPTION).a(this.getDescription()).a('\n').reset();
	}
	
	/**
	 * Adds necessary properties to VToDo, necessary for Google Sync 
	 */
	protected void addVToDoProperty(VToDo vToDo){
		this.addCommonProperty(vToDo);
		if (this.isDeleted()){
			vToDo.getProperties().add(Status.VTODO_CANCELLED);
		} else if (this.getCompleted() == null){
			vToDo.getProperties().add(Status.VTODO_NEEDS_ACTION);
		} else {
			vToDo.getProperties().add(Status.VTODO_COMPLETED);
			vToDo.getProperties().add(new Completed(this.getCompleted()));
		}
	}
	
	/**
	 * Adds necessary properties necessary for sync with Google Task
	 */
	protected void addGTaskProperty(com.google.api.services.tasks.model.Task gTask) {
		gTask.setTitle(this.getTitle());
		if (this.getCompleted() == null) {
			gTask.setCompleted(Data.NULL_DATE_TIME);
			gTask.setStatus("needsAction");
		} else {
			gTask.setCompleted(new com.google.api.client.util.DateTime(this.getCompleted().getTime()));
			gTask.setStatus("completed");
		}
		gTask.setNotes(this.getDescription());
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
	
	public abstract com.google.api.services.tasks.model.Task toGTask();
	protected abstract VToDo toVToDo();

	protected void updateNewTask(Task newTask) {
		newTask.updateTitle(this.getTitle());
		newTask.updateCreated(this.getCreated());
		newTask.updateDescription(this.getDescription());
		if (!(newTask instanceof PeriodicTask)) {
			newTask.updateCompleted(this.getCompleted());
		}
	}

	protected void updateClone(ToDoTask newTask) {
		updateNewTask(newTask);
		newTask.updateLastModified(null);
	}

	protected void formatStatus(Ansi returnString) {
		returnString.a("Status: ").a(completedToString(this.getCompleted()));
	}
}
