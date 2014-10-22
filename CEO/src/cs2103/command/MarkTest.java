package cs2103.command;

import static org.junit.Assert.*;

import org.junit.Test;

import cs2103.TaskList;
import cs2103.exception.FatalException;
import cs2103.exception.HandledException;

public class MarkTest {

	@Test
	public void testMarkFloating() throws HandledException, FatalException {
		TaskList.getInstance(null, false);
		Add addObj = new Add("add -title floating");
		addObj.execute();
		addObj = new Add("add -title deadline -time 2014/10/23 20:20");
		addObj.execute();
		addObj = new Add("add -title periodic -time 2014/10/23 20:20 to 2014/10/25 20:20");
		addObj.execute();
		
		Mark markFloating = new Mark("1");
		assertEquals("Successfully marked 1 as completed",markFloating.execute());
	}
	
	public void testMarkPeriodic() throws HandledException, FatalException{
		TaskList.getInstance();
		
		Mark markPeriodic = new Mark("2");
		assertEquals("The task you specified does not contain status information",markPeriodic.execute());
	}
	
	public void testMarkDeadline() throws HandledException, FatalException{
		TaskList.getInstance();
		
		Mark markDeadline = new Mark("3");
		assertEquals("Successfully marked 3 as completed",markDeadline.execute());
	}
	
}
