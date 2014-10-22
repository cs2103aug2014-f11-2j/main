package cs2103.command;

import static org.junit.Assert.*;

import org.junit.Test;

import cs2103.TaskList;
import cs2103.exception.FatalException;
import cs2103.exception.HandledException;

public class AddTest {
	
	@Test
	public void testAddCorrectCommand() throws HandledException, FatalException {
		TaskList.getInstance(null,false);
		Add addObj;
		addObj = new Add("-title hello -description some description");
		assertEquals("You have successfully added a new task.",addObj.execute());
	
	}

}
