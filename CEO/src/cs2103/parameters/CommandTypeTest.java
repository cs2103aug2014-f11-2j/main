package cs2103.parameters;

import static org.junit.Assert.*;

import org.junit.Test;

import cs2103.parameters.CommandType.Value;

public class CommandTypeTest {

	@Test
	public void testCommandType() {
		CommandType ct = new CommandType(Value.ALERT);
		assertEquals(Value.ALERT,ct.getValue());
		
		ct = CommandType.parse("UPDATE");
		assertEquals(Value.UPDATE,ct.getValue());
		
		ct = CommandType.parse("hello");
		assertEquals(Value.INVALID,ct.getValue());
	}

}
