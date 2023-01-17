package org.example.devloop;

import java.awt.event.KeyEvent;

public class LevelEditorScene extends Scene {
    private boolean changingScene = false;
    private float timeToChangeScene = 2.0f;

    public LevelEditorScene() {
        System.out.println("Inside Level editor scene");
    }
    @Override
    public void update(float dt) {
        if(!changingScene && KeyListener.isKeyPressed(KeyEvent.VK_SPACE)) {
            changingScene = true;
        }
        if(changingScene && timeToChangeScene > 0) {
            timeToChangeScene -= dt;
            Window.getInstance().fadeToBlack(dt);
        } else if(changingScene) {
            Window.changeScene(1); // this will change scene to level scene.
            changingScene = false;
        }
    }
}
