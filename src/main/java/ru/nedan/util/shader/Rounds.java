package ru.nedan.util.shader;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import lombok.experimental.UtilityClass;
import net.minecraft.client.util.Window;
import net.minecraft.util.math.Vector4f;
import org.lwjgl.opengl.GL11;
import ru.nedan.util.ColorHelper;
import ru.nedan.util.Wrapper;

import java.awt.*;

import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;

@UtilityClass
public class Rounds implements Wrapper {

    private static final ShaderUtil roundedShader = new ShaderUtil("            #version 120\n" +
            " \n" +
            "            uniform vec2 location, rectSize;\n" +
            "            uniform vec4 color;\n" +
            "            uniform float radius;\n" +
            "            uniform bool blur;\n" +
            " \n" +
            "            float roundSDF(vec2 p, vec2 b, float r) {\n" +
            "                return length(max(abs(p) - b, 0.0)) - r;\n" +
            "            }\n" +
            " \n" +
            " \n" +
            "            void main() {\n" +
            "                vec2 rectHalf = rectSize * .5;\n" +
            "                // Smooth the result (free antialiasing).\n" +
            "                float smoothedAlpha =  (1.0-smoothstep(0.0, 1.0, roundSDF(rectHalf - (gl_TexCoord[0].st * rectSize), rectHalf - radius - 1., radius))) * color.a;\n" +
            "                gl_FragColor = vec4(color.rgb, smoothedAlpha);// mix(quadColor, shadowColor, 0.0);\n" +
            " \n" +
            "            }", true);

    private static final ShaderUtil roundGradientShader = new ShaderUtil("            #version 120\n" +
            " \n" +
            "            uniform float round;\n" +
            "            uniform vec2 size;\n" +
            "            uniform vec4 color1;\n" +
            "            uniform vec4 color2;\n" +
            "            uniform vec4 color3;\n" +
            "            uniform vec4 color4;\n" +
            " \n" +
            "            float alpha(vec2 d, vec2 d1) {\n" +
            "                vec2 v = abs(d) - d1 + round;\n" +
            "                return min(max(v.x, v.y), 0.0) + length(max(v, .0f)) - round;\n" +
            "            }\n" +
            " \n" +
            "            void main() {\n" +
            "            \t vec2 coords = gl_TexCoord[0].st;\n" +
            "                vec2 centre = .5f * size;\n" +
            "                vec4 color = mix(mix(color1, color2, coords.y), mix(color3, color4, coords.y), coords.x);\n" +
            "                gl_FragColor = vec4(color.rgb, color.a * (1.f- smoothstep(0.f, 1.5f, alpha(centre - (gl_TexCoord[0].st * size), centre - 1.f))));\n" +
            "            }", true);

    private static final ShaderUtil uniqueRadiusRound = new ShaderUtil("                #version 120\n" +
            "                // объявление переменных\n" +
            "                uniform vec2 size; // размер прямоугольника\n" +
            "                uniform vec4 round; // коэффициенты скругления углов\n" +
            "                uniform vec2 smoothness; // плавность перехода от цвета к прозрачности\n" +
            "                uniform float value; // значение, используемое для расчета расстояния до границы\n" +
            "                uniform vec4 color1; // цвет прямоугольника\n" +
            "                uniform vec4 color2; // цвет прямоугольника\n" +
            "                uniform vec4 color3; // цвет прямоугольника\n" +
            "                uniform vec4 color4; // цвет прямоугольника\n" +
            "                #define NOISE .5/255.0\n" +
            " \n" +
            "                // функция для расчета расстояния до границы\n" +
            "                float test(vec2 vec_1, vec2 vec_2, vec4 vec_4) {\n" +
            "                    vec_4.xy = (vec_1.x > 0.0) ? vec_4.xy : vec_4.zw;\n" +
            "                    vec_4.x = (vec_1.y > 0.0) ? vec_4.x : vec_4.y;\n" +
            "                    vec2 coords = abs(vec_1) - vec_2 + vec_4.x;\n" +
            "                    return min(max(coords.x, coords.y), 0.0) + length(max(coords, vec2(0.0f))) - vec_4.x;\n" +
            "                }\n" +
            " \n" +
            "                vec4 createGradient(vec2 coords, vec4 color1, vec4 color2, vec4 color3, vec4 color4) {\n" +
            "                    vec4 color = mix(mix(color1, color2, coords.y), mix(color3, color4, coords.y), coords.x);\n" +
            "                    //Dithering the color\n" +
            "                    // from https://shader-tutorial.dev/advanced/color-banding-dithering/\n" +
            "                    color += mix(NOISE, -NOISE, fract(sin(dot(coords.xy, vec2(12.9898, 78.233))) * 43758.5453));\n" +
            "                    return color;\n" +
            "                }\n" +
            " \n" +
            "                void main() {\n" +
            "                    vec4 color = createGradient(gl_TexCoord[0].st, color1,color2,color3,color4);\n" +
            "                    vec2 st = gl_TexCoord[0].st * size; // координаты текущего пикселя\n" +
            "                    vec2 halfSize = 0.5 * size; // половина размера прямоугольника\n" +
            "                    float sa = 1.0 - smoothstep(smoothness.x, smoothness.y, test(halfSize - st, halfSize - value, round));\n" +
            "                    // рассчитываем прозрачность в зависимости от расстояния до границы\n" +
            " \n" +
            "                    gl_FragColor = mix(vec4(color.rgb, 0.0), vec4(color.rgb, color.a), sa); // устанавливаем цвет прямоугольника с прозрачностью sa\n" +
            "                }", true);

    public void drawRound(double x, double y, double width, double height, double radius, Color color) {
        drawRound((float) x, (float) y, (float) width, (float) height, (float) radius, false, color);
    }

    public static void drawRound(float x, float y, float width, float height, float radius, boolean blur, Color color) {
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        roundedShader.init();

        setupRoundedRectUniforms(x, y, width, height, radius, roundedShader);
        roundedShader.setUniformi("blur", blur ? 1 : 0);
        roundedShader.setUniformf("color", color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, color.getAlpha() / 255f);

        ShaderUtil.drawQuads(x - 1, y - 1, width + 2, height + 2);
        roundedShader.unload();
        GlStateManager.disableBlend();
        RenderSystem.color4f(-1, -1, -1, -1);
    }

    public static void drawRoundGradient(double x, double y, double width, double height, double radius, Color... colors) {
        float[] c = ColorHelper.getColorComps(colors[0]);
        float[] c1 = ColorHelper.getColorComps(colors[1]);
        float[] c2 = ColorHelper.getColorComps(colors[2]);
        float[] c3 = ColorHelper.getColorComps(colors[3]);

        GlStateManager.disableTexture();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        roundGradientShader.init();
        roundGradientShader.setUniformf("size", (float)width * 2, (float)height * 2);
        roundGradientShader.setUniformf("round", (float)radius * 2);
        roundGradientShader.setUniformf("color1", c[0], c[1], c[2], c[3]);
        roundGradientShader.setUniformf("color2", c1[0], c1[1], c1[2], c1[3]);
        roundGradientShader.setUniformf("color3", c2[0], c2[1], c2[2], c2[3]);
        roundGradientShader.setUniformf("color4", c3[0], c3[1], c3[2], c3[3]);
        ShaderUtil.drawQuads(x, y, width, height);
        roundGradientShader.unload();

        GlStateManager.enableTexture();
        GlStateManager.disableBlend();
        RenderSystem.color4f(-1, -1, -1, -1);
    }

    public static void drawRoundedRect(float x,
                                       float y,
                                       float width,
                                       float height,
                                       Vector4f vector4f,
                                       int[] color) {
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        uniqueRadiusRound.init();

        uniqueRadiusRound.setUniformf("size", width * 2, height * 2);
        uniqueRadiusRound.setUniformf("round", vector4f.getX() * 2, vector4f.getY() * 2, vector4f.getZ() * 2, vector4f.getW() * 2);

        uniqueRadiusRound.setUniformf("smoothness", 0.f, 1.5f);
        uniqueRadiusRound.setUniformf("color1",
                ColorHelper.rgba(color[0]));
        uniqueRadiusRound.setUniformf("color2",
                ColorHelper.rgba(color[1]));
        uniqueRadiusRound.setUniformf("color3",
                ColorHelper.rgba(color[2]));
        uniqueRadiusRound.setUniformf("color4",
                ColorHelper.rgba(color[3]));
        ShaderUtil.drawQuads(x, y, width, height);

        uniqueRadiusRound.unload();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
        RenderSystem.color4f(-1, -1, -1, -1);
    }

    private static void setupRoundedRectUniforms(float x, float y, float width, float height, float radius, ShaderUtil roundedTexturedShader) {
        Window window = mc.getWindow();

        roundedTexturedShader.setUniformf("location", (float) (x * window.getScaleFactor()),
                (float) ((window.getHeight() - (height * window.getScaleFactor())) - (y * window.getScaleFactor())));
        roundedTexturedShader.setUniformf("rectSize", (float) (width * window.getScaleFactor()), (float) (height * window.getScaleFactor()));
        roundedTexturedShader.setUniformf("radius", (float) (radius * window.getScaleFactor()));
    }

}