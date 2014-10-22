package cs2103.command;

import static org.junit.Assert.*;

import org.junit.Test;

import cs2103.TaskList;
import cs2103.exception.FatalException;
import cs2103.exception.HandledException;

public class ListTest {

	@Test
	public void testAllList() throws HandledException, FatalException {
		TaskList.getInstance(null, false);
		Add addObj = new Add("add -title floating");
		addObj.execute();
		addObj = new Add("add -title deadline -time 2014/10/23 20:20");
		addObj.execute();
		addObj = new Add("add -title periodic -time 2014/10/23 20:20 to 2014/10/25 20:20");
		addObj.execute();
		
		List listAll = new List("list all");
		assertEquals("1. floating\n" +
				"Type: Floating	Status: Needs Action\n" +
				"2. deadline\n" +
				"Type: Deadline	Status: Needs Action	Due At: 23-Oct-2014 20:20:00\n" +
				"3. periodic\n" +
				"Type: Periodic	From: 23-Oct-2014 20:20:00 To 25-Oct-2014 20:20:00",listAll.execute());
	}
	
	public void testDefaultlist() throws FatalException, HandledException{
		TaskList.getInstance();
		List listDefault = new List("list");
		assertEquals("1. floating\n" +
				"Type: Floating	Status: Needs Action\n" +
				"2. deadline\n" +
				"Type: Deadline	Status: Needs Action	Due At: 23-Oct-2014 20:20:00\n" +
				"3. periodic\n" +
				"Type: Periodic	From: 23-Oct-2014 20:20:00 To 25-Oct-2014 20:20:00",listDefault.execute());
	}

}
