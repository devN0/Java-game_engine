package org.example.devloop;

import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

public class LevelEditorScene extends Scene {
    private String vertexShaderSrc = "#version 330 core\n" +
            "\n" +
            "layout (location = 0) in vec3 aPos;\n" +
            "layout (location = 1) in vec4 aColor;\n" +
            "\n" +
            "out vec4 fColor;\n" +
            "\n" +
            "void main() {\n" +
            "    fColor = aColor;\n" +
            "    gl_Position = vec4(aPos, 1.0);\n" +
            "}";
    private String fragmentShaderSrc = "#version 330 core\n" +
            "\n" +
            "in vec4 fColor;\n" +
            "\n" +
            "out vec4 color;\n" +
            "\n" +
            "void main() {\n" +
            "    color = fColor;\n" +
            "}";
    private int vertexId, fragmentId, shaderProgram; // since we're sending data from cpu to gpu. We need identifiers for these data, to know what we're working with when talking to gpu.
    private int vaoId, vboId, eboId;
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
        // COMPILE AND LINK SHADERS

        // 1. load and compile the vertex shader
        vertexId = glCreateShader(GL_VERTEX_SHADER);
        // pass the shader src to the gpu
        glShaderSource(vertexId, vertexShaderSrc);
        // compile vertex shader
        glCompileShader(vertexId);

        // Check for errors in VS compilation
        int success = glGetShaderi(vertexId, GL_COMPILE_STATUS); // returns 0 if fails
        if(success == GL_FALSE) {
            int len = glGetShaderi(vertexId, GL_INFO_LOG_LENGTH);
            System.out.println("ERROR: 'defaultShader.glsl'\n\t 'Vertex shader compilation failed.'");
            System.out.println(glGetShaderInfoLog(vertexId, len));
            assert false: "";
        }

        // 2. load and compile fragment shader
        fragmentId = glCreateShader(GL_FRAGMENT_SHADER);
        // pass the shader src to the gpu
        glShaderSource(fragmentId, fragmentShaderSrc);
        // compile fragment shader
        glCompileShader(fragmentId);

        // Check for errors in FS compilation
        success = glGetShaderi(fragmentId, GL_COMPILE_STATUS);
        if(success == GL_FALSE) {
            int len = glGetShaderi(fragmentId, GL_INFO_LOG_LENGTH);
            System.out.println("ERROR: 'defaultShader.glsl'\n\t 'Fragment shader compilation failed.'");
            System.out.println(glGetShaderInfoLog(fragmentId, len));
            assert false: "";
        }

        // 3. Link shaders
        shaderProgram = glCreateProgram();
        glAttachShader(shaderProgram, vertexId);
        glAttachShader(shaderProgram, fragmentId);
        glLinkProgram(shaderProgram);

        // check for linking errors
        success = glGetProgrami(shaderProgram, GL_LINK_STATUS);
        if(success == GL_FALSE) {
            int len = glGetProgrami(shaderProgram, GL_INFO_LOG_LENGTH);
            System.out.println("ERROR: 'defaultShader.glsl'\n\t 'Linking shaders failed.'");
            System.out.println(glGetProgramInfoLog(shaderProgram, len));
            assert false: "";
        }

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
        // Bind shader program
        glUseProgram(shaderProgram);
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
        glUseProgram(0);
    }
}
