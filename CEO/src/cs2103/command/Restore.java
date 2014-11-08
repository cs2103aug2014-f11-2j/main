package cs2103.command;

import org.fusesource.jansi.Ansi;

import static org.fusesource.jansi.Ansi.*;
import static org.fusesource.jansi.Ansi.Color.*;
import cs2103.exception.FatalException;
import cs2103.exception.HandledException;
import cs2103.parameters.TaskID;
import cs2103.storage.TaskList;
import cs2103.task.Task;
import cs2103.util.CommonUtil;
import cs2103.util.Logger;

public class Restore extends InfluentialCommand {
	private static final String MESSAGE_RESTORE = "You have successfully restored a task with ID %1$d\n";
	private static final String MESSAGE_RESTORE_FAIL = "Failed to restor the task with ID %1$d\n";
	private Task target;
	private final Logger logger;
	private static final String LOG_RESTORE = "Restore for Task %1$s successful";
	private static final String LOG_UNSUCCESSFUL_RESTORE = "Restore for Task %1$s was unsuccessful";
	
	/**
	 * Creates an instance of Restore from user input
	 * @param command
	 * @throws HandledException
	 * @throws FatalException
	 */
	public Restore(String command) throws HandledException, FatalException {
		this.logger = Logger.getInstance();
		this.parameterList.addParameter(TaskID.parse(command));
		CommonUtil.checkNull(this.parameterList.getTaskID(), HandledException.ExceptionType.INVALID_CMD);
		this.target = TaskList.getInstance().getTaskByID(this.parameterList.getTaskID().getValue());
		if (!this.target.isDeleted()){
			throw new HandledException(HandledException.ExceptionType.NOT_DELETED);
		}
	}
	
	@Override
	public Ansi execute() throws HandledException, FatalException {
		
		CommonUtil.checkNull(this.target, HandledException.ExceptionType.INVALID_TASK_OBJ);
		Ansi returnString = ansi();
		Task updating = cloneTask(this.target);
		updating.restore();
		updating = TaskList.getInstance().updateTask(updating);
		if (this.target == null){
			this.logger.writeLog(String.format(LOG_UNSUCCESSFUL_RESTORE, parameterList.getTaskID().getValue()));
			return returnString.fg(RED).a(String.format(MESSAGE_RESTORE_FAIL, parameterList.getTaskID().getValue())).reset();
		} else {
			this.undoBackup = this.target;
			this.redoBackup = updating;
			this.logger.writeLog(String.format(LOG_RESTORE, parameterList.getTaskID().getValue()));
			returnString.fg(GREEN).a(String.format(MESSAGE_RESTORE, parameterList.getTaskID().getValue())).reset();
			return returnString.a(updating.toDetail());
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
