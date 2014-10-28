package cs2103.util;

public class Console {
	static {
		System.loadLibrary("ConsoleClear");
	}
	public native void clr();
}
