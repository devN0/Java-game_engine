package org.example.devloop;

import static org.lwjgl.glfw.GLFW.*;

public class KeyListener {
    private static KeyListener keyListener;
    private boolean[] keyPressed = new boolean[GLFW_KEY_LAST+1]; // or 350

    private KeyListener(){}

    public static KeyListener getInstance() {
        if(keyListener == null) {
            keyListener = new KeyListener();
        }
        return keyListener;
    }

    public static void keyCallback(long window, int key, int scancode, int action, int mods) {
        if(action == GLFW_PRESS) {
            keyListener.keyPressed[key] = true;
        } else if(action == GLFW_RELEASE) {
            keyListener.keyPressed[key] = false;
        }
    }

    public static boolean isKeyPressed(int key) {
        return keyListener.keyPressed[key];
    }
}
