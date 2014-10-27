package cs2103.util;

public class ConsoleClear {
	static {
		System.loadLibrary("ConsoleClear");
	}
	public static native void clr();
}
