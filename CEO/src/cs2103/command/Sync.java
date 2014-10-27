package cs2103.command;

import cs2103.TaskList;
import cs2103.exception.FatalException;
import cs2103.exception.HandledException;

public class Sync extends InfluentialCommand {
	private static final String SUCCESS = "Successfully sync your data with Google";
	private static final String FAILURE = "Fail to sync your data with Google";
	
	@Override
	public InfluentialCommand undo() throws HandledException, FatalException {
		return null;
	}

	@Override
	public InfluentialCommand redo() throws HandledException, FatalException {
		return null;
	}

	@Override
	public String execute() throws HandledException, FatalException {
		return TaskList.getInstance().syncWithGoogle()?SUCCESS:FAILURE;
	}

}
