package cs2103.command;

import java.util.Map;
import java.util.Queue;

import org.fusesource.jansi.Ansi;
import static org.fusesource.jansi.Ansi.*;
import static org.fusesource.jansi.Ansi.Color.*;
import cs2103.exception.FatalException;
import cs2103.exception.HandledException;
import cs2103.parameters.DeleteOption;
import cs2103.parameters.TaskID;
import cs2103.storage.TaskList;
import cs2103.task.Task;
import cs2103.util.CommonUtil;

public class Delete extends InfluentialCommand {
	private static final String MESSAGE_DELETE = "You have moved task with ID %1$d to trash\n";
	private static final String MESSAGE_PERMANENTLY_DELETE = "You have permanently deleted task with ID %1$d\n";
	private Task target;
	
	/**
	 * Creates an instance of Delete from user input command
	 * @param command
	 * @throws HandledException
	 * @throws FatalException
	 */
	public Delete(String command) throws HandledException, FatalException{
		CommonUtil.checkNull(command, HandledException.ExceptionType.INVALID_CMD);
		Queue<String> parameterQueue = separateCommand(command);
		this.parameterList.addParameter(TaskID.parse(parameterQueue.poll()));
		Map<String, String> parameterMap = separateParameters(parameterQueue);
		this.parameterList.addParameter(DeleteOption.parse(getParameterString(parameterMap, DeleteOption.allowedLiteral)));
		CommonUtil.checkNull(this.parameterList.getTaskID(), HandledException.ExceptionType.INVALID_CMD);
		this.target = TaskList.getInstance().getTaskByID(this.parameterList.getTaskID().getValue());
	}
	
	@Override
	public Ansi execute() throws HandledException, FatalException {
		CommonUtil.checkNull(this.target, HandledException.ExceptionType.INVALID_TASK_OBJ);
		Task deleting = cloneTask(this.target);
		this.undoBackup = this.target;
		if (this.parameterList.getDeleteOption() == null && !this.target.isDeleted()){
			deleting.delete();
			this.redoBackup = deleting;
			TaskList.getInstance().updateTask(deleting);
			return ansi().fg(GREEN).a(String.format(MESSAGE_DELETE, this.parameterList.getTaskID().getValue())).reset();
		} else {
			this.redoBackup = deleting;
			TaskList.getInstance().deleteTask(this.target);
			return ansi().fg(MAGENTA).a(String.format(MESSAGE_PERMANENTLY_DELETE, this.parameterList.getTaskID().getValue())).reset();
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
			if (this.parameterList.getDeleteOption() == null && !this.redoBackup.isDeleted()){
				this.redoBackup.updateLastModified(null);
				TaskList.getInstance().updateTask(this.redoBackup);
			} else {
				this.redoBackup.updateLastModified(null);
				TaskList.getInstance().deleteTask(this.redoBackup);
			}
			return this;
		}
	}
}
