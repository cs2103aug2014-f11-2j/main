//@author A0116713M
package cs2103.command;

import cs2103.exception.FatalException;
import cs2103.exception.HandledException;
import cs2103.task.Task;

public abstract class InfluentialCommand extends Command {
	protected Task undoBackup;
	protected Task redoBackup;
	
	/**
	 * @return Command object of caller casted as InfluentialCommand
	 * @throws HandledException
	 * @throws FatalException
	 */
	public abstract InfluentialCommand undo() throws HandledException, FatalException;
	
	/**
	 * @return Command object of caller casted as InfluentialCommand
	 * @throws HandledException
	 * @throws FatalException
	 */
	public abstract InfluentialCommand redo() throws HandledException, FatalException;
	
	protected static Task cloneTask(Task task) throws HandledException {
		try {
			Task newTask = (Task) task.clone();
			return newTask;
		} catch (CloneNotSupportedException e) {
			throw new HandledException(HandledException.ExceptionType.CLONE_FAILED);
		}
	}
}
