//@author A0112673L
package cs2103.command;

import static org.fusesource.jansi.Ansi.ansi;
import static org.junit.Assert.*;

import org.junit.BeforeClass;
import org.junit.Test;

import cs2103.exception.FatalException;
import cs2103.exception.HandledException;
import cs2103.parameters.Option;
import cs2103.storage.TaskList;
import cs2103.task.Task;

import cs2103.parameters.ParameterList;

public class ShowTest {
	@BeforeClass
	public static void initialise() throws HandledException, FatalException{
		TaskList.getInstance(new Option(Option.Value.TEST)).emptyTestList();;
		Add addObj = new Add("-title testShow -time 2014/10/23 20:20 to 2014/10/25 10:00 -location home -description much testing, such title");
		addObj.execute();
	}
	
	@Test
	public void testShow() throws HandledException, FatalException {
		
		Show show = new Show("1");
		ParameterList pl = show.getParameterList();
		assertEquals(1,pl.getTaskID().getValue());
		String result = show.execute().toString();
		Task t = TaskList.getInstance().getPeriodicList().get(0);
		assertEquals(ansi().a("The details for Task 1:\n").a(t.toDetail()).toString(),result);
	}
	
	@Test(expected = HandledException.class)
	public void testInvalidShow() throws HandledException, FatalException{
		Show show = new Show("2");
		show.execute();
	}

}
