package cs2103.command;

import static org.junit.Assert.*;

import org.junit.BeforeClass;
import org.junit.Test;

import cs2103.TaskList;
import cs2103.exception.FatalException;
import cs2103.exception.HandledException;
import cs2103.parameters.Option;

public class UpdateTest {
	
	@BeforeClass
	public static void initialise() throws HandledException, FatalException{
		TaskList.getInstance(new Option(Option.Value.TEST));
		Add addObj = new Add("-title updateTest");
		addObj.execute();
	}

	@Test
	public void testUpdate() throws HandledException, FatalException {
		
		Update updateTitle = new Update("1 -title I was wrong");
		String result = updateTitle.execute();
		assertEquals("You have updated task with ID 1",result);
		List list = new List("floating");
		result = list.execute();
		
		assertEquals("1. I was wrong\n" +
				"Type: Floating	Status: Needs Action",result);
	}
	
	@Test(expected = HandledException.class)
	public void testInvalidUpdate() throws HandledException, FatalException{
		Update updateInvalid = new Update("-title this is wrong, so much wrong");
		updateInvalid.execute();
	}
	
	@Test
	public void testUpdateRedoUndo() throws HandledException, FatalException{
		Add addObj = new Add("-title muchness title -time 2014/11/29 20:20");
		addObj.execute();
		Update updateRedo = new Update("2 -title much wrong, very title");
		updateRedo.execute();
		updateRedo.undo();
		List list = new List("deadline");
		String result = list.execute();
		assertEquals("2. muchness title\n" +
				"Type: Deadline	Status: Needs Action	Due At: 29-Nov-2014 20:20:00",result);
		updateRedo.redo();
		result = list.execute();
		assertEquals("2. much wrong, very title\n" +
				"Type: Deadline	Status: Needs Action	Due At: 29-Nov-2014 20:20:00",result);
	}
}
