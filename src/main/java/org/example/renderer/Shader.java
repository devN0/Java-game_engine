package org.example.renderer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL20.glGetShaderInfoLog;

public class Shader {
    private int shaderProgramId;
    private String vertexSource;
    private String fragmentSource;
    private String filePath;

    public Shader(String filePath) {
        this.filePath = filePath;
        try {
            String source = new String(Files.readAllBytes(Paths.get(filePath)));
            String[] splitString = source.split("(#type)( )+([a-zA-Z]+)"); // splits until end of line, then store all upcoming lines in index 1 until another match is found at which point stores all the upcoming lines in index 2 and so on.

            int index = source.indexOf("#type") + 6;
            int eol = source.indexOf("\r\n", index); // index of line-break starting from index
            String firstPattern = source.substring(index, eol).trim();

            index = source.indexOf("#type", eol) + 6; // index of first occurrence of "#type" starting from eol
            eol = source.indexOf("\r\n", index);
            String secondPattern = source.substring(index, eol).trim();

            if(firstPattern.equals("vertex")) {
                vertexSource = splitString[1];
            } else if(firstPattern.equals("fragment")) {
                fragmentSource = splitString[1];
            } else {
                throw new IOException("Unexpected token: " + firstPattern + " in source: " + filePath);
            }

            if(secondPattern.equals("vertex")) {
                vertexSource = splitString[2];
            } else if(secondPattern.equals("fragment")) {
                fragmentSource = splitString[2];
            } else {
                throw new IOException("Unexpected token: " + secondPattern + " in source: " + filePath);
            }
        } catch (IOException e) {
            e.printStackTrace();
            assert false : "Error: Could not open file for shader: '"+filePath+"'";
        }

        System.out.println(vertexSource);
        System.out.println(fragmentSource);
    }

    // COMPILE AND LINK SHADERS
    public void compile() {
        int vertexId, fragmentId; // since we're sending data from cpu to gpu. We need identifiers for these data, to know what we're working with when talking to gpu.

        // 1. load and compile the vertex shader
        vertexId = glCreateShader(GL_VERTEX_SHADER);
        // pass the shader src to the gpu
        glShaderSource(vertexId, vertexSource);
        // compile vertex shader
        glCompileShader(vertexId);

        // Check for errors in VS compilation
        int success = glGetShaderi(vertexId, GL_COMPILE_STATUS); // returns 0 if fails
        if(success == GL_FALSE) {
            int len = glGetShaderi(vertexId, GL_INFO_LOG_LENGTH);
            System.out.println("ERROR: '" + filePath + "'\n\t 'Vertex shader compilation failed.'");
            System.out.println(glGetShaderInfoLog(vertexId, len));
            assert false: "";
        }

        // 2. load and compile fragment shader
        fragmentId = glCreateShader(GL_FRAGMENT_SHADER);
        // pass the shader src to the gpu
        glShaderSource(fragmentId, fragmentSource);
        // compile fragment shader
        glCompileShader(fragmentId);

        // Check for errors in FS compilation
        success = glGetShaderi(fragmentId, GL_COMPILE_STATUS);
        if(success == GL_FALSE) {
            int len = glGetShaderi(fragmentId, GL_INFO_LOG_LENGTH);
            System.out.println("ERROR: '" + filePath + "'\n\t 'Fragment shader compilation failed.'");
            System.out.println(glGetShaderInfoLog(fragmentId, len));
            assert false: "";
        }

        // 3. Link shaders
        shaderProgramId = glCreateProgram();
        glAttachShader(shaderProgramId, vertexId);
        glAttachShader(shaderProgramId, fragmentId);
        glLinkProgram(shaderProgramId);

        // check for linking errors
        success = glGetProgrami(shaderProgramId, GL_LINK_STATUS);
        if(success == GL_FALSE) {
            int len = glGetProgrami(shaderProgramId, GL_INFO_LOG_LENGTH);
            System.out.println("ERROR: '" + filePath + "'\n\t 'Linking shaders failed.'");
            System.out.println(glGetProgramInfoLog(shaderProgramId, len));
            assert false: "";
        }
    }

    public void use() {
        // Bind shader program
        glUseProgram(shaderProgramId);
    }

    public void detach() {
        // Bind shader program
        glUseProgram(0);
    }

}
