//@author A0116713M
package cs2103.util;

/**
 * @author Yuri
 * Clear console on Windows
 */
public class Console {
	/* cs2103_util_Console.h
	 #include <jni.h>
	 #ifndef _Included_cs2103_util_Console
	 #define _Included_cs2103_util_Console
	 #ifdef __cplusplus
	 extern "C" {
	 #endif
	 JNIEXPORT void JNICALL Java_cs2103_util_Console_clr(JNIEnv *, jclass);
	 #ifdef __cplusplus
	 }
	 #endif
	 #endif
	 */
	static {
		if (System.getProperty("os.arch").contains("64")) {
			System.loadLibrary("lib/ConsoleClear_x64");
		} else {
			System.loadLibrary("lib/ConsoleClear_x86");
		}
	}
	/* cs2103_util_Console.c
	 #include <windows.h>
	 #include <jni.h>
	 #include "cs2103_util_Console.h"

	 #ifdef __cplusplus
	 extern "C" {
	 #endif
		JNIEXPORT void JNICALL Java_cs2103_util_Console_clr(JNIEnv *env, jclass thisClass){
       		HANDLE chwnd = GetStdHandle(STD_OUTPUT_HANDLE);
       		COORD coordScreen = { 0, 0 };
       		DWORD cCharsWritten;
       		{
           		WORD textColours;
           		CONSOLE_SCREEN_BUFFER_INFO *consoleInfo = new CONSOLE_SCREEN_BUFFER_INFO();
           		GetConsoleScreenBufferInfo(chwnd, consoleInfo);
           		textColours = consoleInfo->wAttributes;
           		SetConsoleTextAttribute(chwnd, textColours);
       		}
       		CONSOLE_SCREEN_BUFFER_INFO csbi;
       		DWORD dwConSize;
       		if( !GetConsoleScreenBufferInfo( chwnd, &csbi )) return;
       		dwConSize = csbi.dwSize.X * csbi.dwSize.Y;
       		if( !FillConsoleOutputCharacter( chwnd, (TCHAR) ' ', dwConSize, coordScreen, &cCharsWritten )) return;
       		if( !GetConsoleScreenBufferInfo( chwnd, &csbi )) return;
       		if( !FillConsoleOutputAttribute( chwnd, csbi.wAttributes, dwConSize, coordScreen, &cCharsWritten )) return;
       		SetConsoleCursorPosition( chwnd, coordScreen );
       		return;
		}
		
	 #ifdef __cplusplus
	 }
	 #endif
	 */
	public static native void clr();
}
