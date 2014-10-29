package cs2103.command;

import static org.junit.Assert.*;

import org.junit.BeforeClass;
import org.junit.Test;

import cs2103.TaskList;
import cs2103.exception.FatalException;
import cs2103.exception.HandledException;
import cs2103.parameters.Option;
import cs2103.parameters.ParameterList;
import cs2103.parameters.TaskType.Value;

public class ListTest {

	@BeforeClass
	public static void initialise() throws HandledException, FatalException{
		TaskList.getInstance(new Option(Option.Value.TEST));
		Add addObj = new Add("add -title floating");
		addObj.execute();
		addObj = new Add("add -title deadline -time 2014/10/23 20:20");
		addObj.execute();
		addObj = new Add("add -title periodic -time 2014/10/23 20:20 to 2014/10/25 20:20");
		addObj.execute();
	}
	
	
	@Test
	public void testDefaultlist() throws FatalException, HandledException{
		
		List listDefault = new List("hello");
		ParameterList pl = listDefault.getParameterList();
		assertEquals(Value.INVALID,pl.getTaskType().getValue());
		String result = listDefault.execute();
		assertEquals("1. floating\n" +
				"Type: Floating	Status: Needs Action\n" +
				"2. deadline\n" +
				"Type: Deadline	Status: Needs Action	Due At: 23-Oct-2014 20:20:00",result);
	}
	
	@Test
	public void testPeriodicList() throws FatalException, HandledException{
		
		List listPeriodic = new List("periodic");
		ParameterList pl = listPeriodic.getParameterList();
		assertEquals(Value.PERIODIC,pl.getTaskType().getValue());
		String result = listPeriodic.execute();
		assertEquals("3. periodic\n" +
				"Type: Periodic	From: 23-Oct-2014 20:20:00 To 25-Oct-2014 20:20:00",result);
	}
	
	@Test
	public void testDeadlineList() throws HandledException, FatalException{
		
		List listDeadline = new List("deadline");
		ParameterList pl = listDeadline.getParameterList();
		assertEquals(Value.DEADLINE,pl.getTaskType().getValue());
		String result = listDeadline.execute();
		assertEquals("2. deadline\n" +
				"Type: Deadline\tStatus: Needs Action\tDue At: 23-Oct-2014 20:20:00",result);
	}
	
	@Test
	public void testFloatingList() throws HandledException, FatalException{
		
		List listFloating = new List("floating");
		ParameterList pl = listFloating.getParameterList();
		assertEquals(Value.FLOATING,pl.getTaskType().getValue());
		String result = listFloating.execute();
		assertEquals("1. floating\n" +
				"Type: Floating	Status: Needs Action",result);
	}

}
