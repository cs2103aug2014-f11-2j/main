package cs2103;

import static org.junit.Assert.*;

import org.junit.Test;

public class StorageEngineTest {

	@Test
	public void test() {
		StorageEngine test=new StorageEngine("test");
		test.read();
	}

}
