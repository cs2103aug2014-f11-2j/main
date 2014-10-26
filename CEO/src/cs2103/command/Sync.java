package cs2103.command;

import cs2103.TaskList;
import cs2103.exception.FatalException;
import cs2103.exception.HandledException;

public class Sync extends InfluentialCommand {
	
	@Override
	public InfluentialCommand undo() throws HandledException, FatalException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public InfluentialCommand redo() throws HandledException, FatalException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String execute() throws HandledException, FatalException {
		TaskList.getInstance().syncWithGoogle();
		return "Successful";
	}

}
