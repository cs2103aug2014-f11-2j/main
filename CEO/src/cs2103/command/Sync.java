package cs2103.command;

import cs2103.exception.FatalException;
import cs2103.exception.HandledException;
import cs2103.parameters.Option;
import cs2103.storage.TaskList;
import cs2103.util.CommonUtil;

public class Sync extends InfluentialCommand {
	private static final String SUCCESS = "Successfully sync your data with Google";
	private static final String FAILURE = "Fail to sync your data with Google";
	private static final String DISABLED = "You have disabled sync with Google";
	
	public Sync(String command) throws HandledException {
		if (command != null){
			this.parameterList.addParameter(Option.parse(new String[]{CommonUtil.removeDash(command)}));
		}
	}
	
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
		if (this.parameterList.getOption() != null && this.parameterList.getOption().getValue().equals(Option.Value.NOSYNC)){
			TaskList.getInstance().disableSync();
			return DISABLED;
		} else {
			return TaskList.getInstance().manualSync()?SUCCESS:FAILURE;
		}
	}
}
