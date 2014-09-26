package cs2103;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Date;

import net.fortuna.ical4j.model.component.VToDo;

import org.junit.Test;

public class StorageEngineTest {

	@Test
	public void test() {
		StorageEngine test=new StorageEngine("test");
		ArrayList<Task> taskList=test.getTaskList();
		Task task = taskList.get(1);
		System.out.println(task.getTaskID());
		//task.updateDescription("Fuck");
		//task.updateLocation("Fuck");
		//test.updateTask(task);
		//test.deleteTask(taskList.get(0));
		
	}

}
