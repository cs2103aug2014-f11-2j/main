//@author A0112673L
package cs2103.parameters;

import static org.junit.Assert.*;

import org.junit.Test;

import cs2103.parameters.TaskType.Value;

public class TaskTypeTest {

	@Test
	public void testTaskType() {
		TaskType taskType = TaskType.parse("deadline");
		assertEquals(Value.DEADLINE,taskType.getValue());
		
		taskType = TaskType.parse(null);
		assertEquals(Value.DEFAULT,taskType.getValue());
	}

}
