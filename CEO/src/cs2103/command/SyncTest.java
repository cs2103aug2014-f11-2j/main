package cs2103.command;

import static org.fusesource.jansi.Ansi.ansi;
import static org.fusesource.jansi.Ansi.Color.GREEN;
import static org.fusesource.jansi.Ansi.Color.MAGENTA;
import static org.junit.Assert.*;

import org.junit.BeforeClass;
import org.junit.Test;

import cs2103.exception.FatalException;
import cs2103.exception.HandledException;
import cs2103.parameters.Option;
import cs2103.storage.TaskList;
import cs2103.util.CommonUtil;

public class SyncTest {

	@BeforeClass
	public static void initialise() throws HandledException, FatalException{
		TaskList.getInstance(new Option(Option.Value.TEST));
	}
	
	@Test
	public void testEnableSync() throws HandledException, FatalException {
		assert(CommonUtil.checkSyncSupport() == true);
		Sync s = new Sync(null);
		String result = s.execute().toString();
		assertEquals(ansi().fg(GREEN).a("Successfully sync your data with Google\n").reset().toString(),result);
	}
	
	@Test
	public void testDiableSync() throws HandledException, FatalException{
		Sync s = new Sync("-disable");
		String result = s.execute().toString();
		assertEquals(ansi().fg(MAGENTA).a("You have disabled sync with Google\n").reset().toString(),result);
	}

}
