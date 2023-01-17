package org.example.devloop;

import java.awt.event.KeyEvent;

public class LevelScene extends Scene {
    private float timeToChangeScene = 2.0f;
    private boolean changingScene = false;
    public LevelScene() {
        System.out.println("Inside Level scene");
    }
    @Override
    public void update(float dt) {
        if(!changingScene && KeyListener.isKeyPressed(KeyEvent.VK_SPACE)) {
            changingScene = true;
        }
        if(changingScene && timeToChangeScene > 0) {
            timeToChangeScene -= dt;
            Window.getInstance().fadeToWhite(dt);
        } else if(changingScene) {
            Window.changeScene(0); // this will change scene to level editor scene.
            changingScene = false;
        }
    }
}
