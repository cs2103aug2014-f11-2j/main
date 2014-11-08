//@author A0112673L
package cs2103.parameters;

import static org.junit.Assert.*;

import org.junit.Test;

import cs2103.exception.HandledException;
import cs2103.parameters.Option.Value;

public class OptionTest {

	@Test
	public void testOption() throws HandledException {
		String param = "-test";
		Option opt = Option.parse(param.split(" "));
		assertEquals(Value.TEST,opt.getValue());
	}
}
