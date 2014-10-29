package cs2103.command;

import static org.junit.Assert.*;

import org.junit.BeforeClass;
import org.junit.Test;

import cs2103.TaskList;
import cs2103.exception.FatalException;
import cs2103.exception.HandledException;
import cs2103.parameters.Option;
import cs2103.parameters.ParameterList;
import cs2103.parameters.Time;

public class UpdateTest {
	
	@BeforeClass
	public static void initialise() throws HandledException, FatalException{
		TaskList.getInstance(new Option(Option.Value.TEST));
		Add addObj = new Add("-title updateTest");
		addObj.execute();
	}

	@Test
	public void testUpdate() throws HandledException, FatalException {
		
		Update updateTitle = new Update("1 -title I was wrong -description can't remember -location earth" +
				" -time 2014/12/25 20:20 to 2014/12/31 23:59 -recur 2d");
		ParameterList pl = updateTitle.getParameterList();
		assertEquals("I was wrong",pl.getTitle().getValue());
		assertEquals("can't remember",pl.getDescription().getValue());
		assertEquals("earth",pl.getLocation().getValue());
		assertEquals(Time.parse("2014/12/25 20:20 to 2014/12/31 23:59").getValue()[0].toString(),pl.getTime().getValue()[0].toString());
		assertNotNull(pl.getRecurrence());
		String result = updateTitle.execute();
		assertEquals("You have updated task with ID 1\n" +
				"1. I was wrong\n" +
				"Type: Recurring	From: 25-Dec-2014 20:20:00 To 31-Dec-2014 23:59:00\n" +
				"Recurrence: 2 DAILY\n" +
				"Location: earth\n" +
				"Description: can't remember",result);
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
