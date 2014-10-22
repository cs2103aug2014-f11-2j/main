package cs2103.command;

import static org.junit.Assert.*;

import org.junit.Test;

import cs2103.TaskList;
import cs2103.exception.FatalException;
import cs2103.exception.HandledException;

public class DeleteTest {

	@Test
	public void testDelete() throws HandledException, FatalException {
		
		TaskList.getInstance(null, false);
		Add addObj = new Add("-title testDelete");
		addObj.execute();
		//returns You have successfully added a new task.
		Delete deleteObj = new Delete("1");
		assertEquals("You have deleted task with ID 1",deleteObj.execute());
	}

}
