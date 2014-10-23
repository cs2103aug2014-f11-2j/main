package cs2103.parameters;

import static org.junit.Assert.*;

import org.junit.Test;

import cs2103.exception.HandledException;

public class TaskIDTest {

	@Test
	public void test() throws HandledException {
		TaskID tid = TaskID.parse("2");
		assertEquals(2,tid.getValue());
	}

}
