package org.example.devloop;

import static org.lwjgl.glfw.GLFW.*;

public class MouseListener {
    private static MouseListener mouseListener;
    private double scrollX, scrollY;
    private double xPos, yPos, lastX, lastY;
    private boolean[] mouseButtonPressed = new boolean[GLFW_MOUSE_BUTTON_LAST+1]; // or use 3
    private boolean isDragging;

    private MouseListener() {
        this.scrollX = 0.0;
        this.scrollY = 0.0;
        this.xPos = 0.0;
        this.yPos = 0.0;
        this.lastX = 0.0;
        this.lastY = 0.0;
    }

    public static MouseListener getInstance() {
        if(mouseListener == null) {
            mouseListener = new MouseListener();
        }
        return mouseListener;
    }

    public static void mousePosCallback(long window, double xpos, double ypos) {
        mouseListener.lastX = mouseListener.xPos;
        mouseListener.lastY = mouseListener.yPos;
        mouseListener.xPos = xpos;
        mouseListener.yPos = ypos;
        /**
         * Note - glfw calls callbacks in the order 1) mouseButtonCallback -> 2) mousePosCallback
         * So, while in mousePosCallback if a button is pressed, then that means mouse is dragging
          */
        for(boolean button : mouseListener.mouseButtonPressed) {
            if(button) {
                mouseListener.isDragging = true;
                break;
            }
        }
//        mouseListener.isDragging = mouseListener.mouseButtonPressed[0] || mouseListener.mouseButtonPressed[1] || mouseListener.mouseButtonPressed[2];
    }

    public static void mouseButtonCallback(long window, int button, int action, int mods) { // mods ex - left click + pressing ctrl
        if(action == GLFW_PRESS) {
            if(button < mouseListener.mouseButtonPressed.length) { // to avoid considering extra mouse buttons in case a user uses a fancy mouse
                mouseListener.mouseButtonPressed[button] = true;
            }
        }
        else if(action == GLFW_RELEASE) {
            if(button < mouseListener.mouseButtonPressed.length) {
                mouseListener.mouseButtonPressed[button] = false;
                mouseListener.isDragging = false;
            }
        }
    }

    public static void mouseScrollCallback(long window, double xOffset, double yOffset) {
        mouseListener.scrollX = xOffset;
        mouseListener.scrollY = yOffset;
    }

    public static void endFrame() {
        mouseListener.scrollX = 0.0;
        mouseListener.scrollY = 0.0;
        mouseListener.lastX = mouseListener.xPos; // this will make our differentials zero i.e. xPos - lastX = 0;
        mouseListener.lastY = mouseListener.yPos;
    }

    //GETTERS
    public static float getX() {
        return (float)mouseListener.xPos;
    }

    public static float getY() {
        return (float)mouseListener.yPos;
    }

    public static float getScrollX() { // some mouses have x scrolling too.
        return (float)mouseListener.scrollX;
    }

    public static float getScrollY() {
        return (float)mouseListener.scrollY;
    }

    public static float getDx() {
        return (float)(mouseListener.lastX - mouseListener.xPos);
    }

    public static float getDy() {
        return (float)(mouseListener.lastY - mouseListener.yPos);
    }

    public static boolean isDragging() {
        return mouseListener.isDragging;
    }

    public static boolean mouseButtonDown(int button) {
        if(button < mouseListener.mouseButtonPressed.length) {
            return mouseListener.mouseButtonPressed[button];
        } else {
            return false;
        }
    }
}
