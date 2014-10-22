package cs2103.command;

import static org.junit.Assert.*;

import org.junit.Test;

import cs2103.TaskList;
import cs2103.exception.FatalException;
import cs2103.exception.HandledException;

public class ListTest {

	private void initialise() throws HandledException, FatalException{
		TaskList.getInstance(null, false);
		Add addObj = new Add("add -title floating");
		addObj.execute();
		addObj = new Add("add -title deadline -time 2014/10/23 20:20");
		addObj.execute();
		addObj = new Add("add -title periodic -time 2014/10/23 20:20 to 2014/10/25 20:20");
		addObj.execute();
	}
	
	@Test
	public void testAllList() throws HandledException, FatalException {
		try{
			TaskList.getInstance();
		}catch(FatalException e){
			initialise();
		}
		List listAll = new List("all");
		String result = listAll.execute();
		assertEquals("1. floating\n" +
				"Type: Floating	Status: Needs Action\n" +
				"2. deadline\n" +
				"Type: Deadline	Status: Needs Action	Due At: 23-Oct-2014 20:20:00\n" +
				"3. periodic\n" +
				"Type: Periodic	From: 23-Oct-2014 20:20:00 To 25-Oct-2014 20:20:00",result);
	}
	
	@Test
	public void testDefaultlist() throws FatalException, HandledException{
		try{
			TaskList.getInstance();
		}catch(FatalException e){
			initialise();
		}
		List listDefault = new List("list");
		String result = listDefault.execute();
		assertEquals("1. floating\n" +
				"Type: Floating	Status: Needs Action\n" +
				"2. deadline\n" +
				"Type: Deadline	Status: Needs Action	Due At: 23-Oct-2014 20:20:00\n" +
				"3. periodic\n" +
				"Type: Periodic	From: 23-Oct-2014 20:20:00 To 25-Oct-2014 20:20:00",result);
	}
	
	@Test
	public void testPeriodicList() throws FatalException, HandledException{
		try{
			TaskList.getInstance();
		}catch(FatalException e){
			initialise();
		}
		List listPeriodic = new List("periodic");
		String result = listPeriodic.execute();
		assertEquals("3. periodic\n" +
				"Type: Periodic	From: 23-Oct-2014 20:20:00 To 25-Oct-2014 20:20:00",result);
	}
	
	@Test
	public void testDeadlineList() throws HandledException, FatalException{
		try{
			TaskList.getInstance();
		}catch(FatalException e){
			initialise();
		}
		List listDeadline = new List("deadline");
		String result = listDeadline.execute();
		assertEquals("2. deadline\n" +
				"Type: Deadline\tStatus: Needs Action\tDue At: 23-Oct-2014 20:20:00",result);
	}
	
	@Test
	public void testFloatingList() throws HandledException, FatalException{
		try{
			TaskList.getInstance();
		}catch(FatalException e){
			initialise();
		}
		List listFloating = new List("floating");
		String result = listFloating.execute();
		assertEquals("1. floating\n" +
				"Type: Floating	Status: Needs Action",result);
	}

}
