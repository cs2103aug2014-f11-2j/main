package cs2103.command;

import static org.junit.Assert.*;

import org.junit.Test;

import cs2103.TaskList;
import cs2103.exception.FatalException;
import cs2103.exception.HandledException;

public class ShowTest {

	@Test
	public void testShow() throws HandledException, FatalException {
		TaskList.getInstance(null, false);
		Add addObj = new Add("-title testShow -time 2014/10/23 20:20 to 2014/10/25 10:00 -location home -description much testing, such title");
		addObj.execute();
		Show show = new Show("1");
		String result = show.execute();
		assertEquals("The details for Task 1:\n" +
				"1. testShow\n" +
				"Type: Periodic	From: 23-Oct-2014 20:20:00 To 25-Oct-2014 10:00:00\n" +
				"Location: home\n" +
				"Description: much testing, such title",result);
	}

}
