package cs2103.parameters;

import static org.junit.Assert.*;

import java.util.Calendar;
import java.util.Date;

import org.junit.Test;

public class TimeTest {

	@Test
	public void test() {
		Time t = Time.parse("2014/10/29 20:20");
		Date[] date = t.getValue();
		Calendar c = Calendar.getInstance();
		c.set(2014, 9, 29, 20,20,00);
		Date d = c.getTime();
		assertEquals(d.toString(),date[0].toString());
		
	}

}
