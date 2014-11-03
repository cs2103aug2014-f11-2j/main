package cs2103.command;

import static org.fusesource.jansi.Ansi.ansi;
import static org.fusesource.jansi.Ansi.Color.MAGENTA;
import static org.junit.Assert.*;

import org.junit.BeforeClass;
import org.junit.Test;

import cs2103.exception.FatalException;
import cs2103.exception.HandledException;
import cs2103.parameters.Option;
import cs2103.storage.TaskList;

import cs2103.parameters.ParameterList;
import cs2103.task.FloatingTask;
import cs2103.task.Task;
import cs2103.util.TestUtil;

public class DeleteTest {

	@BeforeClass
	public static void initialise() throws HandledException, FatalException{
		TaskList.getInstance(new Option(Option.Value.TEST));
	}
	
	@Test
	public void testPermanentDelete() throws HandledException, FatalException {
		
		Add addObj = new Add("-title testDelete");
		addObj.execute();
		Delete deleteObj = new Delete("1 -p");
		ParameterList pl = deleteObj.getParameterList();
		assertEquals(true,pl.getDeleteOption().getValue());
		assertEquals(1,pl.getTaskID().getValue());
		String result = deleteObj.execute().toString();
		assertEquals(ansi().fg(MAGENTA).a("You have permanently deleted task with ID 1\n").reset().toString(),result);
		
	}
	
	//This test test temp delete with one valid input (taskiD - 1) and one invalid input (param - m)
	@Test
	public void testTempDelete() throws HandledException, FatalException{
		Add addObj = new Add("-title tempDelete");
		addObj.execute();
		Delete deleteObj = new Delete("1 -m");
		deleteObj.execute();
		Task task = new FloatingTask(null, null);
		task.updateDescription(null);
		task.updateLastModified(null);
		for(Task t : TaskList.getInstance().getTrashList()){
			TestUtil.compareTasks(task, t);
		}
	}
	
	@Test(expected = HandledException.class)
	public void testInvalidDelete() throws HandledException, FatalException{
		Delete deleteObj = new Delete("2");
		deleteObj.execute();
	}

}
