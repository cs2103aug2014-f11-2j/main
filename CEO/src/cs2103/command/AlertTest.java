//@author A0112673L
package cs2103.command;

import static org.fusesource.jansi.Ansi.ansi;
import static org.junit.Assert.*;

import org.junit.BeforeClass;
import org.junit.Test;

import cs2103.exception.FatalException;
import cs2103.exception.HandledException;
import cs2103.parameters.Option;
import cs2103.storage.TaskList;
import cs2103.task.Task;

public class AlertTest {
	
	//only displays alert for tasks due within one day of testing, 
	//so strings should be edited when running tests
	@BeforeClass
	public static void initialise() throws HandledException, FatalException {
		TaskList.getInstance(new Option(Option.Value.TEST));
		Add addobj = new Add("add -title testDeadlineAlert -time 2014/11/06 20:20");
		addobj.execute();
		addobj = new Add("add -title testPeriodicAlert -time 2014/11/06 20:20 to 2014/11/07 20:20");
		addobj.execute();	
	}
	
	@Test
	public void testAlert() throws HandledException, FatalException {
		Alert alert = new Alert();
		String result = alert.execute().toString();
		Task dead = TaskList.getInstance().getDeadlineList().get(0);
		Task period = TaskList.getInstance().getPeriodicList().get(0);
		assertEquals(ansi().a("Tasks due within one day:\n").a(dead.toSummary()).a("\n")
				.a("Tasks start within one day:\n").a(period.toSummary()).a("\n").toString(),result);
		
	}
}
