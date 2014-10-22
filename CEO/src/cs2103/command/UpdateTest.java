package cs2103.command;

import static org.junit.Assert.*;

import org.junit.Test;

import cs2103.TaskList;
import cs2103.exception.FatalException;
import cs2103.exception.HandledException;

public class UpdateTest {
	private void initialise() throws HandledException, FatalException{
		TaskList.getInstance(null, false);
		Add addObj = new Add("-title updateTest");
		addObj.execute();
	}

	@Test
	public void testUpdate() throws HandledException, FatalException {
		try{
			TaskList.getInstance();
		}catch(FatalException e){
			initialise();
		}
		Update updateTitle = new Update("1 -title I was wrong");
		String result = updateTitle.execute();
		assertEquals("You have updated task with ID 1",result);
		List listAll = new List("all");
		assertEquals("1. I was wrong\n" +
				"Type: Floating	Status: Needs Action",listAll.execute());
	}

}
