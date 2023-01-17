package org.example.util;

public class Time {
    public static float timeStarted = System.nanoTime();
    // getTime() returns the time lapsed since the game started
    public static float getTime() {
        return (float)((System.nanoTime()-timeStarted)*1E-9); //converting from nanoseconds to seconds
    }
}
