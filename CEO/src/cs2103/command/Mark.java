package cs2103.command;

import java.util.Date;

import org.fusesource.jansi.Ansi;

import static org.fusesource.jansi.Ansi.*;
import static org.fusesource.jansi.Ansi.Color.*;
import cs2103.exception.FatalException;
import cs2103.exception.HandledException;
import cs2103.parameters.TaskID;
import cs2103.storage.TaskList;
import cs2103.task.PeriodicTask;
import cs2103.task.Task;
import cs2103.util.CommonUtil;
import cs2103.util.Logger;

public class Mark extends InfluentialCommand {
	private static final String MESSAGE_MARK = "Successfully marked task %1$d as completed\n";
	private static final String MESSAGE_MARK_FAILED = "Failed to mark task %1$d as completed\n";
	private static final String MESSAGE_MARK_NOTSUPPORTED = "Task %1$d does not support mark operation\n";
	private Task target;	
	private final Logger logger;
	private static final String LOG_MARK = "Mark Task %1$s executed";
	private static final String LOG_UNSUPPORTED_MARK = "Task %1$s does not support mark";
	
	/**
	 * Creates an instance of Mark from user input
	 * @param command
	 * @throws HandledException
	 * @throws FatalException
	 */
	public Mark(String command) throws HandledException, FatalException{
		this.logger = Logger.getInstance();
		this.parameterList.addParameter(TaskID.parse(command));
		CommonUtil.checkNull(this.parameterList.getTaskID(), HandledException.ExceptionType.INVALID_TASKID);
		this.target = TaskList.getInstance().getTaskByID(this.parameterList.getTaskID().getValue());
	}

	@Override
	public Ansi execute() throws HandledException, FatalException {
		CommonUtil.checkNull(this.target, HandledException.ExceptionType.INVALID_TASK_OBJ);
		if (this.target instanceof PeriodicTask){
			this.logger.writeLog(String.format(LOG_UNSUPPORTED_MARK, this.parameterList.getTaskID().getValue()));
			return ansi().fg(RED).a(String.format(MESSAGE_MARK_NOTSUPPORTED, this.parameterList.getTaskID().getValue())).reset();
		} else {
			Task newTask = cloneTask(this.target);
			newTask.updateCompleted(new Date());
			newTask = TaskList.getInstance().updateTask(newTask);
			this.logger.writeLog(String.format(LOG_MARK, this.parameterList.getTaskID().getValue()));
			return this.formatReturnString(newTask);
		}
	}
	
	/**
	 * @param newTask
	 * @return Ansi formatted result string for Mark command
	 * @throws HandledException
	 */
	private Ansi formatReturnString(Task newTask) throws HandledException{
		Ansi returnString = ansi();
		if (newTask == null){
			return returnString.fg(RED).a(String.format(MESSAGE_MARK_FAILED, this.parameterList.getTaskID().getValue())).reset();
		} else {
			this.undoBackup = this.target;
			this.redoBackup = newTask;
			returnString.fg(GREEN).a(String.format(MESSAGE_MARK, this.parameterList.getTaskID().getValue())).reset();
			return returnString.a(newTask.toDetail());
		}
	}

	@Override
	public InfluentialCommand undo() throws HandledException, FatalException {
		if (this.undoBackup == null){
			return null;
		} else {
			this.undoBackup.updateLastModified(null);
			TaskList.getInstance().updateTask(this.undoBackup);
			return this;
		}
	}
	
	@Override
	public InfluentialCommand redo() throws HandledException, FatalException {
		if (this.redoBackup == null){
			return null;
		} else {
			this.redoBackup.updateLastModified(null);
			TaskList.getInstance().updateTask(this.redoBackup);
			return this;
		}
	}
}
