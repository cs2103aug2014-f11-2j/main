package cs2103.parameters;

import static org.junit.Assert.*;

import java.text.ParseException;

import org.junit.Test;

import cs2103.exception.HandledException;

public class RecurrenceTest {

	@Test
	public void testRecurrence() throws HandledException, ParseException {
		Recurrence rec = Recurrence.parse("2d");
		assertEquals("RECURRENCE",rec.getType());
		assertEquals("FREQ=DAILY;INTERVAL=2",rec.getValue().toString());
		
		rec = Recurrence.parse("abc");
		assertNull(rec.getValue());
		
		rec = Recurrence.parse(null);
		assertNull(rec);
	}

}
