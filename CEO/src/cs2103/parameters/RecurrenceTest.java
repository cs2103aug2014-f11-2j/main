package cs2103.parameters;

import static org.junit.Assert.*;

import org.junit.Test;

import cs2103.exception.HandledException;

public class RecurrenceTest {

	@Test
	public void testRecurrence() throws HandledException {
		Recurrence rec = Recurrence.parse("2d");
		assertEquals("RECURRENCE",rec.getType());
	}

}
