#!/bin/sh
# Can only be compiled under cygwin64 with mingw w64 gcc package installed. JDK path is absolute hence might need editing before compilation
x86_64-w64-mingw32-g++ -Wall -march=x86-64 -static-libgcc -static-libstdc++ -D_JNI_IMPLEMENTATION_ -Wl,--kill-at -I"C:/Program Files/Java/jdk1.8.0_20/include" -I"C:/Program Files/Java/jdk1.8.0_20/include/win32" -shared cs2103_util_Console.c -o ConsoleClear_x64.dll
i686-w64-mingw32-g++ -Wall -march=i686 -static-libgcc -static-libstdc++ -D_JNI_IMPLEMENTATION_ -Wl,--kill-at -I"C:/Program Files/Java/jdk1.8.0_20/include" -I"C:/Program Files/Java/jdk1.8.0_20/include/win32" -shared cs2103_util_Console.c -o ConsoleClear_x86.dll
upx -9 ConsoleClear_x64.dll
upx -9 ConsoleClear_x86.dll