package cs2103.command;

import static org.junit.Assert.*;

import org.junit.BeforeClass;
import org.junit.Test;

import cs2103.TaskList;
import cs2103.exception.FatalException;
import cs2103.exception.HandledException;

public class SearchTest {
	
	@BeforeClass
	public static void initialise() throws HandledException, FatalException{
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
		addObj = new Add("-title complete");
		addObj.execute();
		Mark m = new Mark("6");
		m.execute();
	}
		
	@Test
	public void testSearchKeyword() throws HandledException, FatalException {	
		
		Search searchTitle = new Search("all -keyword homework");
		String result = searchTitle.execute();
		assertEquals("1. Jap homework\n" +
				"Type: Deadline	Status: Needs Action	Due At: 23-Oct-2014 21:00:00\n" +
				"2. revise stats homework\n" +
				"Type: Periodic	From: 24-Oct-2014 20:00:00 To 25-Oct-2014 20:00:00",result);
	}
	
	
	@Test
	public void testSearchComplete() throws FatalException, HandledException{
		
		Search searchComplete = new Search("all -complete");
		String result = searchComplete.execute();
		assertEquals("6. complete\n" +
				"Type: Floating	Status: Completed",result);
	}
	
	@Test
	public void testSearchPeriodic() throws HandledException, FatalException{
		
		Search searchPeriodic = new Search("periodic");
		String result = searchPeriodic.execute();
		assertEquals("2. revise stats homework\n" +
				"Type: Periodic	From: 24-Oct-2014 20:00:00 To 25-Oct-2014 20:00:00",result);
	}
	
	@Test
	public void testSearchDeadline() throws HandledException, FatalException{
		
		Search searchDeadline = new Search("deadline");
		String result = searchDeadline.execute();
		assertEquals("1. Jap homework\n" +
				"Type: Deadline	Status: Needs Action	Due At: 23-Oct-2014 21:00:00\n" +
				"3. celebrate 21st :D\n" +
				"Type: Deadline	Status: Needs Action	Due At: 01-Nov-2014 18:00:00",result);
	}
	
	@Test
	public void testSearchFloating() throws HandledException, FatalException{
		
		Search searchFloating = new Search("floating");
		String result = searchFloating.execute();
		assertEquals("4. do cs2105 networking assignment\n" +
				"Type: Floating	Status: Needs Action\n" +
				"5. finish essay\n" +
				"Type: Floating	Status: Needs Action\n" +
				"6. complete\n" +
				"Type: Floating	Status: Completed",result);
	}
	
	@Test
	public void testSearchTime() throws HandledException, FatalException{
		
		Search searchTime = new Search("all -time");
		String result = searchTime.execute();
		assertEquals("1. Jap homework\n" +
				"Type: Deadline	Status: Needs Action	Due At: 23-Oct-2014 21:00:00\n" +
				"2. revise stats homework\n" +
				"Type: Periodic	From: 24-Oct-2014 20:00:00 To 25-Oct-2014 20:00:00\n" +
				"3. celebrate 21st :D\n" +
				"Type: Deadline	Status: Needs Action	Due At: 01-Nov-2014 18:00:00",result);
	}
	
}