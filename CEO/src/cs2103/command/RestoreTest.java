package cs2103.command;

import static org.fusesource.jansi.Ansi.ansi;
import static org.fusesource.jansi.Ansi.Color.GREEN;
import static org.fusesource.jansi.Ansi.Color.RED;
import static org.junit.Assert.*;


import org.junit.BeforeClass;
import org.junit.Test;

import cs2103.exception.FatalException;
import cs2103.exception.HandledException;
import cs2103.parameters.Option;
import cs2103.parameters.ParameterList;
import cs2103.storage.TaskList;
import cs2103.task.FloatingTask;
import cs2103.task.Task;

public class RestoreTest {
	@BeforeClass
	public static void initialise() throws HandledException, FatalException{
		TaskList.getInstance(new Option(Option.Value.TEST));
		
	}

	@Test
	public void testRestore() throws HandledException, FatalException {
		assert(TaskList.getInstance()!=null);
		Add addObj = new Add("add task to be deleted");
		addObj.execute();
		Delete d = new Delete("1");
		d.execute();
		Restore r = new Restore("1");
		ParameterList pl = r.getParameterList();
		assertEquals(1,pl.getTaskID().getValue());
		String result = r.execute().toString();
		FloatingTask t = TaskList.getInstance().getFloatingList().get(0);
		assertEquals(ansi().fg(GREEN).a("You have successfully restored a task with ID 1\n").reset().a(t.toDetail()).toString(),result);
	}
	
	@Test(expected = HandledException.class)
	public void testTaskNotInTrashRestore() throws HandledException, FatalException{
		assert(TaskList.getInstance()!=null);
		Delete d = new Delete("1 -p");
		d.execute();
		Restore r = new Restore("1");
		r.execute().toString();
	}
	
	@Test(expected=HandledException.class)
	public void testInvalidRestore() throws HandledException, FatalException{
		Restore r = new Restore("2");
		r.execute().toString();
	}
	
	@Test
	public void testUndoRedo() throws HandledException, FatalException{
		Add addobj = new Add("add another test");
		addobj.execute();
		Delete d = new Delete("1");
		d.execute();
		Restore r = new Restore("1");
		r.execute();
		r.undo();
		List l = new List("all");
		String result = l.execute().toString();
		assertEquals(ansi().bold().fg(RED).a("The task list is empty\n").reset().toString(),result);
		
		r.redo();
		Task t = TaskList.getInstance().getFloatingList().get(0);
		
		result = l.execute().toString();
		d = new Delete("1 -p");
		d.execute();
		assertEquals(ansi().a(t.toSummary()).a("\n").toString(),result);
	}

}
