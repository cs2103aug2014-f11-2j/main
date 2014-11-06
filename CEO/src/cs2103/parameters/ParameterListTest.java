package cs2103.parameters;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Test;

import cs2103.exception.HandledException;

public class ParameterListTest {

	/**
	 * Test adding and retrieving of parameters in ParameterList
	 * @throws HandledException
	 */
	@Test
	public void testParameter() throws HandledException {
		ParameterList pl = new ParameterList();
		pl.addParameter(Title.parse("title"));
		pl.addParameter(Location.parse("location"));
		pl.addParameter(Description.parse("desc"));
		pl.addParameter(TaskID.parse("1"));
		pl.addParameter(Keyword.parse("nus"));
		pl.addParameter(Complete.parse("false"));
		
		ArrayList<Parameter> pArrayList = new ArrayList<Parameter>();
		
		pArrayList.add(Time.parse("2014/10/29 20:20"));
		pArrayList.add(TaskType.parse("Deadline"));
		pArrayList.add(Recurrence.parse("2d"));
		
		pl.addAllParameters(pArrayList);
		
		assertEquals(9,pl.getParameterCount());
		assertEquals(Title.parse("title").getValue(),pl.getTitle().getValue());
		assertEquals(Location.parse("location").getValue(),pl.getLocation().getValue());
		assertEquals(TaskID.parse("1").getValue(),pl.getTaskID().getValue());
		assertEquals(Keyword.parse("nus").getValue(),pl.getKeyword().getValue());
		assertEquals(Complete.parse("false").getValue(),pl.getComplete().getValue());
		assertEquals(Time.parse("2014/10/29 20:20").getValue()[0].toString(),pl.getTime().getValue()[0].toString());
		assertEquals(TaskType.parse("Deadline").getValue(),pl.getTaskType().getValue());
		assertEquals(Recurrence.parse("2d").getValue().toString(),pl.getRecurrence().getValue().toString());
	}

}
