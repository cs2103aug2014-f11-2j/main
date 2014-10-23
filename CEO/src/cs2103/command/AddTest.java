package cs2103.command;

import static org.junit.Assert.*;

import org.junit.BeforeClass;
import org.junit.Test;

import cs2103.TaskList;
import cs2103.exception.FatalException;
import cs2103.exception.HandledException;

public class AddTest {
	
	@BeforeClass 
	public static void initialise() throws HandledException, FatalException{
		TaskList.getInstance(null,false);
	}
	
	@Test
	public void testAddCorrectCommand() throws HandledException, FatalException {
		Add addObj;
		addObj = new Add("-title hello -description some description");
		assertEquals("You have successfully added a new task.",addObj.execute());
	}
	
	@Test(expected = HandledException.class)
	public void testAddNullCommand() throws HandledException{
		Add addObj = new Add(null);
	}
	
	@Test(expected = HandledException.class)
	public void testAddInvalidCommand() throws HandledException, FatalException{
		Add addObj = new Add("-hello");
		addObj.execute();
	}
	
	@Test
	public void testAddUndoRedo() throws HandledException, FatalException{
		Add addObj = new Add("-title undo");
		addObj.execute();
		addObj.undo();
		Search s = new Search("all -keyword undo");
		String result = s.execute();
		assertEquals("The task list is empty",result);
		addObj.redo();
		result = s.execute();
		assertEquals("2. undo\n" +
				"Type: Floating	Status: Needs Action",result);
	}
}
