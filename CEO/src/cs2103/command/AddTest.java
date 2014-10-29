package cs2103.command;

import static org.junit.Assert.*;

import org.junit.BeforeClass;
import org.junit.Test;

import cs2103.TaskList;
import cs2103.exception.FatalException;
import cs2103.exception.HandledException;
import cs2103.parameters.Option;
import cs2103.parameters.ParameterList;
import cs2103.parameters.Recurrence;
import cs2103.parameters.Time;

public class AddTest {
	
	@BeforeClass 
	public static void initialise() throws HandledException, FatalException{
		TaskList.getInstance(new Option(Option.Value.TEST));
	}
	
	@Test
	public void testAddCorrectCommand() throws HandledException, FatalException {
		Add addObj;
		addObj = new Add("-title hello -description some description -time 2014/12/12 20:20 -recur 2d");
		ParameterList pl = addObj.getParameterList();
		assertEquals("hello",pl.getTitle().getValue());
		assertEquals("some description",pl.getDescription().getValue());
		assertEquals(Time.parse("2014/12/12 20:20").getValue()[0].toString(), pl.getTime().getValue()[0].toString());
		assertEquals(Recurrence.parse("2d").getValue().toString(), pl.getRecurrence().getValue().toString());
		String result = addObj.execute();
		
		assertEquals("You have successfully added a new task.\n" +
				"1. hello\n" +
				"Type: Deadline	Status: Needs Action	Due At: 12-Dec-2014 20:20:00\n" +
				"Description: some description",result);
	}
	
	@SuppressWarnings("unused")
	@Test(expected = HandledException.class)
	public void testAddNullCommand() throws HandledException{
		Add addObj = new Add(null);
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
		addObj.undo();
		assertEquals("1. undo\n" +
				"Type: Floating	Status: Needs Action",result);
	}
}
