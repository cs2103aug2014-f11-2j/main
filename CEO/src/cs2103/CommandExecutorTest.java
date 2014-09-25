package cs2103;

import static org.junit.Assert.*;

import java.text.ParseException;

import org.junit.Test;

public class CommandExecutorTest {

	@Test
	public void testStringToDate() {
		CommandExecutor test=new CommandExecutor("");
		try {
			System.out.println(test.stringToDate("2000/10/01/22:33"));
			test.stringToRecur("1d");
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CEOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
