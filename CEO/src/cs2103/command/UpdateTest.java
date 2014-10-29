package cs2103.command;

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
import cs2103.task.PeriodicTask;
import cs2103.task.Task;
import cs2103.util.TestUtil;

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
		
		String time = "2014/12/25 20:20 to 2014/12/31 23:59";
		
		ParameterList pl = updateTitle.getParameterList();
		assertEquals("I was wrong",pl.getTitle().getValue());
		assertEquals("can't remember",pl.getDescription().getValue());
		assertEquals("earth",pl.getLocation().getValue());
		assertEquals(Time.parse(time).getValue()[0].toString(),pl.getTime().getValue()[0].toString());
		assertNotNull(pl.getRecurrence());
		updateTitle.execute();
		
		Task task = new PeriodicTask(null, null, null, "I was wrong", "earth",Time.parse(time).getValue()[0], 
				Time.parse(time).getValue()[1], Recurrence.parse("2d").getValue());
		task.updateDescription("can't remember");
		task.updateLastModified(null);
		
		for(Task t : TaskList.getInstance().getPeriodicList()){
			TestUtil.compareTasks(task, t);
		}
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
