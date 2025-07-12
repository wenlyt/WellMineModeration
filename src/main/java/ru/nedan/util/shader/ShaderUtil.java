package ru.nedan.util.shader;

import net.minecraft.client.util.Window;
import net.minecraft.util.Identifier;
import org.lwjgl.opengl.ARBShaderObjects;
import org.lwjgl.opengl.GL30;
import ru.nedan.util.Wrapper;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL20.*;

public class ShaderUtil implements Wrapper {
    private int programID;
    private String name;

    public ShaderUtil(String str, boolean glsl) {
        try {
            int program = glCreateProgram();

            InputStream is = glsl ? new ByteArrayInputStream(str.getBytes()) : mc.getResourceManager().getResource(new Identifier("neverclient", "shader/" + str)).getInputStream();

            int fragmentShaderID = createShader(is, GL_FRAGMENT_SHADER);

            glAttachShader(program, fragmentShaderID);

/*            String vertex = """
                    #version 120
 
                    void main() {
                        gl_TexCoord[0] = gl_MultiTexCoord0;
                        gl_Position = gl_ModelViewProjectionMatrix * gl_Vertex;
                    }""";*/

            String vertex = "#version 120\n" +
                    " \n" +
                    "                    void main() {\n" +
                    "                        gl_TexCoord[0] = gl_MultiTexCoord0;\n" +
                    "                        gl_Position = gl_ModelViewProjectionMatrix * gl_Vertex;\n" +
                    "                    }";

            int vertexShaderID = createShader(new ByteArrayInputStream(vertex.getBytes()), GL_VERTEX_SHADER);
            glAttachShader(program, vertexShaderID);

            glLinkProgram(program);
            int status = glGetProgrami(program, GL_LINK_STATUS);

            if (status == 0) {
                System.out.println("Failed to link!");
            }

            this.programID = program;
        } catch (Exception ex) {
            ex.printStackTrace(System.err);
        }
    }

    public void unload() {
        glUseProgram(0);
    }

    public int getUniform(String name) {
        return glGetUniformLocation(programID, name);
    }

    public void setUniformf(String name, float... args) {
        int loc = ARBShaderObjects.glGetUniformLocationARB(programID, name);
        switch (args.length) {
            case 1:
                ARBShaderObjects.glUniform1fARB(loc, args[0]);
                break;
            case 2:
                ARBShaderObjects.glUniform2fARB(loc, args[0], args[1]);
                break;
            case 3:
                ARBShaderObjects.glUniform3fARB(loc, args[0], args[1], args[2]);
                break;
            case 4:
                ARBShaderObjects.glUniform4fARB(loc, args[0], args[1], args[2], args[3]);
                break;
            default:
                break;
        }
    }

    public void setUniformi(String name, int... args) {
        int loc = glGetUniformLocation(programID, name);
        if (args.length > 1) glUniform2i(loc, args[0], args[1]);
        else glUniform1i(loc, args[0]);
    }

    public void setUniformfb(String name, FloatBuffer buffer) {
        GL30.glUniform1fv(GL30.glGetUniformLocation(programID, name), buffer);
    }

    public static void drawQuads() {
        Window window = mc.getWindow();

        float width = (float) window.getScaledWidth();
        float height = (float) window.getScaledHeight();
        glBegin(GL_QUADS);
        glTexCoord2f(0, 1);
        glVertex2f(0, 0);
        glTexCoord2f(0, 0);
        glVertex2f(0, height);
        glTexCoord2f(1, 0);
        glVertex2f(width, height);
        glTexCoord2f(1, 1);
        glVertex2f(width, 0);
        glEnd();
    }

    public static void drawQuads(float x, float y, float width, float height) {
        glBegin(GL_QUADS);
        glTexCoord2f(0, 0);
        glVertex2f(x, y);
        glTexCoord2f(0, 1);
        glVertex2f(x, y + height);
        glTexCoord2f(1, 1);
        glVertex2f(x + width, y + height);
        glTexCoord2f(1, 0);
        glVertex2f(x + width, y);
        glEnd();
    }

    public static void drawQuads(double x, double y, double width, double height) {
        glBegin(GL_QUADS);
        glTexCoord2f(0, 0);
        glVertex2d(x, y);
        glTexCoord2f(0, 1);
        glVertex2d(x, y + height);
        glTexCoord2f(1, 1);
        glVertex2d(x + width, y + height);
        glTexCoord2f(1, 0);
        glVertex2d(x + width, y);
        glEnd();
    }

    private int createShader(InputStream inputStream, int shaderType) {
        int shader = glCreateShader(shaderType);
        glShaderSource(shader, readInputStream(inputStream));
        glCompileShader(shader);

        if (glGetShaderi(shader, GL_COMPILE_STATUS) == 0) {
            System.out.println(this.name + ": " + glGetShaderInfoLog(shader, 4096));
            throw new IllegalStateException(String.format("Shader (%s) failed to compile!", shaderType));
        }

        return shader;
    }

    public static String readInputStream(InputStream inputStream) {
        StringBuilder stringBuilder = new StringBuilder();

        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = bufferedReader.readLine()) != null)
                stringBuilder.append(line).append('\n');

        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
        return stringBuilder.toString();
    }

    public void init() {
        glUseProgram(programID);
    }

}