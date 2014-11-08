//@author A0112673L
package cs2103.command;

import org.fusesource.jansi.Ansi;
import static org.fusesource.jansi.Ansi.*;
import static org.fusesource.jansi.Ansi.Color.*;
import cs2103.exception.FatalException;
import cs2103.exception.HandledException;
import cs2103.parameters.Option;
import cs2103.storage.TaskList;
import cs2103.util.CommonUtil;

public class Sync extends InfluentialCommand {
	private static final Ansi SUCCESS = ansi().fg(GREEN).a("Successfully sync your data with Google\n").reset();
	private static final Ansi FAILURE = ansi().fg(RED).a("Fail to sync your data with Google\n").reset();
	private static final Ansi DISABLED = ansi().fg(MAGENTA).a("You have disabled sync with Google\n").reset();
	
	/**
	 * Creates an instance of sync from user input
	 * @param command
	 * @throws HandledException
	 */
	public Sync(String command) throws HandledException {
		if (command != null) {
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
	public Ansi execute() throws HandledException, FatalException {
		if (this.parameterList.getOption() != null && this.parameterList.getOption().getValue().equals(Option.Value.NOSYNC)) {
			TaskList.getInstance().disableSync();
			return DISABLED;
		} else {
			return TaskList.getInstance().manualSync()?SUCCESS:FAILURE;
		}
	}
}
