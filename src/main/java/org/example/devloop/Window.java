package org.example.devloop;

import org.example.util.Time;
import org.lwjgl.Version;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Window {
    private int width, height;
    private String title;
    private static Window window;
    private long glfwWindow;
    private float r, g, b, a;
    private static Scene currentScene;

    private Window() {
        this.width = 1920;
        this.height = 1080;
        this.title = "Finmuts";
        this.r = 1.0f;
        this.g = 1.0f;
        this.b = 1.0f;
        this.a = 1.0f;
    }

    public static Window getInstance() {
        if(window == null) {
            window = new Window();
        }
        return window;
    }

    public void fadeToBlack(float dt) {
        r -= dt*5.0f;
        g -= dt*5.0f;
        b -= dt*5.0f;
    }
    public void fadeToWhite(float dt) {
        r += dt*5.0f;
        g += dt*5.0f;
        b += dt*5.0f;
    }

    public static void changeScene(int newScene) {
        switch (newScene) {
            case 0:
                currentScene = new LevelEditorScene();
                currentScene.init();
                break;
            case 1:
                currentScene = new LevelScene();
                currentScene.init();
                break;
            default:
                assert false : "unknown level "+newScene;
                break;
        }
    }

    // HELPER FUNCTIONS
    public void run() {
        System.out.println("Hello LWJGL " + Version.getVersion() + "!");

        init();
        loop();

        // Free the window callbacks and destroy the window
        glfwFreeCallbacks(glfwWindow);
        glfwDestroyWindow(glfwWindow);

        // Terminate GLFW and free the error callback
        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }

    public void init() {
        // Set up an error callback - define where all GLFW errors will be thrown
        GLFWErrorCallback.createPrint(System.err).set();

        // Initialize GLFW
        if(!glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }

        // Configure GLFW
        glfwDefaultWindowHints(); // optional, the current window hints are already the default
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // the window will stay hidden after creation
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE); // the window will be resizable
        glfwWindowHint(GLFW_MAXIMIZED, GLFW_TRUE);

        // Create the window
        glfwWindow = glfwCreateWindow(this.width, this.height, this.title, NULL, NULL);

        if(glfwWindow == NULL) {
            throw new RuntimeException("Failed to create the GLFW window");
        }

        // REGISTERING CALLBACKS

        // Cursor callbacks
        MouseListener.getInstance();
        glfwSetCursorPosCallback(glfwWindow, MouseListener::mousePosCallback);
        glfwSetMouseButtonCallback(glfwWindow, MouseListener::mouseButtonCallback);
        glfwSetScrollCallback(glfwWindow, MouseListener::mouseScrollCallback);
        // Key callbacks
        KeyListener.getInstance();
        glfwSetKeyCallback(glfwWindow, KeyListener::keyCallback);

        // Make the OpenGL context current
        glfwMakeContextCurrent(glfwWindow);
        // Enable v-sync: swap the frames every single second and there's no wait time between the frames
        glfwSwapInterval(1);

        // Make the window visible
        glfwShowWindow(glfwWindow);

        // This line is critical for LWJGL's interoperation with GLFW's
        // OpenGL context, or any context that is managed externally.
        // LWJGL detects the context that is current in the current thread,
        // creates the GLCapabilities instance and makes the OpenGL
        // bindings available for use.
        GL.createCapabilities();

        // To make sure that we're in a scene before the game starts
        Window.changeScene(0); // Here we'll start with LevelEditorScene
    }

    public void loop() {
        float beginTime = Time.getTime();
        float endTime;
        float dt = -1.0f;

        // Run the rendering loop until the user has attempted to close
        // the window or has pressed the ESCAPE key.
        while(!glfwWindowShouldClose(glfwWindow)) {
            glfwPollEvents();

            // Set the clear color
            glClearColor(r, g, b, a);

            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer

            if(dt >= 0) {
                currentScene.update(dt);
            }

            glfwSwapBuffers(glfwWindow); // swap the color buffers

            endTime = Time.getTime();
            dt = endTime - beginTime; // DELTA TIME VARIABLE - tells the time taken per frame
            beginTime = endTime;
        }
    }
}
