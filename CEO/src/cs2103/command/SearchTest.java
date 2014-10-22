package cs2103.command;

import static org.junit.Assert.*;

import org.junit.Test;

import cs2103.TaskList;
import cs2103.exception.FatalException;
import cs2103.exception.HandledException;

public class SearchTest {
	
	private void initialise() throws HandledException, FatalException{
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
	}
		
	@Test
	public void testSearchKeyword() throws HandledException, FatalException {	
		try{
			TaskList.getInstance();
		}catch(FatalException e){
			initialise();
		}
		Search searchTitle = new Search("all -keyword homework");
		String result = searchTitle.execute();
		assertEquals("1. Jap homework\n" +
				"Type: Deadline	Status: Needs Action	Due At: 23-Oct-2014 21:00:00\n" +
				"2. revise stats homework\n" +
				"Type: Periodic	From: 24-Oct-2014 20:00:00 To 25-Oct-2014 20:00:00",result);
	}
	
	
	@Test
	public void testSearchComplete() throws FatalException, HandledException{
		try{
			TaskList.getInstance();
		}catch(FatalException e){
			initialise();
		}
		Mark markComplete = new Mark("1");
		markComplete.execute();
		Search searchComplete = new Search("all -complete");
		String result = searchComplete.execute();
		markComplete.undo();
		assertEquals("1. Jap homework\n" +
				"Type: Deadline	Status: Completed	Due At: 23-Oct-2014 21:00:00",result);
	}
	
	@Test
	public void testSearchPeriodic() throws HandledException, FatalException{
		try{
			TaskList.getInstance();
		}catch(FatalException e){
			initialise();
		}
		Search searchPeriodic = new Search("periodic");
		String result = searchPeriodic.execute();
		assertEquals("2. revise stats homework\n" +
				"Type: Periodic	From: 24-Oct-2014 20:00:00 To 25-Oct-2014 20:00:00",result);
	}
	
	@Test
	public void testSearchDeadline() throws HandledException, FatalException{
		try{
			TaskList.getInstance();
		}catch(FatalException e){
			initialise();
		}
		Search searchDeadline = new Search("deadline");
		String result = searchDeadline.execute();
		assertEquals("1. Jap homework\n" +
				"Type: Deadline	Status: Needs Action	Due At: 23-Oct-2014 21:00:00\n" +
				"3. celebrate 21st :D\n" +
				"Type: Deadline	Status: Needs Action	Due At: 01-Nov-2014 18:00:00",result);
	}
	
	@Test
	public void testSearchFloating() throws HandledException, FatalException{
		try{
			TaskList.getInstance();
		}catch(FatalException e){
			initialise();
		}
		Search searchFloating = new Search("floating");
		String result = searchFloating.execute();
		assertEquals("4. do cs2105 networking assignment\n" +
				"Type: Floating	Status: Needs Action\n" +
				"5. finish essay\n" +
				"Type: Floating	Status: Needs Action",result);
	}
	
	@Test
	public void testSearchTime() throws HandledException, FatalException{
		try{
			TaskList.getInstance();
		}catch(FatalException e){
			initialise();
		}
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
