package cs2103.command;

import static org.junit.Assert.*;

import org.junit.BeforeClass;
import org.junit.Test;

import cs2103.TaskList;
import cs2103.exception.FatalException;
import cs2103.exception.HandledException;
import cs2103.parameters.Option;
import cs2103.parameters.ParameterList;

public class DeleteTest {

	@BeforeClass
	public static void initialise() throws HandledException, FatalException{
		TaskList.getInstance(new Option(Option.Value.TEST));
	}
	
	@Test
	public void testPermanentDelete() throws HandledException, FatalException {
		
		Add addObj = new Add("-title testDelete");
		addObj.execute();
		//returns You have successfully added a new task.
		Delete deleteObj = new Delete("1 -p");
		ParameterList pl = deleteObj.getParameterList();
		assertEquals(true,pl.getDeleteOption().getValue());
		assertEquals(1,pl.getTaskID().getValue());
		String result = deleteObj.execute();
		assertEquals("You have permanently deleted task with ID 1",result);
	}
	
	@Test
	public void testTempDelete() throws HandledException, FatalException{
		Add addObj = new Add("-title tempDelete");
		addObj.execute();
		Delete deleteObj = new Delete("1");
		String result = deleteObj.execute();
		assertEquals("You have moved task with ID 1 to trash",result);
	}
	
	@Test(expected = HandledException.class)
	public void testInvalidDelete() throws HandledException, FatalException{
		Delete deleteObj = new Delete("2");
		deleteObj.execute();
	}

}
