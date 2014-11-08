package cs2103.command;

import static org.fusesource.jansi.Ansi.ansi;
import static org.fusesource.jansi.Ansi.Color.RED;
import static org.junit.Assert.*;

import org.junit.BeforeClass;
import org.junit.Test;

import cs2103.exception.FatalException;
import cs2103.exception.HandledException;
import cs2103.parameters.Option;
import cs2103.storage.TaskList;

import cs2103.parameters.ParameterList;
import cs2103.parameters.Recurrence;
import cs2103.parameters.Time;
import cs2103.task.DeadlineTask;
import cs2103.task.PeriodicTask;
import cs2103.task.Task;
import cs2103.util.TestUtil;

public class AddTest {
	
	@BeforeClass 
	public static void initialise() throws HandledException, FatalException{
		TaskList.getInstance(new Option(Option.Value.TEST));
	}
	
	@Test
	public void testAddCorrectCommand() throws HandledException, FatalException {
		assert(TaskList.getInstance()!=null);
		Add addObj;
		addObj = new Add("-title hello -description some description -location earth -time 2014/12/12 20:20 to 2014/12/15 20:20 -recur 2d");
		ParameterList pl = addObj.getParameterList();
		assertEquals("hello",pl.getTitle().getValue());
		assertEquals("some description",pl.getDescription().getValue());
		assertEquals(Time.parse("2014/12/12 20:20 to 2014/12/15 20:20").getValue()[0].toString(), pl.getTime().getValue()[0].toString());
		assertEquals(Recurrence.parse("2d").getValue().toString(), pl.getRecurrence().getValue().toString());
		addObj.execute();
		String time = "2014/12/12 20:20  to 2014/12/15 20:20";
		Task dt = new PeriodicTask(null, null, Time.parse(time).getValue()[0], Time.parse(time).getValue()[1]);
		dt.updateTitle("hello");
		dt.updateDescription("some description");
		dt.updateRecurrence(Recurrence.parse("2d").getValue());
		dt.updateLastModified(null);
		for(Task t :TaskList.getInstance().getDeadlineList()){
			if(t.getTitle().equals("hello")){
				assertTrue(TestUtil.compareTasks(t, dt));
			}
		}
	}
	
	@Test
	public void testQuickAddCommand() throws HandledException, FatalException{
		assert(TaskList.getInstance()!=null);
		Add addObj;
		addObj = new Add("finish cs2105 assignment by next monday");
		addObj.execute();
		Task deadlineTask = new DeadlineTask(null,null,Time.parse("next monday").getValue()[0]);
		deadlineTask.updateTitle("finish cs2105 assignment ");
		deadlineTask.updateLastModified(null);
		for(Task t :TaskList.getInstance().getDeadlineList()){
			if(t.getTitle().contains("cs2105")){
				assertTrue(TestUtil.compareTasks(t, deadlineTask));
			}
		}
	}
	
	//This is a test case that combines multiple valid and invalid inputs
	@Test(expected=HandledException.class)
	public void testAddCommandInvalidParam() throws HandledException, FatalException{
		Add addObj;
		addObj = new Add("-tile hello -description was described -plave earth -time 2014/11/12");
		addObj.execute();
		Task deadlineTask = new DeadlineTask(null,null,Time.parse("2014/11/12").getValue()[0]);
		deadlineTask.updateLastModified(null);
		deadlineTask.updateDescription("was described");
		deadlineTask.updateTitle("");
		assertNull(addObj.getParameterList().getLocation());
		for(Task t :TaskList.getInstance().getDeadlineList()){
			if(t.getDescription().contains("was")){
				assertTrue(TestUtil.compareTasks(t, deadlineTask));
			}
		}
	}
	
	@SuppressWarnings("unused")
	@Test(expected = HandledException.class)
	public void testAddNullCommand() throws HandledException{
		Add addObj = new Add(null);
	}

	//tests undo redo function for add command
	@Test
	public void testAddUndoRedo() throws HandledException, FatalException{
		Add addObj = new Add("-title undo");
		addObj.execute();
		addObj.undo();
		Search s = new Search("undo");
		String result = s.execute().toString();
		assertEquals(ansi().bold().fg(RED).a("The task list is empty\n").reset().toString(),result);
		addObj.redo();
		result = s.execute().toString();
		Task t = TaskList.getInstance().getFloatingList().get(0);
		addObj.undo();
		assertEquals(ansi().a(t.toSummary()).a('\n').toString(),result);
	}
}
