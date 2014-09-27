package cs2103;

import static org.junit.Assert.*;

import java.util.LinkedList;
import java.util.Queue;

import org.junit.Test;



public class CommandParserTest {

	@Test
	public void testSeperateCommandNoParameters() {
		Queue<String> expected = new LinkedList<String>();
		Queue<String> actual = new LinkedList<String>();
		expected.add("");
		actual = CommandParser.seperateCommand("");
		assertEquals(expected, actual);
	}
	
	@Test
	public void testSeperateCommandOneParameter() {
		Queue<String> expected = new LinkedList<String>();
		Queue<String> actual = new LinkedList<String>();
		expected.add("add");
		actual = CommandParser.seperateCommand("add");
		assertEquals(expected, actual);
	}
	
	@Test
	public void testSeperateCommandTwoParameters() {
		Queue<String> expected = new LinkedList<String>();
		Queue<String> actual = new LinkedList<String>();
		expected.add("add");
		expected.add("two");
		actual = CommandParser.seperateCommand("add two");
		assertEquals(expected, actual);
	}
	
	@Test
	public void testSeperateCommandTwoParametersMultipleSpaces() {
		Queue<String> expected = new LinkedList<String>();
		Queue<String> actual = new LinkedList<String>();
		expected.add("add");
		expected.add("two");
		actual = CommandParser.seperateCommand("add      two  ");
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
	public void testParseIntegerParameter() {
		assertEquals(-1, CommandParser.parseIntegerParameter(""));
		assertEquals(-1, CommandParser.parseIntegerParameter(null));
		assertEquals(9, CommandParser.parseIntegerParameter("9"));
		assertEquals(0, CommandParser.parseIntegerParameter("0"));
		assertEquals(5, CommandParser.parseIntegerParameter("5"));
		assertEquals(-1, CommandParser.parseIntegerParameter("-1"));
		assertEquals(-1, CommandParser.parseIntegerParameter("15"));
	}
	
}
