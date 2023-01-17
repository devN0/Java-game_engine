package org.example;

import org.example.devloop.Window;

public class Main {
    public static void main(String[] args) {
        Window window = Window.getInstance();
        window.run();
    }
}