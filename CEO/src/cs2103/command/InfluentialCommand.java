package cs2103.command;

import cs2103.exception.FatalException;
import cs2103.exception.HandledException;
import cs2103.task.Task;

public abstract class InfluentialCommand extends Command{
	protected Task undoBackup;
	protected Task redoBackup;
	
	public abstract InfluentialCommand undo() throws HandledException, FatalException;
	public abstract InfluentialCommand redo() throws HandledException, FatalException;
}
