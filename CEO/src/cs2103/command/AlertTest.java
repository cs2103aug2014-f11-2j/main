package cs2103.command;

import static org.junit.Assert.*;

import org.junit.Test;

import cs2103.TaskList;
import cs2103.exception.FatalException;
import cs2103.exception.HandledException;

public class AlertTest {

	//actual changes based on date, so strings should be edited when running tests
	@Test
	public void testAlert() throws HandledException, FatalException {
		TaskList.getInstance(null, false);
		Add addobj = new Add("add -title testDeadlineAlert -time 2014/10/22 20:20");
		addobj.execute();
		addobj = new Add("add -title testPeriodicAlert -time 2014/10/22 20:20 to 2014/10/24 20:20");
		addobj.execute();
		Alert alert = new Alert();
		assertEquals("Tasks due within one day:\n" +
				"1. testDeadlineAlert\n" +
				"Type: Deadline	Status: Needs Action	Due At: 22-Oct-2014 20:20:00" +
				"Tasks start within one day:\n" +
				"2. testPeriodicAlert\n" +
				"Type: Periodic\tFrom: 22-Oct-2014 20:20:00 To 24-Oct-2014 20:20:00",alert.execute());
		
	}
}
