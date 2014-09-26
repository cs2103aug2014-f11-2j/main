package cs2103;

import static org.junit.Assert.*;

import java.text.ParseException;
import java.util.Date;

import org.junit.Test;

public class CommandExecutorTest {

	@Test
	public void testStringToDate() {
		CommandExecutor test=new CommandExecutor("");
		try {
			System.out.println(test.stringToDate("2000/10/01/22:33"));
			//test.updateTask(0, "Shit", "Fuck", "Yes", "COMPLETE", "2015/04/23/12:45", "");
			System.out.println("  hjhj hjhj hjhj   ".trim());
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
