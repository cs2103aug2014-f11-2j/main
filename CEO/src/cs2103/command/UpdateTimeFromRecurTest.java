package cs2103.command;

import static org.junit.Assert.*;

import org.junit.Test;

import cs2103.TaskList;
import cs2103.exception.FatalException;
import cs2103.exception.HandledException;

public class UpdateTimeFromRecurTest {

	private void initialise() throws HandledException, FatalException{
		TaskList.getInstance(null, false);
		Add addObj = new Add("-title updateFromRecur -time 2014/10/18 23:00 to 2014/10/20 23:00 -recurring 2d");
		addObj.execute();
	}
	
	@Test
	public void testUpdateFromRecur() throws HandledException, FatalException {
		try{
			TaskList.getInstance();
		}catch(FatalException e){
			initialise();
		}
		UpdateTimeFromRecur ufr = new UpdateTimeFromRecur();
		String result = ufr.execute();
		assertEquals("Successfully updated 1 recurring tasks",result);
	}

}
