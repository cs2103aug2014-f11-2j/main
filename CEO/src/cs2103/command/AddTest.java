package cs2103.command;

import static org.junit.Assert.*;

import org.junit.Test;

import cs2103.exception.FatalException;
import cs2103.exception.HandledException;

public class AddTest {
	
	@Test
	public void testAddCorrectCommand() throws HandledException, FatalException {
		Add addObj;
		addObj = new Add("add --title hello --description some description");
		assertEquals("You have successfully added a new task.",addObj.execute());
	
	}
	
	@Test
	public void testAddIncorrectCommand() throws HandledException, FatalException{
		Add addobj;
		addobj = new Add("add");
		assertEquals("You have successfully added a new task.",addobj.execute());
	}

}
