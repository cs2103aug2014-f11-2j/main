package cs2103;
import java.util.Date;
import static org.junit.Assert.*;

import org.junit.Test;

public class TaskTest {

	@Test
	public void test() {
		try {
			Task testTask=new Task(null,"TestTitle");
			
		} catch (CEOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
