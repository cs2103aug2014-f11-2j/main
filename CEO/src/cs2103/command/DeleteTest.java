//@author A0112673L
package cs2103.command;

import static org.fusesource.jansi.Ansi.ansi;
import static org.fusesource.jansi.Ansi.Color.MAGENTA;
import static org.fusesource.jansi.Ansi.Color.RED;
import static org.junit.Assert.*;

import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import cs2103.exception.FatalException;
import cs2103.exception.HandledException;
import cs2103.parameters.Option;
import cs2103.storage.TaskList;
import cs2103.parameters.ParameterList;
import cs2103.task.FloatingTask;
import cs2103.task.Task;
import cs2103.util.TestUtil;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class DeleteTest {

	@BeforeClass
	public static void initialise() throws HandledException, FatalException{
		TaskList.getInstance(new Option(Option.Value.TEST)).emptyTestList();;
	}
	
	@Test
	public void test1_testPermanentDelete() throws HandledException, FatalException {
		assert(TaskList.getInstance()!=null);
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
	public void test2_testTempDelete() throws HandledException, FatalException{
		assert(TaskList.getInstance()!=null);
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
	public void test3_testInvalidDelete() throws HandledException, FatalException{
		assert(TaskList.getInstance()!=null);
		Delete deleteObj = new Delete("2");
		deleteObj.execute();
	}
	
	@Test
	public void test4_testUndoRedo() throws HandledException, FatalException{
		assert(TaskList.getInstance()!=null);
		Add addObj = new Add("-title testDelete");
		addObj.execute();
		Delete deleteObj = new Delete("2");
		deleteObj.execute();
		deleteObj.undo();
		Search s = new Search("testDelete");
		String result = s.execute().toString();
		assertNotEquals(ansi().bold().fg(RED).a("The task list is empty\n").reset().toString(),result);
		deleteObj.redo();
		result = s.execute().toString();
		assertEquals(ansi().bold().fg(RED).a("The task list is empty\n").reset().toString(),result);
	}

}
