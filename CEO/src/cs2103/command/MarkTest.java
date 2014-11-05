package cs2103.command;

import static org.fusesource.jansi.Ansi.ansi;
import static org.fusesource.jansi.Ansi.Color.RED;
import static org.junit.Assert.*;

import java.util.Date;

import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import cs2103.exception.FatalException;
import cs2103.exception.HandledException;
import cs2103.parameters.Option;
import cs2103.storage.TaskList;

import cs2103.parameters.ParameterList;
import cs2103.parameters.Time;
import cs2103.task.DeadlineTask;
import cs2103.task.FloatingTask;
import cs2103.task.Task;
import cs2103.util.TestUtil;


@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class MarkTest {

	static Date d = null;
	@BeforeClass
	public static void initialise() throws HandledException, FatalException{
		TaskList.getInstance(new Option(Option.Value.TEST));
		d = new Date();
	}
	
	@Test
	public void floatingTask_MarkTest() throws HandledException, FatalException {
		Add addObj = new Add("-title floating");
		addObj.execute();
		Mark markFloating = new Mark("1");
		ParameterList pl = markFloating.getParameterList();
		assertEquals(1,pl.getTaskID().getValue());
		Task ft = new FloatingTask(null, null);
		ft.updateTitle("floating");
		ft.updateDescription(null);
		ft.updateLastModified(null);
		ft.updateCompleted(d);
		markFloating.execute();
		for(Task t : TaskList.getInstance().getFloatingList()){
			assertTrue(TestUtil.compareTasks(t, ft));
		}
	
	}
	
	@Test
	public void periodicTask_MarkTest() throws HandledException, FatalException{
		Add addObj = new Add("-title periodic -time 2014/10/23 20:20 to 2014/10/25 20:20");
		addObj.execute();
		Mark markPeriodic = new Mark("3");
		ParameterList pl = markPeriodic.getParameterList();
		assertEquals(3,pl.getTaskID().getValue());
		String result = markPeriodic.execute().toString();
		//unable to replace with compareTask as periodic has no complete flag
		assertEquals(ansi().fg(RED).a("Task 3 does not support mark operation\n").reset().toString(),result);
	}
	
	@Test
	public void deadlineTask_MarkTest() throws HandledException, FatalException{
		Add addObj = new Add("-title deadline -time 2014/10/23 20:20");
		addObj.execute();
		Mark markDeadline = new Mark("2");
		ParameterList pl = markDeadline.getParameterList();
		assertEquals(2,pl.getTaskID().getValue());
		markDeadline.execute();
		String time ="2014/10/23 20:20";
		Task pt = new DeadlineTask(null, null, Time.parse(time).getValue()[0]);
		pt.updateTitle("deadline");
		pt.updateDescription(null);
		pt.updateLastModified(null);
		pt.updateCompleted(d);
		markDeadline.execute();
		for(Task t: TaskList.getInstance().getDeadlineList()){
			TestUtil.compareTasks(t, pt);
		}
	
	}
}
