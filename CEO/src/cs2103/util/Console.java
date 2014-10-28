package cs2103.util;

public class Console {
	static {
		if (System.getProperty("os.arch").contains("64")){
			System.loadLibrary("ConsoleClear_x64");
		} else {
			System.loadLibrary("ConsoleClear_x86");
		}
	}
	public native void clr();
}
