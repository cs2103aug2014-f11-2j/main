//@author A0112673L
package cs2103.command;

import static org.fusesource.jansi.Ansi.ansi;
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
import cs2103.task.Task;
import cs2103.parameters.ParameterList;
import cs2103.parameters.TaskType.Value;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ListTest {

	@BeforeClass
	public static void initialise() throws HandledException, FatalException{
		TaskList.getInstance(new Option(Option.Value.TEST)).emptyTestList();;		
	}
	
	@Test
	public void test1_testEmptyList() throws FatalException, HandledException{
		assert(TaskList.getInstance()!=null);
		List listEmpty = new List("all");
		String result = listEmpty.execute().toString();
		assertEquals(ansi().bold().fg(RED).a("The task list is empty\n").reset().toString(),result);
	}
	
	//This test checks output for invalid input
	@Test
	public void test2_testDefaultlist() throws FatalException, HandledException{
		assert(TaskList.getInstance()!=null);
		Add addObj = new Add("add -title floating");
		addObj.execute();
		addObj = new Add("add -title deadline -time 2014/11/23 20:20");
		addObj.execute();
		addObj = new Add("add -title periodic -time 2014/11/23 20:20 to 2014/11/25 20:20");
		addObj.execute();
		List listDefault = new List("hello");
		ParameterList pl = listDefault.getParameterList();
		assertEquals(Value.INVALID,pl.getTaskType().getValue());
		String result = listDefault.execute().toString();
		Task t = TaskList.getInstance().getFloatingList().get(0);
		Task t2 = TaskList.getInstance().getDeadlineList().get(0);
		Task t3 = TaskList.getInstance().getPeriodicList().get(0);
		assertEquals(ansi().a(t.toSummary()).a("\n").a(t2.toSummary())
				.a("\n").a(t3.toSummary()).a("\n").toString(),result);
	}
	
	@Test
	public void test3_testAllist() throws FatalException, HandledException{
		assert(TaskList.getInstance()!=null);
		List listDefault = new List("all");
		ParameterList pl = listDefault.getParameterList();
		assertEquals(Value.ALL,pl.getTaskType().getValue());
		String result = listDefault.execute().toString();
		Task t = TaskList.getInstance().getFloatingList().get(0);
		Task t2 = TaskList.getInstance().getDeadlineList().get(0);
		Task t3 = TaskList.getInstance().getPeriodicList().get(0);
		assertEquals(ansi().a(t.toSummary()).a("\n").a(t2.toSummary())
				.a("\n").a(t3.toSummary()).a("\n").toString(),result);
	}
	
	@Test
	public void test4_testPeriodicList() throws FatalException, HandledException{
		assert(TaskList.getInstance()!=null);
		List listPeriodic = new List("periodic");
		ParameterList pl = listPeriodic.getParameterList();
		assertEquals(Value.PERIODIC,pl.getTaskType().getValue());
		String result = listPeriodic.execute().toString();
		Task t3 = TaskList.getInstance().getPeriodicList().get(0);
		assertEquals(ansi().a(t3.toSummary()).a("\n").toString(),result);
	}
	
	@Test
	public void test5_testDeadlineList() throws HandledException, FatalException{
		assert(TaskList.getInstance()!=null);
		List listDeadline = new List("deadline");
		ParameterList pl = listDeadline.getParameterList();
		assertEquals(Value.DEADLINE,pl.getTaskType().getValue());
		String result = listDeadline.execute().toString();
		Task t2 = TaskList.getInstance().getDeadlineList().get(0);
		assertEquals(ansi().a(t2.toSummary()).a("\n").toString(),result);
	}
	
	@Test
	public void test6_testFloatingList() throws HandledException, FatalException{
		assert(TaskList.getInstance()!=null);
		List listFloating = new List("floating");
		ParameterList pl = listFloating.getParameterList();
		assertEquals(Value.FLOATING,pl.getTaskType().getValue());
		String result = listFloating.execute().toString();
		Task t = TaskList.getInstance().getFloatingList().get(0);
		assertEquals(ansi().a(t.toSummary()).a("\n").toString(),result);
	}

}
