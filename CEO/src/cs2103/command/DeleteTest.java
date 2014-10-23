package cs2103.command;

import static org.junit.Assert.*;

import org.junit.BeforeClass;
import org.junit.Test;

import cs2103.TaskList;
import cs2103.exception.FatalException;
import cs2103.exception.HandledException;

public class DeleteTest {

	@BeforeClass
	public static void initialise() throws HandledException, FatalException{
		TaskList.getInstance(null, false);
	}
	
	@Test
	public void testDelete() throws HandledException, FatalException {
		
		Add addObj = new Add("-title testDelete");
		addObj.execute();
		//returns You have successfully added a new task.
		Delete deleteObj = new Delete("1");
		assertEquals("You have deleted task with ID 1",deleteObj.execute());
	}
	
	@Test(expected = HandledException.class)
	public void testInvalidDelete() throws HandledException, FatalException{
		Delete deleteObj = new Delete("2");
		deleteObj.execute();
	}

}
