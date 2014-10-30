package cs2103.util;

public class Console {
	static {
		if (System.getProperty("os.arch").contains("64")){
			System.loadLibrary("lib/ConsoleClear_x64");
		} else {
			System.loadLibrary("lib/ConsoleClear_x86");
		}
	}
	public static native void clr();
}
