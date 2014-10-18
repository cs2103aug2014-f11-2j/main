package cs2103.command;

import cs2103.FatalException;
import cs2103.HandledException;
import cs2103.Task;

public abstract class InfluentialCommand extends Command{
	protected Task undoBackup;
	protected Task redoBackup;
	
	public abstract InfluentialCommand undo() throws HandledException, FatalException;
	public abstract InfluentialCommand redo() throws HandledException, FatalException;
}
