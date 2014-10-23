package cs2103.parameters;

import static org.junit.Assert.*;

import java.util.Queue;

import org.junit.Test;

import cs2103.command.Command;
import cs2103.exception.HandledException;

public class ParameterListTest {

	@Test
	public void testParameter() throws HandledException {
		ParameterList pl = new ParameterList();
		pl.addParameter(Title.parse("title"));
		pl.addParameter(Location.parse("location"));
		pl.addParameter(Description.parse("desc"));
		pl.addParameter(TaskID.parse("1"));
		pl.addParameter(Keyword.parse("nus"));
		pl.addParameter(Complete.parse("false"));
		pl.addParameter(Time.parse("2014/10/29 20:20"));
		pl.addParameter(TaskType.parse("Deadline"));
		pl.addParameter(Recurrence.parse("2d"));
		pl.addParameter(Option.parse("test".split(" ")));
		
		assertEquals(10,pl.getParameterCount());
		assertEquals(Title.parse("title").getValue(),pl.getTitle().getValue());
		assertEquals(Location.parse("location").getValue(),pl.getLocation().getValue());
		assertEquals(TaskID.parse("1").getValue(),pl.getTaskID().getValue());
		assertEquals(Keyword.parse("nus").getValue(),pl.getKeyword().getValue());
		assertEquals(Complete.parse("false").getValue(),pl.getComplete().getValue());
		assertEquals(Time.parse("2014/10/29 20:20").getValue()[0].toString(),pl.getTime().getValue()[0].toString());
		assertEquals(TaskType.parse("Deadline").getValue(),pl.getTaskType().getValue());
		assertEquals(Recurrence.parse("2d").getValue().toString(),pl.getRecurrence().getValue().toString());
		//assertEquals(Option.parse("test".split(" ")),pl.Option);	can't find option in ParameterList
	}

}
