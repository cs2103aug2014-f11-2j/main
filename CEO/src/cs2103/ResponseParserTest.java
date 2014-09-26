package cs2103;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Test;

public class ResponseParserTest {

	@Test
	public void test() {
		StorageEngine test=new StorageEngine("test");
		ArrayList<Task> taskList=test.getTaskList();
		System.out.println(ResponseParser.parseShowDetailResponse(taskList.get(0),1));
		String testString = "-L -W dsfsd --help fsdfsd fsdfsd --fuck fsdfsd --ashdjkashj -T";
		
		
	}

}
