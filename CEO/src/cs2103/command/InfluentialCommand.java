package cs2103.command;

import cs2103.TaskList;
import cs2103.exception.FatalException;
import cs2103.exception.HandledException;
import cs2103.task.Task;
import cs2103.util.CommonUtil;

public abstract class InfluentialCommand extends Command{
	protected Task undoBackup;
	protected Task redoBackup;
	
	public abstract InfluentialCommand undo() throws HandledException, FatalException;
	public abstract InfluentialCommand redo() throws HandledException, FatalException;
	
	protected String formatReturnString(String msg, Task task){
		StringBuffer sb = new StringBuffer();
		sb.append(msg).append(task.toDetail());
		return CommonUtil.deleteLastChar(sb);
	}
}
