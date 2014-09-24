package cs2103;
import java.util.ArrayList;

import net.fortuna.ical4j.*;
class StorageEngine {
	private ArrayList<Task> taskList;
	public StorageEngine(String configFile){
		
	}
	public ArrayList<Task> getTaskList(){
		return taskList;
	}
}
