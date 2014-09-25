package cs2103;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Test;

public class StorageEngineTest {

	@Test
	public void test() {
		StorageEngine test=new StorageEngine("test");
		try {
			test.read();
			ArrayList<Task> taskList=test.getTaskList();
			System.out.println(taskList.get(0).getStartTime().getTime());
		} catch (CEOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
