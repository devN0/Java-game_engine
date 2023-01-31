package org.example.devloop;

import org.example.renderer.Shader;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

public class LevelEditorScene extends Scene {
    private int vaoId, vboId, eboId;
    private Shader defaultShader;
    private float[] vertexArray = {
            // (we've to pass them in normalized device coordinates)
            // i.e. left side of scree = -1, right side = 1

            //let's build a square
            //position(xyz)        color(rgba)
            0.5f, -0.5f, 0.0f,     1.0f, 0.0f, 0.0f, 1.0f, // bottom right red
            -0.5f, 0.5f, 0.0f,     0.0f, 1.0f, 0.0f, 1.0f, // top left green
            0.5f, 0.5f, 0.0f,      0.0f, 0.0f, 1.0f, 1.0f, // top right blue
            -0.5f, -0.5f, 0.0f,    1.0f, 1.0f, 0.0f, 1.0f, // bottom left yellow
    };

    // order of coordinates indices in elementArray must be in counter-clockwise
    private int[] elementArray = {
        // our square will consist of two triangles
            2, 1, 0, // top-right triangle - TR, TL, BR
            0, 1, 3 // bottom-left triangle - BR, TL, BL
    };

    public LevelEditorScene() {
    }

    @Override
    public void init() {
        defaultShader = new Shader("assets/shaders/default.glsl");
        defaultShader.compile();

        // Generate VAO, VBO and EBO buffer objects and send to GPU
        vaoId = glGenVertexArrays(); // generating a new vertex array inside GPU and giving its unique identifier
        glBindVertexArray(vaoId); // bind is telling to use this

        // create a float buffer of vertices from vertexArray as openGL expects a float buffer
        FloatBuffer vertexBuffer = BufferUtils.createFloatBuffer(vertexArray.length);
        vertexBuffer.put(vertexArray).flip(); // to make sure that it's oriented correctly for OpenGL

        // Create VBO, upload the vertex buffer
        vboId = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboId);
        glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_STATIC_DRAW);

        // Create an int buffer of indices
        IntBuffer elementBuffer = BufferUtils.createIntBuffer(elementArray.length);
        elementBuffer.put(elementArray).flip();

        // Create EBO and upload the element buffer
        eboId = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboId);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, elementBuffer, GL_STATIC_DRAW);

        // Add the vertex attribute pointers
        int positionsSize = 3;
        int colorsSize = 4;
        int floatSizeBytes = 4;
        int vertexSizeBytes = (positionsSize + colorsSize) * floatSizeBytes;

        glVertexAttribPointer(0, positionsSize, GL_FLOAT, false, vertexSizeBytes, 0);
        glEnableVertexAttribArray(0);

        glVertexAttribPointer(1, colorsSize, GL_FLOAT, false, vertexSizeBytes, positionsSize * floatSizeBytes);
        glEnableVertexAttribArray(1);
    }

    @Override
    public void update(float dt) {
        defaultShader.use();

        // Bind the VAO we're using
        glBindVertexArray(vaoId);
        //Enable vertex attribute pointers
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);

        glDrawElements(GL_TRIANGLES, elementArray.length, GL_UNSIGNED_INT, 0);

        // Unbinding everything
        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
        glBindVertexArray(0);

        defaultShader.detach();
    }
}
