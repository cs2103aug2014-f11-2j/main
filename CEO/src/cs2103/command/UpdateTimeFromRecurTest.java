package cs2103.command;

import static org.junit.Assert.*;

import org.junit.BeforeClass;
import org.junit.Test;

import cs2103.exception.FatalException;
import cs2103.exception.HandledException;
import cs2103.parameters.Option;
import cs2103.storage.TaskList;

public class UpdateTimeFromRecurTest {

	@BeforeClass
	public static void initialise() throws HandledException, FatalException{
		TaskList.getInstance(new Option(Option.Value.TEST));
		Add addObj = new Add("-title updateFromRecur -time 2014/10/18 23:00 to 2014/10/20 23:00 -recur 2d");
		addObj.execute();
	}
	
	@Test
	public void testUpdateFromRecur() throws HandledException, FatalException {
		
		UpdateTimeFromRecur ufr = new UpdateTimeFromRecur();
		String result = ufr.execute();
		assertEquals("Successfully updated 1 recurring tasks",result);
	}

}
