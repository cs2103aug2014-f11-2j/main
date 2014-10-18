package cs2103.command;

import cs2103.FatalException;
import cs2103.HandledException;
import cs2103.Task;

public abstract class WriteCommand extends Command{
	protected Task undoBackup;
	protected Task redoBackup;
	
	public abstract WriteCommand undo() throws HandledException, FatalException;
	public abstract WriteCommand redo() throws HandledException, FatalException;
}
