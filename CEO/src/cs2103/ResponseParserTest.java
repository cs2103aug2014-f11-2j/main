package cs2103;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Test;

public class ResponseParserTest {

	@Test
	public void test() {
		StorageEngine test=new StorageEngine("test");
		ArrayList<Task> taskList=test.getTaskList();
		System.out.println(ResponseParser.parseShowDetailResponse(taskList.get(0),1));
	}

}
