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
		returnString.a(STRING_DESCRIPTION).a(this.getDescription()).a('\n').reset();
		return returnString;
	}
	
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
	
	protected void addGTaskProperty(com.google.api.services.tasks.model.Task gTask){
		gTask.setTitle(this.getTitle());
		if (this.getCompleted() == null){
			gTask.setCompleted(Data.NULL_DATE_TIME);
			gTask.setStatus("needsAction");
		} else {
			gTask.setCompleted(new com.google.api.client.util.DateTime(this.getCompleted().getTime()));
			gTask.setStatus("completed");
		}
		gTask.setNotes(this.getDescription());
	}
	
	protected static Ansi completedToString(DateTime completed){
		Ansi returnString = ansi();
		if (completed == null){
			returnString.fg(RED).a("Needs Action");
		} else {
			returnString.fg(GREEN).a("Completed");
		}
		return returnString.reset();
	}
	
	@Override
	public boolean matches(String keyword) {
		if (keyword == null || keyword.isEmpty()){
			return true;
		} else {
			return StringUtils.containsIgnoreCase(this.getTitle(), keyword) || StringUtils.containsIgnoreCase(this.getDescription(), keyword);
		}
	}
	
	public abstract com.google.api.services.tasks.model.Task toGTask();
	protected abstract VToDo toVToDo();
}
