package cs2103.command;

import static org.junit.Assert.*;

import org.junit.Test;

import cs2103.TaskList;
import cs2103.exception.FatalException;
import cs2103.exception.HandledException;

public class SearchTest {
	
	@Test
	public void testSearchKeyword() throws HandledException, FatalException {
		TaskList.getInstance(null, false);
		Add addObj = new Add("-title Jap homework -time 2014/10/23 21:00");
		addObj.execute();
		addObj = new Add("-title revise stats homework -description revise chp 5 to 7 -location NUS UTOWN -time 2014/10/24 20:00 to 2014/10/25 20:00");
		addObj.execute();
		addObj = new Add("-title celebrate 21st :D -time 2014/11/1 18:00");
		addObj.execute();
		addObj = new Add("-title do cs2105 networking assignment");
		addObj.execute();
		addObj = new Add("-title finish essay");
		addObj.execute();
		
		Search searchTitle = new Search("all -keyword homework");
		assertEquals("1. Jap homework\n" +
				"Type: Deadline	Status: Needs Action	Due At: 23-Oct-2014 21:00:00\n" +
				"2. revise stats homework\n" +
				"Type: Periodic	From: 24-Oct-2014 20:00:00 To 25-Oct-2014 20:00:00",searchTitle.execute());
	}
	
	public void testSearchIgnoreCase() throws FatalException, HandledException{
		TaskList.getInstance();
		Search searchIgnoreCase = new Search("all -keyword nus");
		assertEquals("2. revise stats homework\n" +
				"Type: Periodic	From: 24-Oct-2014 20:00:00 To 25-Oct-2014 20:00:00",searchIgnoreCase.execute());
	}
	
	public void testSearchComplete() throws FatalException, HandledException{
		TaskList.getInstance();
		Mark markComplete = new Mark("1");
		markComplete.execute();
		Search searchComplete = new Search("all -complete");
		assertEquals("1. Jap homework\n" +
				"Type: Deadline	Status: Completed	Due At: 23-Oct-2014 21:00:00",searchComplete.execute());
	}
}
