package cs2103;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

import org.junit.Test;



public class CommandParserTest {

	@Test
	public void testSeperateCommandNoParameters() {
		Queue<String> expected = new LinkedList<String>();
		Queue<String> actual = new LinkedList<String>();
		expected.add("");
		actual = CommandParser.separateCommand("");
		assertEquals(expected, actual);
	}
	
	@Test
	public void testSeperateCommandOneParameter() {
		Queue<String> expected = new LinkedList<String>();
		Queue<String> actual = new LinkedList<String>();
		expected.add("add");
		actual = CommandParser.separateCommand("add");
		assertEquals(expected, actual);
	}
	
	@Test
	public void testSeperateCommandTwoParameters() {
		Queue<String> expected = new LinkedList<String>();
		Queue<String> actual = new LinkedList<String>();
		expected.add("add");
		expected.add("two");
		actual = CommandParser.separateCommand("add two");
		assertEquals(expected, actual);
	}
	
	@Test
	public void testSeperateCommandTwoParametersMultipleSpaces() {
		Queue<String> expected = new LinkedList<String>();
		Queue<String> actual = new LinkedList<String>();
		expected.add("add");
		expected.add("two");
		actual = CommandParser.separateCommand("add      two  ");
		assertEquals(expected, actual);
	}

	@Test
	public void testDetermineCommandType() {
		assertEquals(CommandLineUI.CommandType.INVALID, CommandParser.determineCommandType(null));
		assertEquals(CommandLineUI.CommandType.LIST, CommandParser.determineCommandType("list"));
		assertEquals(CommandLineUI.CommandType.UPDATE, CommandParser.determineCommandType("update"));
		assertEquals(CommandLineUI.CommandType.EXIT, CommandParser.determineCommandType("exit"));
		assertEquals(CommandLineUI.CommandType.ADD, CommandParser.determineCommandType("add"));
		assertEquals(CommandLineUI.CommandType.DELETE, CommandParser.determineCommandType("delete"));
		assertEquals(CommandLineUI.CommandType.SHOWDETAIL, CommandParser.determineCommandType("show"));
		assertEquals(CommandLineUI.CommandType.INVALID, CommandParser.determineCommandType(""));
		assertEquals(CommandLineUI.CommandType.INVALID, CommandParser.determineCommandType("hello"));
	}
	
	@Test 
	public void testDetermineTaskType() {
		assertEquals(CommandLineUI.TaskType.INVALID, CommandParser.determineTaskType(null));
		assertEquals(CommandLineUI.TaskType.ALL, CommandParser.determineTaskType("all"));
		assertEquals(CommandLineUI.TaskType.FLOATING, CommandParser.determineTaskType("floating"));
		assertEquals(CommandLineUI.TaskType.PERIODIC, CommandParser.determineTaskType("periodic"));
		assertEquals(CommandLineUI.TaskType.DEADLINE, CommandParser.determineTaskType("deadline"));
		assertEquals(CommandLineUI.TaskType.INVALID, CommandParser.determineTaskType("hello"));
	
	}
	@Test
	public void testParseIntegerParameter() {
		assertEquals(-1, CommandParser.parseIntegerParameter(""));
		assertEquals(-1, CommandParser.parseIntegerParameter(null));
		assertEquals(9, CommandParser.parseIntegerParameter("9"));
		assertEquals(0, CommandParser.parseIntegerParameter("0"));
		assertEquals(5, CommandParser.parseIntegerParameter("5"));
		assertEquals(-1, CommandParser.parseIntegerParameter("-1"));
		assertEquals(15, CommandParser.parseIntegerParameter("15"));
		assertEquals(999, CommandParser.parseIntegerParameter("999")); // should there be a limit?
	}
	
	@Test 
	public void testSeparateParameters() {
		Map<String,String> expected = new HashMap<String, String>();
		Map<String,String> actual = new HashMap<String, String>();
		
	}
	
}
